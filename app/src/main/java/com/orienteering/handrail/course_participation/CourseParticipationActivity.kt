package com.orienteering.handrail.course_participation

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.events.EventsActivity
import com.orienteering.handrail.geofence_utilities.GeofenceBroadcastReceiver
import com.orienteering.handrail.geofence_utilities.GeofenceBuilder
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.interactors.PCPInteractor
import com.orienteering.handrail.models.*
import com.orienteering.handrail.performance.PerformanceActivity
import com.orienteering.handrail.performance_utilities.GeofencePerformanceCalculator
import com.orienteering.handrail.permissions.PermissionManager


// TAG for Logs
private val TAG: String = CourseParticipationActivity::class.java.name

/**
 * Class responsible for recording participants event performances
 *
 */
class CourseParticipationActivity : AppCompatActivity(), OnMapReadyCallback ,ICourseParticipationContract.ICourseActivity, GoogleMap.OnMarkerClickListener {

    // presenter for retrieval and upload of data
    lateinit var presenter : ICourseParticipationContract.ICoursePresenter
    //geofence builder to create fences on request
    val geofenceBuilder : GeofenceBuilder = GeofenceBuilder()
    // geofencing client to manage geofences
    private lateinit var geofencingClient: GeofencingClient
    // pending intent to handle geofence transitions
    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // google map view
    private lateinit var courseMap: GoogleMap
    // course list listview
    private lateinit var courseList : ListView
    // button for results upload
    private lateinit var uploadResultsButton : Button
    //button for results viewing
    private lateinit var viewResultsButton: Button
    //list of unique rest ids for geofences
    private var geoFencingRequestIds = mutableListOf<String>()
    // list of performance times
    private var performanceList = mutableListOf<String>()
    // event for course
    lateinit var event : Event
    // next control for participant
    private lateinit var myControl : Control
    //adapt performance results to list
    private lateinit var arrayAdapter : ArrayAdapter<String>
    // location client to provide location data
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // callback to handle location updates
    private lateinit var locationCallback: LocationCallback
    // location request used to create a
    private lateinit var locationRequest: LocationRequest
    // state of location upadtes
    private var locationUpdateState = false
    //Last Location
    private lateinit var lastLocation: Location
    // participant startTime
    private var startTime : Long = 0
    // participant performances
    var participantControlPerformances = mutableListOf<ParticipantControlPerformance>()
    // participant route points
    var participantRoutePoints = mutableListOf<RoutePoint>()
    // geofence performance calculator
    val geofencePerformanceCalculator = GeofencePerformanceCalculator()
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();

    /**
     * Companion object, contains permission request codes
     */
    companion object Companion{
        var altitude : Double? = null
        private const val REQUEST_CODE_CHECK = 2
        var courseActivity : CourseParticipationActivity =
            CourseParticipationActivity()

        fun getInstance() : CourseParticipationActivity {
            return courseActivity
        }
    }

