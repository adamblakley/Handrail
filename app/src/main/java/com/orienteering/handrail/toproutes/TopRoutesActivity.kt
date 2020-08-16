package com.orienteering.handrail.toproutes

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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


class TopRoutesActivity : AppCompatActivity(), OnMapReadyCallback, ITopRoutesContract.ITopRoutesView {

    var eventIdPassed: Int = 0
    private lateinit var recyclerView: RecyclerView
    lateinit var topRoutesPerformer: ITopRoutesContract.ITopRoutesPerformer

    // google map view
    private lateinit var routesMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_top_routes)
        val mapCourseFragment =
            supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_route_results) as SupportMapFragment

        initRecyclerView()

        this.eventIdPassed = intent.getSerializableExtra("EVENT_ID") as Int
        topRoutesPerformer = TopRoutesPerformer(this, ParticipantInteractor())

        if (intent.extras != null) {
            topRoutesPerformer.requestDataFromServer(intent.getSerializableExtra("EVENT_ID") as Int)
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
        routesMap.uiSettings.setZoomControlsEnabled(false)
        routesMap.uiSettings.setCompassEnabled(false)
        routesMap.uiSettings.setMyLocationButtonEnabled(false)
        routesMap.uiSettings.setAllGesturesEnabled(false)
        routesMap.isMyLocationEnabled = false
        routesMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
    }

    override fun addControls(controlsNameLatLng: Map<String, LatLng>) {
        for ((key, value) in controlsNameLatLng) {
            Log.e("TAG", "$key,$value")
            val markerOptions = MarkerOptions().position(value).title(key)
            // add marker to map
            routesMap.addMarker(markerOptions)
        }
    }

    override fun showRoute(participants: List<Participant>, bounds: LatLngBounds) {

        routesMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))

        val pattern: MutableList<PatternItem> = mutableListOf()
        pattern.add(Dot())
        pattern.add(Gap(20F))
        pattern.add(Dash(30F))
        pattern.add(Gap(20F))

        for (participant in participants) {
            var route: MutableList<LatLng> = mutableListOf()
            for (routePoint in participant.routePoints) {
                route.add(routePoint.latlng)
            }

            var color: Int = Color.RED

            when (participants.indexOf(participant)) {
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
    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.rv_route_results)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun showRecyclerInformation(
        names: List<String>,
        times: List<String>,
        positions: List<Int>,
        ids: MutableList<Int?>,
        imageUrls: List<String>
    ) {
        val resultsAdapter: ResultsAdapter = ResultsAdapter(names, times, imageUrls, ids, positions)
        recyclerView.adapter = resultsAdapter
    }

    override fun onResponseFailure(throwable: Throwable) {
        Toast.makeText(this@TopRoutesActivity, "Error: Connectivity Error, unable to retreive results", Toast.LENGTH_SHORT).show()
    }

    override fun onResponseError() {
        Toast.makeText(this@TopRoutesActivity, "No results available", Toast.LENGTH_SHORT).show()
    }
}