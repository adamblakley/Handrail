package com.orienteering.handrail.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.*
import com.orienteering.handrail.controllers.ParticipantController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewPerformanceActivity : AppCompatActivity(), OnMapReadyCallback {

    // TAG for log
    val TAG = "ViewPerformanceActivity"

    lateinit var participant : Participant
    // participant id passed as intent extra
    var eventId : Int? = null
    // course controls
    var controls = mutableListOf<Control>()
    lateinit var recyclerView : RecyclerView

    // google map view
    private lateinit var performanceMap: GoogleMap
    // geofence performance calculator to convert performance times
    val geofencePerformanceCalculator = GeofencePerformanceCalculator()
    // map utilities such as camera and movement
    val mapUtilities = MapUtilities()
    // participant controller to manage participant services
    val participantController = ParticipantController()

    // check if running Q or later
    // additional permission required if so
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    // manage response of getEvents
    val getParticipantCallback = object : Callback<StatusResponseEntity<Participant>> {
        override fun onFailure(call: Call<StatusResponseEntity<Participant>>, t: Throwable) {
            Log.e(TAG, "Failure getting performance")
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<Participant>>,
            response: Response<StatusResponseEntity<Participant>>
        ) {
            if (response.isSuccessful){
                Log.e(TAG, "Success getting performance")
                participant = response.body()?.entity!!
                for (performance in participant.participantControlPerformances){
                    performance.pcpControl.createLatLng()
                    controls.add(performance.pcpControl)
                }
                addMarkers(controls)
                addRoute(participant.routePoints)
                initRecyclerView()
            } else {
                Log.e(TAG, "Failure getting performance")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_performance)
        val mapCourseFragment = supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_route_performance) as SupportMapFragment

        if(intent.extras!=null) {
            mapCourseFragment.getMapAsync(this)
            eventId = intent.getSerializableExtra("EVENT_ID") as Int
        }

        getParticipantPerformance()


    }

    /**
     * sets up map ui elements and setUpMap function
     * @param p0
     */
    override fun onMapReady(googleMap: GoogleMap) {
        performanceMap=googleMap
        performanceMap.uiSettings.setZoomControlsEnabled(false)
        performanceMap.uiSettings.setCompassEnabled(false)
        performanceMap.uiSettings.setMyLocationButtonEnabled(false)
        performanceMap.uiSettings.setAllGesturesEnabled(false)
        setUpMap()
    }

    /**
     * Setup map and request permissions
     */
    private fun setUpMap() {

        Log.e(TAG,"Setting Up Map")
        performanceMap.isMyLocationEnabled = true
        performanceMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        if(PermissionManager.checkPermission(this,this@ViewPerformanceActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)
        )

            if (runningQOrLater) {
                PermissionManager.checkPermission(this,this@ViewPerformanceActivity,
                    arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PermissionManager.BACKGROUND_PERMISSION_REQUEST_CODE)
            }

        //Update to LatLngBounds. Define Method to calculate SW and NE corners
        var latLng = LatLng(54.574647, -5.957871)
        performanceMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        Log.e(TAG,"Animating Camera to location")
    }

    /**
     * Manage service call to get participant
     *
     */
    fun getParticipantPerformance(){
        participantController.getParticipant(eventId!!,(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0)),getParticipantCallback)
    }

    /**
     * add polyline routes
     */
    private fun addRoute(routePoints : List<RoutePoint>){

        val pattern: MutableList<PatternItem> = mutableListOf()
        pattern.add(Dot())
        pattern.add(Gap(20F))
        pattern.add(Dash(30F))
        pattern.add(Gap(20F))

        for (routePoint in routePoints){
            var route : MutableList<LatLng> = mutableListOf()
            for (routePoint in routePoints){
                routePoint.createLatLng()
                route.add(routePoint.latlng)
            }

            var color : Int = Color.RED

            performanceMap.addPolyline(
                PolylineOptions()
                    .addAll(route)
                    .width(10f)
                    .color(color)
                    .pattern(pattern)
            )
        }
    }

    /**
     * add control markers
     */
    private fun addMarkers(controls : List<Control>){
        for (control in controls){
            val markerOptions = MarkerOptions().position(control.controlLatLng)
            // add marker name
            markerOptions.title(control.controlName)
            // add marker to map
            performanceMap.addMarker(markerOptions)
        }
    }

    /**
     *
     * initialises recycler view of participants
     */
    private fun initRecyclerView(){
        // control names
        var controlNames = mutableListOf<String>()
        // control positions
        var controlPositions = mutableListOf<Int>()
        // image urls for controls
        var imageUrls = mutableListOf<String>()
        // participant time
        var times = mutableListOf<String>()
        // distance ran between controls
        var distances = mutableListOf<Float>()
        // recycler view for performances

        for (control in controls){
            if (control.controlPhotograph!=null){
                imageUrls.add(control.controlPhotograph.photoPath)
            }
            controlPositions.add(control.controlPosition!!)
            controlNames.add(control.controlName!!)
        }

        for (performance in participant.participantControlPerformances){
            times.add(geofencePerformanceCalculator.convertMilliToMinutes(performance.controlTime))
        }

        Log.e(TAG,"initReyclerView")
        recyclerView = findViewById(R.id.rv_route_performance)
        val adapter = PerformanceReyclerViewAdapter(imageUrls,controlPositions,controlNames,times,this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
