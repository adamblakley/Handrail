package com.orienteering.handrail.create_course

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.orienteering.handrail.R
import com.orienteering.handrail.course.ICourseActivity
import com.orienteering.handrail.dialogs.*
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.image_utilities.ImageSelect

class CreateCourseActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, ExampleDialogListener, EventDialogListener,ICreateCourseContract.ICreateCourseView {

    val REQUEST_CHECK_SETTINGS : Int = 2
    // GoogleMap variable
    private lateinit var map: GoogleMap
    // map fragment
    private lateinit var mapFragment: SupportMapFragment
    // dialog for control create
    val createControlDialog: CreateControlDialog = CreateControlDialog()
    // performer
    lateinit var performer : ICreateCourseContract.ICreateCoursePerformer
    // image select
    lateinit var imageSelect : ImageSelect
    // add marker
    lateinit var btnAddMarker : Button
    // save course
    lateinit var btnSaveCourse : Button
    // pattern of polyline
    lateinit var pattern : MutableList<PatternItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapFragment = supportFragmentManager.findFragmentById(com.orienteering.handrail.R.id.map_course_create) as SupportMapFragment
        mapFragment.getMapAsync(this)
        imageSelect = ImageSelect(
            this,
            this@CreateCourseActivity
        )
        performer = CreateCoursePerformer(this,CourseInteractor(),imageSelect)
        performer.createLocationRequest()
        definePattern()
        createButtons()
    }

    /**
     * Manipulates the map once available.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.setZoomControlsEnabled(false)
        map.uiSettings.setCompassEnabled(false)
        map.uiSettings.setMyLocationButtonEnabled(false)
        map.uiSettings.setAllGesturesEnabled(false)
        map.setOnMarkerClickListener(this)
        performer.setUpMap()
    }

    fun createButtons() {
        //map buttons and add listeners
        btnAddMarker = findViewById<Button>(com.orienteering.handrail.R.id.btn_add_marker)
        btnSaveCourse = findViewById<Button>(com.orienteering.handrail.R.id.btn_save_course)

        btnAddMarker.setOnClickListener() {
            performer.saveLatLng()
        }
        btnSaveCourse.setOnClickListener() { openSaveEventDialog() }
    }

    fun definePattern(){
        pattern.add(Dot())
        pattern.add(Gap(10F))
        pattern.add(Dash(20F))
        pattern.add(Gap(10F))
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val nameOfMarker: String? = marker?.title

        if (nameOfMarker != null) {
            performer.getControlInformation(nameOfMarker)
        }
        return false
    }

    override fun setUpMap(){
        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }

    override fun onControlinformationSucess(nameOfMarker:String?, noteOfMarker: String?, positionOfMarker: Int?, imageUriOfMarker: Uri?){
        val markerDialog: ViewMarkerDialog = ViewMarkerDialog(nameOfMarker, noteOfMarker, positionOfMarker, imageUriOfMarker)
        markerDialog.show(mapFragment.childFragmentManager, "ViewMarkerDialog")
    }

    override fun onSaveLatLngSuccess(){
        createControlDialog.show(mapFragment.childFragmentManager, "ExampleDialog")
    }

    override fun onPolylineClick(p0: Polyline?) {
        // no onclick
    }

    override fun onPolygonClick(p0: Polygon?) {
        // no onclick
    }

    /**
     * Add Marker to map
     */
    override fun addMapControl(name: String, note: String, currentLatLng: LatLng) {
        // create marker options that uses user location
        val markerOptions = MarkerOptions().position(currentLatLng)
        // add marker name
        markerOptions.title(name)
        // add marker to map
        map.addMarker(markerOptions)
    }

    fun openSaveEventDialog() {

        if (performer.getCourseLength()){
            val courseDialog: CreateCourseDialog = CreateCourseDialog()
            courseDialog.show(mapFragment.childFragmentManager, "CreateEventDialog")
        } else {
            Toast.makeText(this,"Error: Add at least one control",Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Create event return text from dialog
     * @name
     * @note
     */
    override fun applyEventText(name: String, note: String) {
        performer.createCourse(name, note)
    }

    /**
     * Handles passed through text from dialog
     * Deprecated to createControl
     */
    override fun applyText(name: String, note: String) {
        performer.createControl(name,note)
    }

    override fun returnContext(): Context {
        return this@CreateCourseActivity
    }

    override fun returnActivity(): Activity {
        return this
    }

    /**
     * Responsible for updating the map display upon receipt of latitude and longitude values from the presenter
     * @param currentLatlng
     * @param routePoints
     */
    override fun updateDisplay(currentLatlng: LatLng, routePoints: List<LatLng>) {
        animateMapCamera(currentLatlng)
        addMapPolyline(routePoints)
    }

    /**
     * calls the animateCamera method of the Map object. centering the map on the new latitude and longitude
     * @param currentLatlng
     */
    fun animateMapCamera(currentLatlng: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, 15f))
    }

    /**
     * creates a polyline fresh from a new list of LatLng provided by the presenter class, adding the pattern defined in the definePatter() method
     * @param routePoints
     */
    fun addMapPolyline(routePoints: List<LatLng>) {
        map.addPolyline(
            PolylineOptions()
                .addAll(routePoints)
                .width(10f)
                .color(Color.RED)
                .pattern(pattern)
        )
    }

    override fun onResponseError() {
        Toast.makeText(this@CreateCourseActivity,"Error: Unable to process request, please try again",Toast.LENGTH_SHORT)
    }

    override fun onResponseFailure() {
        Toast.makeText(this@CreateCourseActivity,"Error: Failure to connect to service",Toast.LENGTH_SHORT)
    }

    override fun onLocationUpdateFailure() {
        Toast.makeText(this@CreateCourseActivity,"Error: Failure to acquire location",Toast.LENGTH_SHORT)
    }

    override fun onPostResponseSuccess(courseId: Int) {
        Toast.makeText(this@CreateCourseActivity,"Success creating course course",Toast.LENGTH_SHORT)
        val intent = Intent(this@CreateCourseActivity, ICourseActivity::class.java).apply {}
        intent.putExtra("COURSE_ID", courseId)
        this.startActivity(intent)
    }

    /**
     * override AppCompatAcitivy's onActivityResult() method and start update request if result_ok under request check settings
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                performer.updateLocationUpdateState()
            }
        }
        if (requestCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1001 -> {
                    Log.e("CreateCourse", "Request 1001")
                    if (resultCode == Activity.RESULT_OK) {
                        performer.setImage(imageSelect.tempImageUri)
                    } else {
                        Toast.makeText(this@CreateCourseActivity,"Error: Cannot use image",Toast.LENGTH_SHORT).show()
                    }
                }
                1002 -> {
                    val permission = imageSelect.checkExternalStoragePermission()
                    Log.e("FileWriter", "Permission check = $permission")

                    Log.e("CreateCourse", "Request 1002")
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        data.data?.let { performer.setImage(it) }

                    } else {
                        Toast.makeText(this@CreateCourseActivity,"Error: Cannot use image",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Log.e("CreateCourse", "Request cancelled...")
        }
    }

}

