package com.orienteering.handrail.performance

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.orienteering.handrail.R
import com.orienteering.handrail.interactors.ParticipantInteractor

// TAG for Logs
private val TAG: String = PerformanceActivity::class.java.name

/**
 * Class responsible for displaying user performance values
 *
 */
class PerformanceActivity : AppCompatActivity(), OnMapReadyCallback, IPerformanceContract.IPerformanceView {

    // presenter handles performance logic
    private lateinit var performancePresenter : IPerformanceContract.IPerformancePresenter

    // recycler view for control list
    private lateinit var recyclerView : RecyclerView
    // text view for total distance
    private lateinit var textViewDistance : TextView
    // text view for pace
    private lateinit var textViewPace : TextView
    // google map view
    private lateinit var performanceMap: GoogleMap
    private lateinit var textPosition : TextView

    /**
     * Request view intialise along with ui elements and presenter
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_performance)
        // map fragment for displaying course information
        val mapCourseFragment = supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_route_performance) as SupportMapFragment

        initRecyclerView()
        createTextView()

        this.performancePresenter = PerformancePresenter(this, ParticipantInteractor())
        // if intent extra is provided in form of event id, request presenter find performance
        if(intent.extras!=null) {
            // request map initialisation
            mapCourseFragment.getMapAsync(this)
            performancePresenter.requestDataFromServer(intent.getSerializableExtra("EVENT_ID") as Int)
        }
    }

    /**
     * sets up map ui elements and setUpMap function
     * @param p0
     */
    override fun onMapReady(googleMap: GoogleMap) {
        performanceMap=googleMap
        performanceMap.uiSettings.isZoomControlsEnabled = false
        performanceMap.uiSettings.isCompassEnabled = false
        performanceMap.uiSettings.isMyLocationButtonEnabled = false
        performanceMap.uiSettings.setAllGesturesEnabled(false)
        performanceMap.isMyLocationEnabled = false
        performanceMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }

    /**
     * show route on map fragment via polyline
     *
     * @param routePoints
     * @param bounds
     * @param totalDistance
     * @param pace
     */
    override fun showRoute(routePoints: List<LatLng>, bounds: LatLngBounds, totalDistance : Double, pace : Double) {
        performanceMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,100))
        val pattern: MutableList<PatternItem> = mutableListOf()
        pattern.add(Dot())
        pattern.add(Gap(20F))
        pattern.add(Dash(30F))
        pattern.add(Gap(20F))
        textViewDistance.text = "Total Distance: Metres: %.2f".format(totalDistance)
        textViewPace.text = "Average Pace: Metres per Minute: %d".format(pace.toInt())
        performanceMap.addPolyline(PolylineOptions().addAll(routePoints).width(10f).color(Color.RED).pattern(pattern))
    }

    /**
     * For controls in map, add to mapview. Key value is marker options title
     *
     * @param controlsNameLatLng
     */
    override fun addControls(controlsNameLatLng : Map<String,LatLng>) {
        for ((key,value) in controlsNameLatLng){
            Log.e("TAG","$key,$value")
            val markerOptions = MarkerOptions().position(value).title(key)
            // add marker to map
            performanceMap.addMarker(markerOptions)
        }
    }


    /**
     * Sets up textviews in view
     *
     */
    private fun createTextView(){
        textViewDistance = findViewById(R.id.textView_distance_performance)
        textViewPace = findViewById(R.id.textView_pace_performance)
        textPosition = findViewById(R.id.textView_position_performance)
    }

    /**
     *
     * initialises recycler view of participants
     */
    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_route_performance)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Use recycler view adapter to iterate through parameters and add to recycler view as items
     *
     * @param imageUrls
     * @param controlPositions
     * @param controlNames
     * @param times
     * @param altitudes
     */
    override fun fillInformation(imageUrls : List<String>,controlPositions : List<Int>,controlNames : List<String>,times : List<String>,altitudes : List<Double>, position : Int) {
        val performanceAdapter : PerformanceAdapter = PerformanceAdapter(imageUrls,controlPositions,controlNames,times,altitudes)
        if (position!=0){
            textPosition.text = "Position: $position"
        }
        recyclerView.adapter = performanceAdapter
    }

    override fun onResponseFailure(throwable: Throwable) {
        Log.e(TAG, "Failure connecting to service")
        Toast.makeText(this@PerformanceActivity,"Error: Unable to connect to service",Toast.LENGTH_SHORT).show()
    }

    override fun onResponseError() {
        Log.e(TAG, "Error getting performance")
        Toast.makeText(this@PerformanceActivity,"Error: Unable to retreive performance",Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy(){
        super.onDestroy()
        performancePresenter.onDestroy()
    }
}
