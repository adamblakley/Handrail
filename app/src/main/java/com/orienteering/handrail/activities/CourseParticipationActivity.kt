package com.orienteering.handrail.activities

import com.orienteering.handrail.R
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.orienteering.handrail.classes.*
import com.orienteering.handrail.controllers.EventController
import com.orienteering.handrail.controllers.PcpController
import com.orienteering.handrail.controllers.RoutePointController
import com.orienteering.handrail.httprequests.*
import com.orienteering.handrail.services.EventService
import com.orienteering.handrail.services.PcpService
import com.orienteering.handrail.services.RoutePointService
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.utilities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseParticipationActivity : AppCompatActivity(), OnMapReadyCallback {

    //geofence builder to create fences on request
    val geofenceBuilder : GeofenceBuilder = GeofenceBuilder()

    // geofencing client to manage geofences
    lateinit var geofencingClient: GeofencingClient

    // pending intent to handle geofence transitions
    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // event controller to manage service requests
    val eventController:EventController = EventController()

    // pcp controller to manage service requests
    val pcpController:PcpController = PcpController()

    // route point controller to manage service requests
    val routePointController: RoutePointController = RoutePointController()

    // manage response of getEvents
    val getEventsCallback = object : Callback<Event>{
        override fun onFailure(call: Call<Event>, t: Throwable) {
            Log.e(TAG, "Failure getting event")
        }

        override fun onResponse(
            call: Call<Event>,
            response: Response<Event>
        ) {
            Log.e(TAG, "Success getting event")
            val eventgot: Event? = response.body()
            if (eventgot != null) {

                for (participant in eventgot.participants){
                    if (participant.participantUser.userId?.equals(3)!!){
                        myParticipant=participant;
                        myParticipant.startTime = System.currentTimeMillis()
                    }
                }

                for (control in eventgot.eventCourse.courseControls){
                    if (control.controlPosition==0){
                        myControl = control
                    }
                }

                event = eventgot
                Log.e(TAG,event.toString())
                for (control in event.eventCourse.courseControls){
                    control.createLatLng()
                }
            }
            addNextControl()
        }
    }

    // manage response of upload performance
    val uploadParticipantControlPerformanceCallback = object : Callback<StatusResponseEntity<List<ParticipantControlPerformance>>?> {

        override fun onFailure(call: Call<StatusResponseEntity<List<ParticipantControlPerformance>>?>, t: Throwable) {
            Log.e(TAG,"Failure adding pcps")
        }

        override fun onResponse(
            call: Call<StatusResponseEntity<List<ParticipantControlPerformance>>?>,
            response: Response<StatusResponseEntity<List<ParticipantControlPerformance>>?>
        ) {
            Log.e(TAG,"Success adding pcps")
        }
    }

    // manage reponse of upload route points
    val uploadRoutePointsCallback = object : Callback<StatusResponseEntity<List<RoutePoint>>?> {

        override fun onFailure(call: Call<StatusResponseEntity<List<RoutePoint>>?>, t: Throwable) {
            Log.e(TAG,"Failure adding RoutePoints")
        }

        override fun onResponse(
            call: Call<StatusResponseEntity<List<RoutePoint>>?>,
            response: Response<StatusResponseEntity<List<RoutePoint>>?>
        ) {
            Log.e(TAG,"Success adding RoutePoints")
        }
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
    var geoFencingRequestIds = mutableListOf<String>()

    // list of performance times
    var performanceList = mutableListOf<String>()

    // event for course
    lateinit var event : Event

    // next control for participant
    lateinit var myControl : Control

    //adapt performance results to list
    lateinit var arrayAdapter : ArrayAdapter<String>

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    private lateinit var locationRequest: LocationRequest

    private var locationUpdateState = false

    //Last Location
    private lateinit var lastLocation: Location

    //current participant
    lateinit var myParticipant : Participant

    // geofence performance calculator
    val geofencePerformanceCalculator = GeofencePerformanceCalculator()

    /**
     * Companion object, contains permission request codes
     */
    companion object Companion{
        var altitude : Double? = null
        private const val REQUEST_CODE_CHECK = 2
        var courseActivity : CourseParticipationActivity = CourseParticipationActivity()

        fun getInstance() : CourseParticipationActivity{
            return courseActivity
        }
    }

    //check if running Q or later
    //    additional permission required if so
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    /**
     * OnCreate
     * Determines correct Event
     * Sets up Elements
     * Sets up and creates location request
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.orienteering.handrail.R.layout.activity_course_participation)

        val mapCourseFragment = supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_course_participation) as SupportMapFragment

        createButtons()

        if(intent.extras!=null){

            getEvents()

            courseList = findViewById(com.orienteering.handrail.R.id.list_course_participation)

            arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,performanceList)

            courseList.adapter = arrayAdapter

            mapCourseFragment.getMapAsync(this)

            courseActivity = this

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    lastLocation = p0.lastLocation
                    val routePoint = RoutePoint(myParticipant.routePoints.size,lastLocation.latitude,lastLocation.longitude)
                    myParticipant.routePoints.add(routePoint)
                    for (rp in myParticipant.routePoints){
                        Log.e(TAG,rp.toString())
                    }
                }
            }

            geofencingClient = LocationServices.getGeofencingClient(this)

            createLocationRequest()

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

        uploadResultsButton.visibility= View.INVISIBLE
        viewResultsButton.visibility=View.INVISIBLE

        uploadResultsButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                uploadParticipantControlPerformances()
                uploadRoutePoints()
            }
        })
    }

    /**
     * Function to get event by intent extra event ID
     */
    fun getEvents(){
        val eventId = intent.getSerializableExtra("EVENT_ID") as Int
        eventController.retreiveByID(eventId,getEventsCallback)
    }

    /**
     * When map is ready, set options, request permissions and markers and geofences
     */
    override fun onMapReady(googleMap: GoogleMap) {
        courseMap = googleMap

        courseMap.uiSettings.setZoomControlsEnabled(false)
        courseMap.uiSettings.setCompassEnabled(false)
        courseMap.uiSettings.setMyLocationButtonEnabled(false)
        courseMap.uiSettings.setAllGesturesEnabled(false)

        setUpMap()
    }

    /**
     * Setup map and request permissions
     */
    private fun setUpMap() {

        Log.e(TAG,"Setting Up Map")

        courseMap.isMyLocationEnabled = true
        courseMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        if(PermissionManager.checkPermission(this,this@CourseParticipationActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)
        )

        if (runningQOrLater) {
            PermissionManager.checkPermission(this,this@CourseParticipationActivity,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),PermissionManager.BACKGROUND_PERMISSION_REQUEST_CODE)
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                altitude = location.getAltitude()

                //Update to LatLngBounds. Define Method to calculate SW and NE corners
                courseMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.eventCourse.courseControls[0].controlLatLng, 15f))
                Log.e(TAG,"Animating Camera to location")
            }
        }
    }


    /**
     * Start location updates
     */
    private fun startLocationUpdates() {

        if(PermissionManager.checkPermission(this,this@CourseParticipationActivity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),PermissionManager.LOCATION_PERMISSION_REQUEST_CODE))

        if (runningQOrLater) {
            PermissionManager.checkPermission(this,this@CourseParticipationActivity,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),PermissionManager.BACKGROUND_PERMISSION_REQUEST_CODE)
        }
        Log.e(TAG,"Starting Location Updates")
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    /**
     * Add Control Markers to Course
     */
    private fun setCourse() {
        Log.e(TAG,"Adding Controls to map")
        for (control in event.eventCourse.courseControls) {
            control.controlLatLng
            val markerOptions = MarkerOptions().position(control.controlLatLng)
                .title(control.controlId.toString())
            courseMap.addMarker(markerOptions)
        }

    }

    private fun addNextControl(){
        val markerOptions = MarkerOptions().position(myControl.controlLatLng)
        courseMap.addMarker(markerOptions)

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

        val participantPerformance = ParticipantControlPerformance(System.currentTimeMillis()- myParticipant.startTime!!, myControl)
        myParticipant.participantControlPerformances.add(participantPerformance)

        updateList(participantPerformance.controlTime)

        if (myControl.controlPosition!!<(event.eventCourse.courseControls.size-1)){

            for (control in event.eventCourse.courseControls){
                if (control.controlPosition == (myControl.controlPosition!! +1)) {
                    myControl=control
                    break
                }
            }
            addNextControl()
        } else {
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
                    e.startResolutionForResult(this@CourseParticipationActivity, REQUEST_CODE_CHECK)
                } catch (sendEx: IntentSender.SendIntentException) {
                    //ignore
                }
            }
        }
    }

    fun updateList(time : Long){
        Log.e("Course Part Activity","Writing Time")

        val timeString = geofencePerformanceCalculator.convertMilliToMinutes(time)

        performanceList.add("Time/Minutes: $timeString")
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,performanceList)
        courseList.adapter = arrayAdapter
    }


    fun uploadParticipantControlPerformances(){
       pcpController.create(myParticipant.participantId,myParticipant.participantControlPerformances,uploadParticipantControlPerformanceCallback)
    }

    fun uploadRoutePoints(){
        routePointController.create(myParticipant.participantId,myParticipant.routePoints,uploadRoutePointsCallback)
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

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

}

private const val TAG = "CourseParticipation"
