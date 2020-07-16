package com.orienteering.handrail.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.common.api.ResolvableApiException

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.orienteering.handrail.classes.Control
import com.orienteering.handrail.classes.Course

import com.orienteering.handrail.dialogs.CreateCourseDialog
import com.orienteering.handrail.dialogs.EventDialogListener
import com.orienteering.handrail.dialogs.CreateControlDialog
import com.orienteering.handrail.dialogs.ExampleDialogListener

import com.orienteering.handrail.utilities.CourseCreator
import com.orienteering.handrail.utilities.GPXBuilder
import com.orienteering.handrail.utilities.PermissionManager


class CreateMapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener,
    ExampleDialogListener, EventDialogListener {


    /**
     * Variables
     */

    companion object{
        //TAG variable for log
        private val TAG = CreateMapsActivity::class.qualifiedName
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    // GoogleMap variable
    private lateinit var map: GoogleMap

    // Fused Location Prover Client Variable
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    // Last Location
    private lateinit var lastLocation : Location

    // location call back to preoperty to update location
    private lateinit var locationCallback: LocationCallback

    // location request and updated location state
    // store parameters for requests to fused location provider (determining level of accuracy)
    private lateinit var locationRequest: LocationRequest

    // location update state
    private var locationUpdateState = false

    // map fragment
    private lateinit var mapFragment: SupportMapFragment

    //control position
    var controlPosition : Int = 0

    // course for controls
    var controlsForCourse = mutableListOf<Control>()

    //coordinates array for polyline
    val listOfRoutePoints: MutableList<LatLng> = mutableListOf()

    //Image capture codes
    private val IMAGE_CAPTURE_CODE = 1001
    private val PICK_IMAGE_CODE = 1002


    /**
     * Creates Fragments, maps buttons and adds listeners
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.orienteering.handrail.R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
                .findFragmentById(com.orienteering.handrail.R.id.map_course_create) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //create fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //map buttons and add listeners
        val btnAddMarker = findViewById<Button>(com.orienteering.handrail.R.id.btn_add_marker)
        val btnSaveCourse = findViewById<Button>(com.orienteering.handrail.R.id.btn_save_course)


        btnAddMarker.setOnClickListener(){
                Log.e(TAG,"BUTTON PRESSED")
                openDialog()
        }


        btnSaveCourse.setOnClickListener() {
            Log.e(TAG,"Save pressed")

            openEventDialog()


        }

        // fused location provider invokes the LocationCallback.onLocationResult() method. Incoming argument contains a  Locaiton obkect containing location's lat and lng
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                val currentLatLng = LatLng(lastLocation.latitude,lastLocation.longitude)

                listOfRoutePoints.add(currentLatLng)
                createPolyLine(listOfRoutePoints)
            }
        }

        //call create location requests, add marker to new locatin
        createLocationRequest()
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

        setUpMap()
    }

    /**
     * Request fine location, find current location and locate device
     */
    private fun setUpMap(){
        if(PermissionManager.checkPermission(this,this@CreateMapsActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)
        ){
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

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                } else {
                    // cannot find location
                    Log.e(TAG,"Unable to find location -> Location = null")
                    Toast.makeText(this@CreateMapsActivity, "Unable to find current location", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }

    override fun onPolylineClick(p0: Polyline?) {
        TODO("Not yet implemented")
    }

    override fun onPolygonClick(p0: Polygon?) {
        TODO("Not yet implemented")
    }
    private fun createPolyLine(listOfRoutePoints : MutableList<LatLng>){

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
    fun placeControlMarkerOnMap(name : String, note : String, currentLatLng: LatLng){
        // create marker options that uses user location
        val markerOptions = MarkerOptions().position(currentLatLng)

        // add marker name
        markerOptions.title(name)

        // add marker to map
        map.addMarker(markerOptions)
    }

    fun openDialog(){
        val createControlDialog : CreateControlDialog = CreateControlDialog()
        createControlDialog.show(mapFragment.childFragmentManager,"ExampleDialog")
    }

    fun openEventDialog(){
        val courseDialog : CreateCourseDialog = CreateCourseDialog()
        courseDialog.show(mapFragment.childFragmentManager,"CreateEventDialog")
    }

    /**
     * Save placed marker details to course variable
     */
    fun createControl(name : String, note : String){

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if(location != null){

                lastLocation = location

                val currentLatLng = LatLng(location.latitude,location.longitude)
                placeControlMarkerOnMap(name, note, currentLatLng)

                val control = Control(name,controlPosition,note, location.latitude,location.longitude, 20.1 )

                controlsForCourse.add(control)
                controlPosition++
            } else {
                Log.e(TAG, "Add Marker Failure -> Location = null")
                Toast.makeText(this@CreateMapsActivity, "Unable to find current location", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * create an event using variable course and passed through name and note
     */
    fun createCourse(name: String){
        Log.e(TAG,"creating course")

        val course = Course(controlsForCourse,name)

        Log.e(TAG, "${course.courseDate}")
        CourseCreator.uploadCourse(course, this)

        if(PermissionManager.checkPermission(this,this@CreateMapsActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),PermissionManager.MULTIPLE_REQUEST_CODES)
        ){
            Log.e(TAG,"Creating Builder")

            var gpxBuilder = GPXBuilder(this,course.courseControls)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.e(TAG,"Building")
                gpxBuilder.buildGPX()
            }
        } else {
            Log.e(TAG,"No read-write permission")
        }

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
        createControl(name,note)
        selectImage(this@CreateMapsActivity)
    }

    /**
     * Start Location Updates
     */
    private fun startLocationUpdates(){
        if(PermissionManager.checkPermission(this,this@CreateMapsActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null)
        } else {
            Log.e(TAG,"Unable to find location -> Location = null")
            Toast.makeText(this@CreateMapsActivity, "Unable to find current location", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Update location
     */
    private fun createLocationRequest(){
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
        task.addOnFailureListener{e ->

            if (e is ResolvableApiException){
                // show a user dialogue by calling startResolutionForResult()
                // check result in onActivityResult()
                try{
                    e.startResolutionForResult(this@CreateMapsActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch(sendEx: IntentSender.SendIntentException){
                    // ignore
                }

            }
        }
    }

    // override AppCompatAcitivy's onActivityResult() method and start update request if result_ok under request check settings
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS){
            if (resultCode == Activity.RESULT_OK){
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    // override onPause() to stop location update request
    override fun onPause(){
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // override onResume to restart location updates
    override fun onResume() {
        super.onResume()
        if (!locationUpdateState){
            startLocationUpdates()
        }
    }

    fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Control Photo")
        builder.setItems(
            options,
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, item: Int ->

                if (options[item].equals("Take Photo")) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, "New Picture")
                    values.put(MediaStore.Images.Media.TITLE, "From the Camera")
                    var image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
                } else if (options[item].equals("Choose from Gallery")) {
                    val pickPhotoIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhotoIntent, PICK_IMAGE_CODE)
                } else {
                    dialogInterface.dismiss()
                }

            })
        builder.show()
    }

}
