package com.orienteering.handrail.toproutes

import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.results.ResultsAdapter

/**
 * Class responsible for user interface and displaying participant information of the top participant routes
 *
 */
class TopRoutesActivity : AppCompatActivity(), OnMapReadyCallback, ITopRoutesContract.ITopRoutesView {
    // event id passed through intent extra
    var eventIdPassed: Int = 0
    // recycler view to display performance items for participants
    private lateinit var recyclerView: RecyclerView
    // presenter handles all logic
    private lateinit var topRoutesPresenter: ITopRoutesContract.ITopRoutesPresenter
    // google map view
    private lateinit var routesMap: GoogleMap
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();

    /**
     * Initialise view, recycler view, map fragment and presenter. request participant information
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_top_routes)
        val mapCourseFragment = supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_route_results) as SupportMapFragment

        initRecyclerView()
        progressDialog = ProgressDialog(this@TopRoutesActivity)
        progressDialog.setCancelable(false)
        this.eventIdPassed = intent.getSerializableExtra("EVENT_ID") as Int
        topRoutesPresenter = TopRoutesPresenter(this, ParticipantInteractor())

        if (intent.extras != null) {
            //if event id is available intialise map and request participant data
            progressDialog.setMessage("Loading Content...")
            progressDialog.show()
            topRoutesPresenter.requestDataFromServer(intent.getSerializableExtra("EVENT_ID") as Int)
            this.eventIdPassed = intent.getSerializableExtra("EVENT_ID") as Int
            mapCourseFragment.getMapAsync(this)
        }
    }

    /**
     * sets up map ui elements and setUpMap function
     * @param p0
     */
    override fun onMapReady(googleMap: GoogleMap) {
        routesMap = googleMap
        routesMap.uiSettings.isZoomControlsEnabled = false
        routesMap.uiSettings.isCompassEnabled = false
        routesMap.uiSettings.isMyLocationButtonEnabled = false
        routesMap.uiSettings.setAllGesturesEnabled(false)
        routesMap.isMyLocationEnabled = false
        routesMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
    }

    /**
     * Display controls via map fragment markers
     *
     * @param controlsNameLatLng
     */
    override fun addControls(controlsNameLatLng: Map<String, LatLng>) {
        for ((key, value) in controlsNameLatLng) {
            // set marker title to string key of map, position to latlng value
            val markerOptions = MarkerOptions().position(value).title(key)
            // add marker to map
            routesMap.addMarker(markerOptions)
        }
    }

    /**
     * Animate camera to route bounds, add pattern for each participant
     *
     * @param participants
     * @param bounds
     */
    override fun showRoute(participants: List<Participant>, bounds: LatLngBounds) {
        // move map to new bounds
        routesMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
        // determine polyline pattern
        val pattern: MutableList<PatternItem> = mutableListOf()
        pattern.add(Dot())
        pattern.add(Gap(20F))
        pattern.add(Dash(30F))
        pattern.add(Gap(20F))
        // iterate through participants route points, add each to new latlng of color
        for (participant in participants) {
            val route: MutableList<LatLng> = mutableListOf()
            for (routePoint in participant.routePoints) {
                route.add(routePoint.latlng)
            }

            var color: Int = Color.RED
            // specify colour for position
            when (participants.indexOf(participant)) {
                0 -> color = Color.MAGENTA
                1 -> color = Color.BLUE
                2 -> color = Color.GREEN
                3 -> color = Color.YELLOW
                5 -> color = Color.CYAN
            }
            // create polyline for participant
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
    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.rv_route_results)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Show information via reycler view adapter to bind each information piece to new item in list
     *
     * @param names
     * @param times
     * @param positions
     * @param ids
     * @param imageUrls
     */
    override fun showInformation(names: List<String>, times: List<String>, positions: List<Int>, ids: MutableList<Int?>, imageUrls: List<String>) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val resultsAdapter = ResultsAdapter(names, times, imageUrls, ids, positions)
        recyclerView.adapter = resultsAdapter
    }

    override fun onResponseFailure(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Toast.makeText(this@TopRoutesActivity, "Error: Connectivity Error, unable to retreive results", Toast.LENGTH_SHORT).show()
    }

    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Toast.makeText(this@TopRoutesActivity, "No results available", Toast.LENGTH_SHORT).show()
    }
}