    //check if running Q or later
    //additional permission required if so
    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * OnCreate
     * Determines correct Event
     * Sets up Elements
     * Sets up and creates location request
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_participation)

        val mapCourseFragment = supportFragmentManager.findFragmentById(R.id.map_course_participation) as SupportMapFragment
        PermissionManager.checkPermission(this,this@CourseParticipationActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)
        createButtons()


        // if intent extras passed successfully, initiate participation methods and location updates
        if(intent.extras!=null){
            // create list for control success information to be displayed
            courseList = findViewById(R.id.list_course_participation)
            arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,performanceList)
            courseList.adapter = arrayAdapter
            // initialise map and view
            mapCourseFragment.getMapAsync(this)
            courseActivity = this
            // initiate location services client
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            // callback to add to routepoints in order to capture user route
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    lastLocation = p0.lastLocation
                    val routePoint = RoutePoint(participantRoutePoints.size,lastLocation.latitude,lastLocation.longitude)
                    participantRoutePoints.add(routePoint)
                }
            }
            // initiate preseenter and set event id
            val eventId = intent.getSerializableExtra("EVENT_ID") as Int
            presenter = CourseParticipationPresenter(eventId,this, EventInteractor(), PCPInteractor())
            // initiate geofencing client
            geofencingClient = LocationServices.getGeofencingClient(this)
            // create location request
            createLocationRequest()
            // handle with error message
        }  else {
            Log.e(TAG,"Error: Event not found")
        }


    }


    /**
     * Function to create buttons for view and map uses
     */
    fun createButtons(){
        uploadResultsButton = findViewById(R.id.button_upload_results_course_participation)
        viewResultsButton =findViewById(R.id.button_view_results_course_participation)
        progressDialog = ProgressDialog(this@CourseParticipationActivity)
        progressDialog.setCancelable(false)
        uploadResultsButton.visibility= View.INVISIBLE
        viewResultsButton.visibility=View.INVISIBLE
        // upload participation on click
        uploadResultsButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                uploadParticipantControlPerformances()
                progressDialog.setMessage("Uploading...")
                progressDialog.show()
            }
        })
    }

    /**
     * Function to get event by intent extra event ID
     */
    private fun getEvents(){
        presenter.getDataFromDatabase()
        progressDialog.setMessage("Loading Content...")
        progressDialog.show()
    }

    /**
     * When map is ready, set options for user interaction, request permissions and markers and geofences
     */
    override fun onMapReady(googleMap: GoogleMap) {
        courseMap = googleMap
        getEvents()
        courseMap.uiSettings.setZoomControlsEnabled(false)
        courseMap.uiSettings.setCompassEnabled(false)
        courseMap.setOnMarkerClickListener(this)
        courseMap.uiSettings.setMyLocationButtonEnabled(false)
        courseMap.uiSettings.setAllGesturesEnabled(false)
        // manage additional map features such as display
        courseMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }

    /**
     * Setup map and request permissions
     */
    private fun setUpMap() {
        // check permissions for location, if so add onsuccess listener to location service
        if(PermissionManager.checkPermission(this,this@CourseParticipationActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)){
            courseMap.isMyLocationEnabled = true
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                lastLocation = location
                altitude = location.altitude

                //Update to LatLngBounds. Define Method to calculate SW and NE corners
                courseMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.eventCourse.courseControls[0].controlLatLng, 15f))
                Log.e(TAG,"Animating Camera to location")
            }
        }
    }


    /**
     * Start location updates by requesting from fusedlocation client
     */
    private fun startLocationUpdates() {
        // check permissions before requyesting location updates
        if(PermissionManager.checkPermission(this,this@CourseParticipationActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionManager.LOCATION_PERMISSION_REQUEST_CODE))

            if (runningQOrLater) {
                PermissionManager.checkPermission(this,this@CourseParticipationActivity,
                    arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PermissionManager.BACKGROUND_PERMISSION_REQUEST_CODE)
            }
        Log.e(TAG,"Starting Location Updates")
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    /**
     * Add the next control in the list of controls to the map in marker form, request a geofence be manufactured
     *
     */
    private fun addNextControl(){
        val markerOptions = MarkerOptions().position(myControl.controlLatLng)
        courseMap.addMarker(markerOptions)
        courseMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myControl.controlLatLng, 15f))
        if (myControl.controlPosition!!<event.eventCourse.courseControls.size){
            addNextGeoFence()
        }
    }

    private fun addNextGeoFence(){

        val geofencingRequest = geofenceBuilder.addGeofence(myControl.controlLatLng)

        geoFencingRequestIds.add(geofencingRequest.geofences[0].requestId)

        geofencingClient.addGeofences(geofencingRequest,geofencePendingIntent)?.run{
            addOnSuccessListener {

                Log.e(TAG,"Geofence Added at ${geofencingRequest.geofences}")

                val circleOptions = geofenceBuilder.drawGeofence(myControl.controlLatLng)
                courseMap.addCircle(circleOptions)
            }
        }
    }

    /**
     * called on successful trigger of geofence
     */
    fun triggerGeofence(){
        geofencingClient.removeGeofences(geoFencingRequestIds).addOnSuccessListener {
            Log.e(TAG,"FENCE REMOVED")
            addPerformance()
        }
    }

    /**
     * record and add performance to participant
     */
    private fun addPerformance(){
        // create a new participantcontrolperformance object, add to the list of performances
        val participantPerformance = ParticipantControlPerformance(System.currentTimeMillis() - startTime, myControl)
        participantControlPerformances.add(participantPerformance)
        // update the recycler view with the control time
        updateList(participantPerformance.controlTime)
        // check if more controls are required
        if (myControl.controlPosition!!<(event.eventCourse.courseControls.size-1)){
            // add the next control to the class variable myControl if so
            for (control in event.eventCourse.courseControls){
                if (control.controlPosition == (myControl.controlPosition!! +1)) {
                    myControl=control
                    break
                }
            }
            // call the addNextControl method to begin adding the next geofence
            addNextControl()
        } else {
            // else notify the user of the end and present them with the option to upload their results.
            Toast.makeText(this@CourseParticipationActivity,"Congratulations, you have completed the course",Toast.LENGTH_SHORT).show()
            makeButtonVisibile(uploadResultsButton)
        }
    }

    private fun makeButtonVisibile(button : Button){
        button.visibility = View.VISIBLE
    }


    /**
     * create location request
     */
    private fun createLocationRequest() {

        locationRequest = LocationRequest()

        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            Log.e(TAG,"Creating Location Request")
            startLocationUpdates()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@CourseParticipationActivity,
                        REQUEST_CODE_CHECK
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    //ignore
                }
            }
        }
    }

    private fun updateList(time : Long){
        val timeString = geofencePerformanceCalculator.convertMilliToMinutes(time)

        performanceList.add("Time/Minutes: $timeString")
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,performanceList)
        courseList.adapter = arrayAdapter
    }


    fun uploadParticipantControlPerformances(){
        val performanceUploadRequest = PerformanceUploadRequest(startTime,participantControlPerformances,participantRoutePoints)
        presenter.uploadParticipantControlPerformances(performanceUploadRequest)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHECK) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                Log.e(TAG,"OnActivityResult Starting Location Updates")
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy(){
        super.onDestroy()
        geofencingClient.removeGeofences(geoFencingRequestIds).addOnSuccessListener { Log.e(TAG,"FENCE REMOVED") }
        fusedLocationClient.removeLocationUpdates(locationCallback)
        presenter.onDestroy()
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onEventGetSuccess(event : Event){
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Log.e(TAG, "Success getting event")
        if (event != null) {
            setUpMap()
            startTime = System.currentTimeMillis()

            for (control in event.eventCourse.courseControls){
                control.createLatLng()
                if (control.controlPosition==0){
                    myControl = control
                }
            }
            this.event = event
        }
        addNextControl()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var granted = false
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults){
            if (result == PackageManager.PERMISSION_GRANTED){
                granted = true
                courseMap.isMyLocationEnabled = true
            } else {
                granted = false
                PermissionManager.displayPermissionRejection(this@CourseParticipationActivity)
                break
            }
        }
    }

    override fun onEventGetError(){
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Log.e(TAG, "Error getting event")
        Toast.makeText(this@CourseParticipationActivity,"Error: Service currently unavailable",Toast.LENGTH_SHORT).show()
        Intent(this@CourseParticipationActivity, EventsActivity::class.java).apply { }
    }

    override fun onEventGetFailure(){
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Log.e(TAG, "Failure getting event")
        Toast.makeText(this@CourseParticipationActivity,"Error: Failure to connect to service",Toast.LENGTH_SHORT).show()
        Intent(this@CourseParticipationActivity, EventsActivity::class.java).apply { }
    }

    override fun onParticipantPostSuccess(participant : Participant){
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Log.e(TAG,"Success adding performance")
        val toast = Toast.makeText(this@CourseParticipationActivity,"Performance Recorded",Toast.LENGTH_SHORT)
        toast.show()
        val intentResults = Intent(this@CourseParticipationActivity, PerformanceActivity::class.java).apply { }

        if (participant != null) {
            intentResults.putExtra("EVENT_ID", event.eventId)
            startActivity(intentResults)
            finish()
        }
    }

    override fun onParticipantPostError(){
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Log.e(TAG, "Error posting event")
        AlertDialog.Builder(this@CourseParticipationActivity)
            .setTitle("Error: Service currently unavailable")
            .setMessage("Please try again once connection resolved, do not leave this page or performance will be lost")
            .setPositiveButton("I Understand",
                DialogInterface.OnClickListener { dialog, which ->

                })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun onParticipantPostFailure(){
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Log.e(TAG, "Failure posting participant")
        AlertDialog.Builder(this@CourseParticipationActivity)
            .setTitle("Error: Failure to connect to service")
            .setMessage("Please try again once connection resolved, do not leave this page or performance will be lost")
            .setPositiveButton("I Understand",
                DialogInterface.OnClickListener { dialog, which ->
                })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return true
    }

}