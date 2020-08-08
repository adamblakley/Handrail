package com.orienteering.handrail.activities

import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Control
import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.classes.RoutePoint
import com.orienteering.handrail.controllers.CourseController
import com.orienteering.handrail.dialogs.ViewCourseInfoDialog
import com.orienteering.handrail.dialogs.ViewMarkerCourseControlDialog
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // TAG for log
    val TAG = "CourseActivity"

    // map fragment
    private lateinit var mapFragment: SupportMapFragment

    lateinit var course : Course
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
    // participant controller to manage participant services
    val courseController = CourseController()

    // check if running Q or later
    // additional permission required if so
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    // manage response of getEvents
    val getCourseCallback = object : Callback<StatusResponseEntity<Course>> {
        override fun onFailure(call: Call<StatusResponseEntity<Course>>, t: Throwable) {
            Log.e(TAG, "Failure getting performance")
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<Course>>,
            response: Response<StatusResponseEntity<Course>>
        ) {
            if (response.isSuccessful){
                Log.e(TAG, "Success getting course")
                course = response.body()?.entity!!
                if (course.courseName.isNotEmpty()){
                    textViewCourseName.text = course.courseName!!
                }
                addMarkers(course.courseControls)
                var allControlPoints : MutableList<LatLng> = mapUtilities.getAllControlPoints(course.courseControls)
                addRoute(allControlPoints)
                //Update to LatLngBounds. Define Method to calculate SW and NE corners
                courseMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mapUtilities.determineNESW(allControlPoints),200))
                Log.e(TAG,"Animating Camera to location")
                //(course.routePoints)
                initRecyclerView()
            } else {
                Log.e(TAG, "Failure getting course")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course2)
        
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_course_view) as SupportMapFragment

        textViewCourseName = findViewById(R.id.textView_course_view_name)
        createButtons()

        if(intent.extras!=null) {
            mapFragment.getMapAsync(this)
            courseId = intent.getSerializableExtra("COURSE_ID") as Int
        }
        getCourse()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        for (control in course.courseControls) {
            if (marker != null) {
                if (control.controlName.equals(marker.title)) {
                    displayControlDialog(control)
                    break;
                }
            }
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

            var courseAltitudes = mutableListOf<Double>()
            var courseLatLngs = mutableListOf<LatLng>()
            for (control in course.courseControls){
                control.createLatLng()
                if (control.controlAltitude!=null){
                    courseAltitudes.add(control.controlAltitude!!)
                }
                if (control.controlLatLng!=null){
                    courseLatLngs.add(control.controlLatLng)
                }
            }
            val totalDistance : Double = mapUtilities.calculateTotalDistance(courseLatLngs)
            val viewCourseInfoDialog : ViewCourseInfoDialog = ViewCourseInfoDialog(course.courseName,course.courseNote,courseAltitudes,totalDistance)
            viewCourseInfoDialog.show(mapFragment.childFragmentManager,"CourseInfo")
        }

        buttonExport.setOnClickListener() {

        }

        buttonDelete.setOnClickListener() {

        }
    }

    fun displayControlDialog(control : Control){
        val nameOfControl: String? = control.controlName
        var positionOfControl: Int? = control.controlPosition
        var imagePathOfControl: String? = control.controlPhotograph.photoPath
        var NoteOfControl: String? = control.controlNote

        val markerDialog : ViewMarkerCourseControlDialog = ViewMarkerCourseControlDialog(nameOfControl, NoteOfControl, positionOfControl, imagePathOfControl)
        markerDialog.show(mapFragment.childFragmentManager, "ViewMarkerDialog")
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
        setUpMap()
    }

    /**
     * Setup map and request permissions
     */
    private fun setUpMap() {

        Log.e(TAG,"Setting Up Map")
        courseMap.setOnMarkerClickListener(this)
        courseMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        courseMap.isMyLocationEnabled=false

        if(PermissionManager.checkPermission(this,this@CourseActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)
        )

            if (runningQOrLater) {
                PermissionManager.checkPermission(this,this@CourseActivity,
                    arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PermissionManager.BACKGROUND_PERMISSION_REQUEST_CODE)
            }


    }

    /**
     * Manage service call to get participant
     *
     */
    fun getCourse(){
        courseController.retrieve(courseId!!,getCourseCallback)
    }

    /**
     * add polyline routes
     */
    private fun addRoute(routePoints : List<LatLng>){

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

    /**
     * add control markers
     */
    private fun addMarkers(controls : List<Control>){
        for (control in controls){
            control.createLatLng()
            val markerOptions = MarkerOptions().position(control.controlLatLng).title(control.controlName)
            // add marker name
            markerOptions.title(control.controlName)
            // add marker to map
            courseMap.addMarker(markerOptions)
        }
    }

    /**
     *
     * initialises recycler view of participants
     */
    private fun initRecyclerView(){
        val recyclerView : RecyclerView = findViewById(R.id.rv_course_controls)
        val adapter = ControlsRecyclerViewAdapter(course.courseControls,this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
