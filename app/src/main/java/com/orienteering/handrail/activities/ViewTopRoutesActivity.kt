package com.orienteering.handrail.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Participant
import com.orienteering.handrail.controllers.ParticipantController
import com.orienteering.handrail.utilities.GeofencePerformanceCalculator
import com.orienteering.handrail.utilities.MapUtilities
import com.orienteering.handrail.utilities.PermissionManager
import com.orienteering.handrail.utilities.ResultsRecylcerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ViewTopRoutesActivity"

class ViewTopRoutesActivity : AppCompatActivity(), OnMapReadyCallback {

    // google map view
    private lateinit var routesMap: GoogleMap

    // controller for participant services
    val participantController : ParticipantController = ParticipantController()

    // event id passed as intent extra
    var eventIdPassed : Int = 0
    // participant names
    var mNames = mutableListOf<String>()
    //participant time
    var mTime = mutableListOf<String>()
    //participant positions
    var mPosition = mutableListOf<Int>()
    //participant ids
    var mIds = mutableListOf<Int?>()
    //participant image urls
    var mImageUrls = mutableListOf<String>()
    // geofence performance calculator to convert performance times
    val geofencePerformanceCalculator = GeofencePerformanceCalculator()
    // map utilities such as camera and movement
    val mapUtilities = MapUtilities()

    // check if running Q or later
    // additional permission required if so
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    //participant callback to manage response of get participants service
    val getParticipantsCallback = object : Callback<List<Participant>> {
        override fun onFailure(call: Call<List<Participant>?>, t: Throwable) {
            Log.e(TAG, "Failure getting participants")
        }
        override fun onResponse(call: Call<List<Participant>?>, response: Response<List<Participant>>) {
            Log.e(TAG, "Success getting participants")
            val participants: List<Participant>? = response.body()

            if (participants != null) {

                for (participant in participants){
                    mNames.add(participant.participantUser.userFirstName)
                    mTime.add(geofencePerformanceCalculator.convertMilliToMinutes(participant.participantControlPerformances[participant.participantControlPerformances.size-1].controlTime))
                    mPosition.add(participants.indexOf(participant)+1)
                    mIds.add(participant.participantId)
                    mImageUrls.add("dummy")

                    for (routePoint in participant.routePoints){
                        routePoint.createLatLng()
                    }
                }
                addRoutes(participants)
                routesMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mapUtilities.determineNESW(mapUtilities.getAllParticipantRoutePoints(participants)),100))
                addMarkers()
                initRecyclerView()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_top_routes)
        val mapCourseFragment = supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_route_results) as SupportMapFragment

        this.eventIdPassed =  intent.getSerializableExtra("EVENT_ID") as Int
        getEvent()

        if(intent.extras!=null) {
            mapCourseFragment.getMapAsync(this)
        }
    }

    private fun getEvent(){
        participantController.getParticipants(eventIdPassed,getParticipantsCallback)
    }

    /**
     * When map is ready, set options, request permissions and markers and geofences
     */
    override fun onMapReady(googleMap: GoogleMap) {
        routesMap=googleMap
        routesMap.uiSettings.setZoomControlsEnabled(false)
        routesMap.uiSettings.setCompassEnabled(false)
        routesMap.uiSettings.setMyLocationButtonEnabled(false)
        routesMap.uiSettings.setAllGesturesEnabled(false)
        setUpMap()
    }

    /**
     * Setup map and request permissions
     */
    private fun setUpMap() {

        Log.e(TAG,"Setting Up Map")
        routesMap.isMyLocationEnabled = true
        routesMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        if(PermissionManager.checkPermission(this,this@ViewTopRoutesActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)
        )

            if (runningQOrLater) {
                PermissionManager.checkPermission(this,this@ViewTopRoutesActivity,
                    arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),PermissionManager.BACKGROUND_PERMISSION_REQUEST_CODE)
            }

        //Update to LatLngBounds. Define Method to calculate SW and NE corners
        var latLng = LatLng(54.574647, -5.957871)
        routesMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        Log.e(TAG,"Animating Camera to location")
    }

    /**
     * add control markers
     */
    private fun addMarkers(){

    }

    /**
     * add polyline routes
     */
    private fun addRoutes(participants : List<Participant>){

        val pattern: MutableList<PatternItem> = mutableListOf()
        pattern.add(Dot())
        pattern.add(Gap(20F))
        pattern.add(Dash(30F))
        pattern.add(Gap(20F))

        for (participant in participants){
            var route : MutableList<LatLng> = mutableListOf()
            for (routePoint in participant.routePoints){
                route.add(routePoint.latlng)
            }

            var color : Int = Color.RED

            when (participants.indexOf(participant)){
                0 -> color = Color.MAGENTA
                1 -> color = Color.BLUE
                2 -> color = Color.GREEN
                3 -> color = Color.YELLOW
                5 -> color = Color.CYAN
            }

            routesMap.addPolyline(
                PolylineOptions()
                    .addAll(route)
                    .width(10f)
                    .color(color)
                    .pattern(pattern)
            )
        }
    }

    /**
     * initialises recycler view of participants
     */
    private fun initRecyclerView(){
        Log.e(TAG,"initReyclerView")
        val recyclerView : RecyclerView = findViewById(R.id.rv_route_results)
        val mIdsToList = mIds.toList()
        val adapter = ResultsRecylcerViewAdapter(mNames,mTime,mImageUrls,mIdsToList,mPosition,this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
