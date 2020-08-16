package com.orienteering.handrail.activities

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.util.IOUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.controllers.CourseController
import com.orienteering.handrail.dialogs.*
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.ImageSelect
import com.orienteering.handrail.utilities.PermissionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CreateMapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, ExampleDialogListener, EventDialogListener {


    /**
     * Variables
     */

    companion object {
        //TAG variable for log
        private val TAG = CreateMapsActivity::class.qualifiedName
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    // image select
    val imageSelect = ImageSelect(this, this)

    // dialog for control create
    val createControlDialog: CreateControlDialog = CreateControlDialog()

    // GoogleMap variable
    private lateinit var map: GoogleMap

    // Fused Location Prover Client Variable
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Last Location
    private lateinit var lastLocation: Location

    // location call back to preoperty to update location
    private lateinit var locationCallback: LocationCallback

    // location request and updated location state
    // store parameters for requests to fused location provider (determining level of accuracy)
    private lateinit var locationRequest: LocationRequest

    // location update state
    private var locationUpdateState = false

    // map fragment
    private lateinit var mapFragment: SupportMapFragment

    // course Controller to initiate service calls
    lateinit var courseController: CourseController

    // potential control latlng
    var potentialLatLng: LatLng = LatLng(0.0, 0.0)

    // potential control altitude
    var potentialAltitude: Double = 0.0

    // hashmap for image files
    var fileUris: HashMap<Int, Uri> = HashMap()

    // course for controls
    var controlsForCourse: MutableList<Control> = mutableListOf<Control>()

    //coordinates array for polyline
    val listOfRoutePoints: MutableList<LatLng> = mutableListOf()

    // callback create course
    val createCourseCallback = object :
        Callback<StatusResponseEntity<Course>> {
        override fun onFailure(call: Call<StatusResponseEntity<Course>?>, t: Throwable) {
            Log.e(TAG, "Failure connecting successfully")
            Log.e(TAG, Log.getStackTraceString(t))
            val toast = Toast.makeText(
                this@CreateMapsActivity,
                "Connection Failure, please try again later",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }

        override fun onResponse(
            call: Call<StatusResponseEntity<Course>?>,
            response: Response<StatusResponseEntity<Course>?>
        ) {
            if (response.isSuccessful) {
                Log.e(TAG, "Success adding Course")
                val toast = Toast.makeText(
                    this@CreateMapsActivity,
                    "Success adding Course.",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            } else {
                Log.e(TAG, "Failure adding Course")
                val toast = Toast.makeText(
                    this@CreateMapsActivity,
                    "Failure to create course, try again. If problem persists, please contact admin.",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }

    /**
     * Creates Fragments, maps buttons and adds listeners
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.orienteering.handrail.R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_course_create) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //create fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // initialise course Controller
        this.courseController = CourseController()

        createButtons()

        // fused location provider invokes the LocationCallback.onLocationResult() method. Incoming argument contains a  Locaiton obkect containing location's lat and lng
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                listOfRoutePoints.add(currentLatLng)
                createPolyLine(listOfRoutePoints)
            }
        }

        //call create location requests, add marker to new locatin
        createLocationRequest()
    }

    /**
     * Creates buttons and event listeners

     */
    fun createButtons() {
        //map buttons and add listeners
        val btnAddMarker = findViewById<Button>(com.orienteering.handrail.R.id.btn_add_marker)
        val btnSaveCourse = findViewById<Button>(com.orienteering.handrail.R.id.btn_save_course)

        btnAddMarker.setOnClickListener() {
            if (saveLatLng()) {
                if (PermissionManager.checkPermission(
                        this, this@CreateMapsActivity,
                        arrayOf(
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE
                    )
                ) {
                    createControlDialog.show(mapFragment.childFragmentManager, "ExampleDialog")
                }

            }
        }
        btnSaveCourse.setOnClickListener() { openSaveEventDialog() }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.setZoomControlsEnabled(false)
        map.uiSettings.setCompassEnabled(false)
        map.uiSettings.setMyLocationButtonEnabled(false)
        map.uiSettings.setAllGesturesEnabled(false)
        map.setOnMarkerClickListener(this)
        setUpMap()
    }

    /**
     * Save current latlng for potential control
     * @return
     */
    private fun saveLatLng(): Boolean {
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                potentialAltitude = lastLocation.altitude
                potentialLatLng = LatLng(location.latitude, location.longitude)
            } else {
                Log.e(TAG, "Save LatLng Failure -> Location = null")
                Toast.makeText(
                    this@CreateMapsActivity,
                    "Unable to find current location",
                    Toast.LENGTH_SHORT
                ).show()
                potentialLatLng = LatLng(0.0, 0.0)
            }
        }
        if (potentialLatLng.latitude != 0.0 && potentialLatLng.longitude != 0.0) {
            return true
        } else {
            return false
        }
    }


    /**
     * Request fine location, find current location and locate device
     */
    private fun setUpMap() {
        if (PermissionManager.checkPermission(this, this@CreateMapsActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)) {
            map.isMyLocationEnabled = true
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN

            // step 2 to display location
            // provide most recent location
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

                // step 3 to display location
                // get last known location
                if (location != null) {

                    lastLocation = location

                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                } else {
                    // cannot find location
                    Log.e(TAG, "Unable to find location -> Location = null")
                    Toast.makeText(
                        this@CreateMapsActivity,
                        "Unable to find current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val nameOfMarker: String? = marker?.title
        var positionOfMarker: Int? = null
        var imageUriOfMarker: Uri? = null
        var noteOfMarket: String? = null

        for (control in controlsForCourse) {
            if (control.controlName.equals(nameOfMarker)) {
                positionOfMarker = control.controlPosition
                noteOfMarket = control.controlNote
                if (fileUris.containsKey(positionOfMarker)) {
                    imageUriOfMarker = fileUris.get(positionOfMarker)
                }
            }
        }

        val markerDialog: ViewMarkerDialog =
            ViewMarkerDialog(nameOfMarker, noteOfMarket, positionOfMarker, imageUriOfMarker)
        markerDialog.show(mapFragment.childFragmentManager, "ViewMarkerDialog")

        return false
    }

    override fun onPolylineClick(p0: Polyline?) {
        TODO("Not yet implemented")
    }

    override fun onPolygonClick(p0: Polygon?) {
        TODO("Not yet implemented")
    }

    private fun createPolyLine(listOfRoutePoints: MutableList<LatLng>) {

        val pattern: MutableList<PatternItem> = mutableListOf()
        pattern.add(Dot())
        pattern.add(Gap(20F))
        pattern.add(Dash(30F))
        pattern.add(Gap(20F))

        map.addPolyline(
            PolylineOptions()
                .addAll(listOfRoutePoints)
                .width(10f)
                .color(Color.RED)
                .pattern(pattern)
        )
    }

    /**
     * Add Marker to map
     */
    fun placeControlMarkerOnMap(name: String, note: String, currentLatLng: LatLng) {
        // create marker options that uses user location
        val markerOptions = MarkerOptions().position(currentLatLng)
        // add marker name
        markerOptions.title(name)
        // add marker to map
        map.addMarker(markerOptions)
    }

    fun openSaveEventDialog() {
        val courseDialog: CreateCourseDialog = CreateCourseDialog()
        courseDialog.show(mapFragment.childFragmentManager, "CreateEventDialog")
    }

    /**
     * Save placed marker details to course variable
     */
    fun createControl(name: String, note: String) {
        val calendar = java.util.Calendar.getInstance()
        val date = calendar.time
        val control = Control(
            name,
            note,
            potentialLatLng.latitude,
            potentialLatLng.longitude,
            potentialAltitude, date
        )
        controlsForCourse.add(control)
        control.controlPosition = controlsForCourse.size
        placeControlMarkerOnMap(name, note, potentialLatLng)
    }

    /**
     * create an event using variable course and passed through name and note
     *
     * @param name
     */
    fun createCourse(name: String) {
        val course = Course(controlsForCourse, name)
        var files: Array<MultipartBody.Part?> = arrayOfNulls(controlsForCourse.size)
        for ((key, value) in fileUris) {
            val multipartBodyPart: MultipartBody.Part = createImageMultipartBody(value)
            val position = key
            val positionMinusOne = position - 1
            files.set(positionMinusOne, multipartBodyPart)
        }
        courseController.uploadCourse(
            App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),
            course,
            files,
            createCourseCallback
        )
    }

    /**
     * Create event return text from dialog
     * @name
     * @note
     */
    override fun applyEventText(name: String, note: String) {
        createCourse(name)
    }

    /**
     * Handles passed through text from dialog
     * Deprecated to createControl
     */
    override fun applyText(name: String, note: String) {
        createControl(name, note)
        imageSelect.selectImage()
    }

    /**
     * override AppCompatAcitivy's onActivityResult() method and start update request if result_ok under request check settings
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
        if (requestCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1001 -> {
                    Log.e(TAG, "Request 1001")
                    if (resultCode == Activity.RESULT_OK) {
                        Log.e(TAG, "Result ok, data not null")

                        val imageUri = imageSelect.tempImageUri

                        if (imageUri != null) {
                            fileUris.put(controlsForCourse.size, imageUri)
                        } else {
                            Log.e(TAG, "Image Uri is null")
                        }
                    } else {
                        Log.e(TAG, "Result: $resultCode  Data: $data")
                    }
                }
                1002 -> {
                    val permission = imageSelect.checkExternalStoragePermission()
                    Log.e("FileWriter", "Permission check = $permission")

                    Log.e(TAG, "Request 1002")
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Log.e(TAG, "result ok and data doesn't equal null")

                        val imageUri = data.data
                        if (imageUri != null) {
                            fileUris.put(controlsForCourse.size, imageUri)
                        }
                    }
                }
            }
        } else {
            Log.e(TAG, "Request cancelled...")
        }
    }


    /**
     * Create multipart file image
     *
     * @param fileUri
     * @return
     */
    fun createImageMultipartBody(fileUri: Uri): MultipartBody.Part {

        if (fileUri.toString()[0].equals('c')) {
            Log.e(TAG,"I START WITH C")
            var inputStream: InputStream? = contentResolver.openInputStream(fileUri)
            var file = File(fileUri.toString())
            try {
                val timeStamp : String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
                var cachedFile: File = File(cacheDir, "JPEG_${timeStamp}.jpg")
                try {
                    var outputStream: OutputStream = FileOutputStream(cachedFile)
                    IOUtils.copyStream(inputStream, outputStream)
                } catch (f: FileNotFoundException) {
                    Log.e(TAG, f.printStackTrace().toString())
                } catch (i: IOException) {
                    Log.e(TAG, i.printStackTrace().toString())
                }
                val requestBody: RequestBody = RequestBody.create(contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() }, cachedFile)
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", cachedFile.name, requestBody)
                return body
            } catch (i: IOException) {
                Log.e(TAG, i.printStackTrace().toString())
            }
        }
        var file = File(imageSelect.getImagePath(fileUri))
        val requestBody : RequestBody = RequestBody.create(contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() },file)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file",file.name,requestBody)
        return body
    }

    /**
     * override onPause() to stop location update request
     *
     */
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * override onResume to restart location updates
     *
     */
    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    /**
     * Start Location Updates
     */
    private fun startLocationUpdates() {
        if (PermissionManager.checkPermission(
                this,
                this@CreateMapsActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionManager.LOCATION_PERMISSION_REQUEST_CODE
            )
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            Log.e(TAG, "Unable to find location -> Location = null")
            Toast.makeText(
                this@CreateMapsActivity,
                "Unable to find current location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Update location
     */
    private fun createLocationRequest() {
        // create instance of locationRequest, add to instance of locationSettingsRequest.Builder and get and deal with any changes to be made based on current state of location settings
        locationRequest = LocationRequest()
        // rate at which we will get updates
        locationRequest.interval = 10000
        //fastestInterval provides the fastest rate we can handle updates. It places a limit on how often updates will be sent.
        locationRequest.fastestInterval = 5000
        // high accuracy more likely to use GPS than wifi and cell
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // adds one request to builder to get location
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        //check whether current location settings are satisfied
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        //when task completes, app can check location settings by looking at status code from LocationSettingsResponse object
        //update locationUpdateState and startLocationUpdates()
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        // on failure (location settings) eg locaton settings being turned off, try a fix
        task.addOnFailureListener { e ->

            if (e is ResolvableApiException) {
                // show a user dialogue by calling startResolutionForResult()
                // check result in onActivityResult()
                try {
                    e.startResolutionForResult(
                        this@CreateMapsActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore
                }
            }
        }
    }
}