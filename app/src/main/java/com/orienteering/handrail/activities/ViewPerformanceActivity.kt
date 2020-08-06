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
import com.orienteering.handrail.classes.Control
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.classes.PerformanceResponse
import com.orienteering.handrail.classes.RoutePoint
import com.orienteering.handrail.controllers.ParticipantController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewPerformanceActivity : AppCompatActivity(), OnMapReadyCallback {

    // TAG for log
    val TAG = "ViewPerformanceActivity"

    lateinit var participantPerformance : PerformanceResponse
    // participant id passed as intent extra
    var eventId : Int? = null
    //participant time
    var mTime = mutableListOf<String>()
    // image urls for controls
    var mImageUrls = mutableListOf<String>()
    // distance ran between controls
    var mDistance = mutableListOf<String>()
    // recycler view for performances
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
    val getPerformanceCallback = object : Callback<StatusResponseEntity<PerformanceResponse>> {
        override fun onFailure(call: Call<StatusResponseEntity<PerformanceResponse>>, t: Throwable) {
            Log.e(TAG, "Failure getting performance")
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<PerformanceResponse>>,
            response: Response<StatusResponseEntity<PerformanceResponse>>
        ) {
            if (response.isSuccessful){
                Log.e(TAG, "Success getting performance")
                participantPerformance = response.body()?.entity!!
                addMarkers(participantPerformance.controls)
                addRoute(participantPerformance.routePoints)
                initRecyclerView()
            } else {
                Log.e(TAG, "Failure getting performance")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_performance)
        val mapCourseFragment = supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_route_results) as SupportMapFragment

        getParticipantPerformance()

        if(intent.extras!=null) {
            mapCourseFragment.getMapAsync(this)
            eventId = intent.getSerializableExtra("EVENT_ID") as Int
        }
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
        participantController.getParticipantPerformance(eventId!!, App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0), getPerformanceCallback)
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
     * initialises recycler view of participants
     */
    private fun initRecyclerView(){
        Log.e(TAG,"initReyclerView")
        recyclerView = findViewById(R.id.rv_route_performance)
        val mIdsToList = mIds.toList()
        val adapter = ResultsRecylcerViewAdapter(mNames,mTime,mImageUrls,mIdsToList,mPosition,this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
