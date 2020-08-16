package com.orienteering.handrail.create_course

import android.content.IntentSender
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.ImageSelect
import com.orienteering.handrail.utilities.MultipartBodyFactory
import com.orienteering.handrail.utilities.PermissionManager
import okhttp3.MultipartBody
import retrofit2.Response

class CreateCoursePerformer(createCourseView : ICreateCourseContract.ICreateCourseView, courseInteractor: CourseInteractor, imageSelect: ImageSelect) : ICreateCourseContract.ICreateCoursePerformer {
    private val REQUEST_CHECK_SETTINGS = 2
    var courseInteractor : CourseInteractor
    var createCourseView : ICreateCourseContract.ICreateCourseView
    var postCoursetOnFinishedListener : IOnFinishedListener<Course>
    // image select
    var imageSelect : ImageSelect
    // create image files
    var multipartBodyFactory : MultipartBodyFactory
    // Fused Location Prover Client Variable
    private var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback : LocationCallback
    // location request and updated location state
    // store parameters for requests to fused location provider (determining level of accuracy)
    private lateinit var locationRequest: LocationRequest
    // location update state
    private var locationUpdateState = false
    // Last Location
    private lateinit var lastLocation: Location
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

    init{
        this.courseInteractor = courseInteractor
        this.createCourseView = createCourseView
        this.postCoursetOnFinishedListener = PostCourseOnFinishedListener(this,createCourseView)
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(createCourseView.returnContext())
        this.imageSelect=imageSelect
        this.multipartBodyFactory = MultipartBodyFactory(imageSelect)
        // fused location provider invokes the LocationCallback.onLocationResult() method. Incoming argument contains a  Locaiton obkect containing location's lat and lng
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                listOfRoutePoints.add(currentLatLng)
                createCourseView.animateMapCamera(currentLatLng)
                createCourseView.addMapPolyline(listOfRoutePoints)
            }
        }
    }

    /**
     * Update location
     */
    override fun createLocationRequest() {
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
        val client = LocationServices.getSettingsClient(createCourseView.returnActivity())
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
                try { e.startResolutionForResult(createCourseView.returnActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore
                }
            }
        }
    }

    override fun updateLocationUpdateState(){
        locationUpdateState = true
        startLocationUpdates()
    }

    override fun setImage(imageUri: Uri) {
        if (imageUri != null) {
            fileUris.put(controlsForCourse.size, imageUri)
        }
    }

    /**
     * Request fine location, find current location and locate device
     */
    override fun setUpMap() {
        if (PermissionManager.checkPermission(createCourseView.returnActivity(), createCourseView.returnContext(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)) {

            createCourseView.setUpMap()
            // step 2 to display location
            // provide most recent location
            fusedLocationClient.lastLocation.addOnSuccessListener(createCourseView.returnActivity()) { location ->

                // step 3 to display location
                // get last known location
                if (location != null) {

                    lastLocation = location

                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    createCourseView.animateMapCamera(currentLatLng)

                } else {
                    createCourseView.onResponseError()
                }
            }
        }
    }


    /**
     * Start Location Updates
     */
    private fun startLocationUpdates() {
        if (PermissionManager.checkPermission(createCourseView.returnActivity(), createCourseView.returnContext(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            Log.e("TAG", "Unable to find location -> Location = null")
            Toast.makeText(createCourseView.returnContext(), "Unable to find current location", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Save current latlng for potential control
     * @return
     */
    override fun saveLatLng(){
        fusedLocationClient.lastLocation.addOnSuccessListener(createCourseView.returnActivity()) { location ->
            if (location != null) {
                lastLocation = location
                potentialAltitude = lastLocation.altitude
                potentialLatLng = LatLng(location.latitude, location.longitude)
            } else {
                potentialLatLng = LatLng(0.0, 0.0)
                createCourseView.onLocationUpdateFailure()
            }
        }
        if (potentialLatLng.latitude != 0.0 && potentialLatLng.longitude != 0.0) {
            if (PermissionManager.checkPermission(createCourseView.returnActivity(), createCourseView.returnContext(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)) {
                createCourseView.onSaveLatLngSuccess()
            }
        } else {
            createCourseView.onLocationUpdateFailure()
        }
    }

    override fun createControl(name : String, note : String) {
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
        createCourseView.addMapControl(name,note,potentialLatLng)
        imageSelect.selectImage()
    }

    /**
     * create an course using variable course and passed through name and note
     *
     * @param name
     */
    override fun createCourse(name: String, note : String) {
        val course = Course(controlsForCourse, name)
        var files: Array<MultipartBody.Part?> = arrayOfNulls(controlsForCourse.size)
        for ((key, value) in fileUris) {
            val multipartBodyPart: MultipartBody.Part = multipartBodyFactory.createImageMultipartBody(createCourseView.returnActivity(),value)
            val position = key
            val positionMinusOne = position - 1
            files.set(positionMinusOne, multipartBodyPart)
        }
        courseInteractor.uploadCourse(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0), course, files, postCoursetOnFinishedListener)
    }

    override fun getCourseLength(): Boolean {
        if (controlsForCourse.size>0){
            return true
        } else {
            return false
        }
    }

    override fun getControlInformation(nameOfControl: String) {
        val nameOfMarker: String? = nameOfControl
        var positionOfMarker: Int? = null
        var imageUriOfMarker: Uri? = null
        var noteOfMarket: String? = null

        for (control in controlsForCourse) {
            if (control.controlName.equals(nameOfControl)) {
                positionOfMarker = control.controlPosition
                noteOfMarket = control.controlNote
                if (fileUris.containsKey(positionOfMarker)) {
                    imageUriOfMarker = fileUris.get(positionOfMarker)
                }
            }
        }
        createCourseView.onControlinformationSucess(nameOfMarker,noteOfMarket,positionOfMarker,imageUriOfMarker)
    }
}




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class PostCourseOnFinishedListener(createCoursePerformer : ICreateCourseContract.ICreateCoursePerformer, createCourseView : ICreateCourseContract.ICreateCourseView) : IOnFinishedListener<Course> {
    // Events view
    private var createCourseView : ICreateCourseContract.ICreateCourseView
    // Events presenter
    private var createCoursePerformer : ICreateCourseContract.ICreateCoursePerformer

    /**
     * Initialises view, presenter
     */
    init{
        this.createCoursePerformer = createCoursePerformer
        this.createCourseView = createCourseView
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Course>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                response.body()!!.entity?.courseId?.let { createCourseView.onPostResponseSuccess(it) }
            } else {
                createCourseView.onResponseError()
            }
        } else {
            createCourseView.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (createCourseView!=null){
            createCourseView.onResponseFailure()
        }
    }
}