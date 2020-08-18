package com.orienteering.handrail.course

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.orienteering.handrail.activities.CoursesActivity
import com.orienteering.handrail.dialogs.ViewCourseInfoDialog
import com.orienteering.handrail.dialogs.ViewMarkerCourseControlDialog
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.map_utilities.MapUtilities

class ICourseActivity : AppCompatActivity(),ICourseContract.ICourseView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // TAG for log
    val TAG = "CourseActivity"

    lateinit var coursePresenter: ICourseContract.ICoursePresenter

    // map fragment
    private lateinit var mapFragment: SupportMapFragment

    lateinit var recyclerView : RecyclerView

    // participant id passed as intent extra
    var courseId : Int? = null

    // google map view
    private lateinit var courseMap: GoogleMap
    // text view for course name
    lateinit var textViewCourseName : TextView
    // button for information
    lateinit var buttonInfo : Button
    // button for GPX Export
    lateinit var buttonExport : Button
    // button for delete
    lateinit var buttonDelete : Button

    // map utilities such as camera and movement
    val mapUtilities = MapUtilities()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course2)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_course_view) as SupportMapFragment

        if(intent.extras!=null) {
            mapFragment.getMapAsync(this)
            courseId = intent.getSerializableExtra("COURSE_ID") as Int
        }
        coursePresenter = this.courseId?.let { ICoursePresenter(it,this, CourseInteractor()) }!!

        textViewCourseName = findViewById(R.id.textView_course_view_name)

        initRecyclerView()
        createButtons()
        coursePresenter.requestDataFromServer()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker != null) {
            coursePresenter.controlInformation(marker.title)
        }
        return true
    }

    /**
     * Initialise buttons and set onclick listeners
     *
     */
    fun createButtons(){
        buttonInfo = findViewById(R.id.btn_course_view_info)
        buttonExport = findViewById(R.id.btn_course_view_export)
        buttonDelete = findViewById(R.id.btn_course_view_delete)

        buttonInfo.setOnClickListener() {
            coursePresenter.courseInformation()
        }

        buttonExport.setOnClickListener() {
            coursePresenter.generateFile(this@ICourseActivity)
        }

        buttonDelete.setOnClickListener() {
            coursePresenter.removeDataFromServer()
        }
    }

    /**
     * sets up map ui elements and setUpMap function
     * @param p0
     */
    override fun onMapReady(googleMap: GoogleMap) {
        courseMap=googleMap
        courseMap.uiSettings.setZoomControlsEnabled(false)
        courseMap.uiSettings.setCompassEnabled(false)
        courseMap.uiSettings.setMyLocationButtonEnabled(false)
        courseMap.uiSettings.setAllGesturesEnabled(false)
        courseMap.setOnMarkerClickListener(this)
        courseMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        courseMap.isMyLocationEnabled=false
        setUpMap()
    }

    /**
     * Setup map and request permissions
     */
    private fun setUpMap() {
        courseMap.setOnMarkerClickListener(this)
        courseMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        courseMap.isMyLocationEnabled=false
    }

    override fun showInformation(courseName: String?, courseNote: String?, courseAltitudes: MutableList<Double>?, courseDistance: Double) {
        val viewCourseInfoDialog : ViewCourseInfoDialog = ViewCourseInfoDialog(courseName,courseNote,courseAltitudes,courseDistance)
        viewCourseInfoDialog.show(mapFragment.childFragmentManager,"CourseInfo")
    }


    override fun showControlInformation(nameOfControl: String?, noteOfControl: String?, positionOfControl: Int?, imagePath: String?) {
        val markerDialog : ViewMarkerCourseControlDialog = ViewMarkerCourseControlDialog(nameOfControl, noteOfControl, positionOfControl, imagePath)
        markerDialog.show(mapFragment.childFragmentManager, "ViewMarkerDialog")
    }

    override fun showMessage(message: String) {
        Toast.makeText(this@ICourseActivity,message,Toast.LENGTH_SHORT).show()
    }

    /**
     * add polyline routes
     */
    override fun showRoute(routePoints : List<LatLng>, bounds : LatLngBounds){

        courseMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,200))

        val pattern: MutableList<PatternItem> = mutableListOf()
        pattern.add(Dot())
        pattern.add(Gap(20F))
        pattern.add(Dash(30F))
        pattern.add(Gap(20F))

        var color : Int = Color.RED
        courseMap.addPolyline(
            PolylineOptions()
                .addAll(routePoints)
                .width(10f)
                .color(color)
                .pattern(pattern)
        )
    }

    override fun addControls(controlsNameLatLng : Map<String,LatLng>) {
        for ((key,value) in controlsNameLatLng){
            Log.e("TAG","$key,$value")
            val markerOptions = MarkerOptions().position(value).title(key)
            // add marker to map
            courseMap.addMarker(markerOptions)
        }
    }

    /**
     *
     * initialises recycler view of participants
     */
    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_course_controls)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun fillRecyclerView(controls : List<Control>) {
        val courseAdapter : CourseAdapter = CourseAdapter(controls)
        recyclerView.adapter = courseAdapter
    }

    override fun onGetResponseSuccess(controls: List<Control>,courseName : String) {
        fillRecyclerView(controls)
        coursePresenter.getRoute(controls)
        if (courseName.isNotEmpty()){
            textViewCourseName.text = courseName!!
        }
    }

    override fun onDeleteResponseSuccess() {
        Log.i(TAG, "Success deleting course")
        Toast.makeText(this@ICourseActivity,"Course successfully deleted",Toast.LENGTH_SHORT).show()
        val intent : Intent = Intent(this@ICourseActivity, CoursesActivity::class.java)
        startActivity(intent)
    }

    override fun onResponseFailure(throwable: Throwable) {
        Log.e(TAG, "Failure reaching resource")
        Toast.makeText(this@ICourseActivity,"Error: Service currently unavailable",Toast.LENGTH_SHORT).show()
    }

    override fun onResponseError() {
        Log.e(TAG, "Failure processing course request")
        Toast.makeText(this@ICourseActivity,"Error: If problem persists, please contact admin",Toast.LENGTH_SHORT).show()
    }
}
