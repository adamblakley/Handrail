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
import com.orienteering.handrail.courses.CourseAdapter
import com.orienteering.handrail.dialogs.ViewCourseInfoDialog
import com.orienteering.handrail.dialogs.ViewMarkerCourseControlDialog
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.models.Control

// TAG for Logs
private val TAG: String = CourseActivity::class.java.name

/**
 * View responsible for viewing individual course information
 *
 */
class CourseActivity : AppCompatActivity(),ICourseContract.ICourseView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // Responsible for logic of retreiving course information and handling events
    lateinit var presenter: ICourseContract.ICoursePresenter
    // map fragment for displaying course
    private lateinit var mapFragment: SupportMapFragment
    // list to hold all control items
    lateinit var recyclerView : RecyclerView
    // participant id passed as intent extra
    var courseId : Int? = null
    // google map view
    private lateinit var courseMap: GoogleMap
    // text view for course name
    private lateinit var textViewCourseName : TextView
    // button for information
    private lateinit var buttonInfo : Button
    // button for GPX Export
    private lateinit var buttonExport : Button
    // button for delete
    private lateinit var buttonDelete : Button

    /**
     * Handles creation of view, buttons and requests data from presenter
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course2)
        // set map view as supportmap fragment of selected map view layout
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_course_view) as SupportMapFragment

        // check intent for course id, then initialise presenter and request
        if(intent.extras!=null) {
            mapFragment.getMapAsync(this)
            courseId = intent.getSerializableExtra("COURSE_ID") as Int
            presenter = this.courseId?.let { CoursePresenter(it,this, CourseInteractor()) }!!
            presenter.requestDataFromServer()
        }
        // textview for the course name
        textViewCourseName = findViewById(R.id.textView_course_view_name)
        // initialise recyclerview and buttons
        initRecyclerView()
        createButtons()

    }

    /**
     * on marker click display control information
     * @param marker
     * @return
     */
    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker != null) {
            presenter.controlInformation(marker.title)
        }
        return true
    }

    /**
     * Initialise buttons and set onclick listeners for information, gpx export and deletion
     *
     */
    fun createButtons(){
        buttonInfo = findViewById(R.id.btn_course_view_info)
        buttonExport = findViewById(R.id.btn_course_view_export)
        buttonDelete = findViewById(R.id.btn_course_view_delete)

        buttonInfo.setOnClickListener {
            presenter.courseInformation()
        }

        buttonExport.setOnClickListener {
            presenter.generateFile(this@CourseActivity)
        }

        buttonDelete.setOnClickListener {
            presenter.removeDataFromServer()
        }
    }

     /**
     *sets up map ui elements and setUpMap function
     * @param googleMap
     */
    override fun onMapReady(googleMap: GoogleMap) {
        courseMap=googleMap
        courseMap.uiSettings.isZoomControlsEnabled = false
        courseMap.uiSettings.isCompassEnabled = false
        courseMap.uiSettings.isMyLocationButtonEnabled = false
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

    /**
     * Show course information in a new dialog
     * @param courseName
     * @param courseNote
     * @param courseAltitudes
     * @param courseDistance
     */
    override fun showInformation(courseName: String?, courseNote: String?, courseAltitudes: MutableList<Double>?, courseDistance: Double) {
        val viewCourseInfoDialog = ViewCourseInfoDialog(courseName,courseNote,courseAltitudes,courseDistance)
        viewCourseInfoDialog.show(mapFragment.childFragmentManager,"CourseInfo")
    }


    /**
     * Show control information in a new dialog
     * @param nameOfControl
     * @param noteOfControl
     * @param positionOfControl
     * @param imagePath
     */
    override fun showControlInformation(nameOfControl: String?, noteOfControl: String?, positionOfControl: Int?, imagePath: String?) {
        val markerDialog = ViewMarkerCourseControlDialog(nameOfControl, noteOfControl, positionOfControl, imagePath)
        markerDialog.show(mapFragment.childFragmentManager, "ViewMarkerDialog")
    }

    /**
     * make toast message by passing string
     *
     * @param message
     */
    override fun showMessage(message: String) {
        Toast.makeText(this@CourseActivity,message,Toast.LENGTH_SHORT).show()
    }

    /**
     * add polyline routes
     */
    override fun showRoute(routePoints : List<LatLng>, bounds : LatLngBounds){
        // move the camera to the new location speciffied
        courseMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,200))
        // determine pattern of route
        val pattern: MutableList<PatternItem> = mutableListOf()
        pattern.add(Dot())
        pattern.add(Gap(20F))
        pattern.add(Dash(30F))
        pattern.add(Gap(20F))
        // create route and add to map
        val color : Int = Color.RED
        courseMap.addPolyline(
            PolylineOptions()
                .addAll(routePoints)
                .width(10f)
                .color(color)
                .pattern(pattern)
        )
    }

    /**
     * Add all controls to the map
     * @param controlsNameLatLng
     */
    override fun addControls(controlsNameLatLng : Map<String,LatLng>) {
        for ((key,value) in controlsNameLatLng){
            Log.e("TAG","$key,$value")
            val markerOptions = MarkerOptions().position(value).title(key)
            // add marker to map
            courseMap.addMarker(markerOptions)
        }
    }

    /**
     * initialises recycler view of controls
     */
    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_course_controls)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Fills recycler view with passed controls
     * @param controls
     */
    override fun fillRecyclerView(controls : List<Control>) {
        val courseAdapter = CourseAdapter(controls)
        recyclerView.adapter = courseAdapter
    }

    /**
     * If course received, complete acitons to update the display
     * @param controls
     * @param courseName
     */
    override fun onGetResponseSuccess(controls: List<Control>,courseName : String) {
        fillRecyclerView(controls)
        presenter.getRoute(controls)
        if (courseName.isNotEmpty()){
            textViewCourseName.text = courseName
        }
    }

    /**
     * Confirm course deletion start activity Courses
     */
    override fun onDeleteResponseSuccess() {
        Log.i(TAG, "Success deleting course")
        Toast.makeText(this@CourseActivity,"Course successfully deleted",Toast.LENGTH_SHORT).show()
        val intent = Intent(this@CourseActivity, com.orienteering.handrail.courses.CoursesActivity::class.java)
        startActivity(intent)
    }

    /**
     * Notify user of failure
     * @param throwable
     */
    override fun onResponseFailure(throwable: Throwable) {
        Log.e(TAG, "Failure reaching resource")
        Toast.makeText(this@CourseActivity,"Error: Service currently unavailable",Toast.LENGTH_SHORT).show()
    }

    /**
     * Notify user of error
     */
    override fun onResponseError() {
        Log.e(TAG, "Failure processing course request")
        Toast.makeText(this@CourseActivity,"Error: If problem persists, please contact admin",Toast.LENGTH_SHORT).show()
    }
}
