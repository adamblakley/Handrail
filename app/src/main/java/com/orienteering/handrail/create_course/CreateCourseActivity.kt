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
import com.orienteering.handrail.course.CourseActivity
import com.orienteering.handrail.dialogs.*
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.image_utilities.ImageSelect

/**
 * View for the creation of a course. Allow user to create control and course via a map fragment, adding notes and images
 *
 */
class CreateCourseActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, StandardDialogListener, EventDialogListener,ICreateCourseContract.ICreateCourseView {
    // request check for location services onactvitiy result
    val REQUEST_CHECK_SETTINGS : Int = 2
    // GoogleMap variable
    private lateinit var map: GoogleMap
    // map fragment to display course
    private lateinit var mapFragment: SupportMapFragment
    // dialog for control create
    val createControlDialog: CreateControlDialog =
        CreateControlDialog()
    // performer
    lateinit var presenter : ICreateCourseContract.ICreateCoursePresenter
    // image select class to pick image
    lateinit var imageSelect : ImageSelect
    // add marker button
    lateinit var btnAddMarker : Button
    // save course button
    lateinit var btnSaveCourse : Button
    // pattern of polyline
    lateinit var pattern : MutableList<PatternItem>

    /**
     * Initialise map fragment, image select and presenter - request buttons and pattern be created and location requested
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // initialise map and view
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_course_create) as SupportMapFragment
        mapFragment.getMapAsync(this)
        imageSelect = ImageSelect(this, this@CreateCourseActivity)
        presenter = CreateCoursePresenter(this,CourseInteractor(),imageSelect)
        presenter.createLocationRequest()
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
        presenter.setUpMap()
    }

    /**
     * Create buttons and add onclick listeners
     *
     */
    fun createButtons() {
        //map buttons and add listeners
        btnAddMarker = findViewById<Button>(R.id.btn_add_marker)
        btnSaveCourse = findViewById<Button>(R.id.btn_save_course)

        btnAddMarker.setOnClickListener() {
            presenter.saveLatLng()
        }
        btnSaveCourse.setOnClickListener() { openSaveCourseDialog() }
    }

    /**
     * Pattern definitions for polyline for route
     *
     */
    fun definePattern(){
        pattern = mutableListOf<PatternItem>()
        pattern.add(Dot())
        pattern.add(Gap(10F))
        pattern.add(Dash(20F))
        pattern.add(Gap(10F))
    }

    /**
     * All control information display on control marker click
     *
     * @param marker
     * @return
     */
    override fun onMarkerClick(marker: Marker?): Boolean {
        val nameOfMarker: String? = marker?.title

        if (nameOfMarker != null) {
            presenter.getControlInformation(nameOfMarker)
        }
        return false
    }

    /**
     * Add terrain type and enable location view
     *
     */
    override fun setUpMap(){
        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }

    /**
     * display control information via marker dialog, called from presenter
     *
     * @param nameOfMarker
     * @param noteOfMarker
     * @param positionOfMarker
     * @param imageUriOfMarker
     */
    override fun onControlinformationSucess(nameOfMarker:String?, noteOfMarker: String?, positionOfMarker: Int?, imageUriOfMarker: Uri?){
        val markerDialog = ViewMarkerDialog(nameOfMarker, noteOfMarker, positionOfMarker, imageUriOfMarker)
        markerDialog.show(mapFragment.childFragmentManager, "ViewMarkerDialog")
    }

    /**
     * Show dialog for control creation on success of appropriate latlng value achieved
     *
     */
    override fun onSaveLatLngSuccess(){
        createControlDialog.show(mapFragment.childFragmentManager, "ControlDialog")
    }

    override fun onPolylineClick(p0: Polyline?) {
        // no onclick
    }

    override fun onPolygonClick(p0: Polygon?) {
        // no onclick
    }

    /**
     * Add Marker to map and set title
     */
    override fun addMapControl(name: String, note: String, currentLatLng: LatLng) {
        // create marker options that uses user location
        val markerOptions = MarkerOptions().position(currentLatLng)
        // add marker name
        markerOptions.title(name)
        // add marker to map
        map.addMarker(markerOptions)
    }

    /**
     * Allow user to save a course via dialog
     *
     */
    private fun openSaveCourseDialog() {

        if (presenter.getCourseLength()){
            val courseDialog = CreateCourseDialog()
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
        presenter.createCourse(name, note)
    }

    /**
     * Handles passed through text from dialog
     * Deprecated to createControl
     */
    override fun applyText(username: String, note: String) {
        presenter.createControl(username,note)
    }

    /**
     * return context
     *
     * @return
     */
    override fun returnContext(): Context {
        return this@CreateCourseActivity
    }

    /**
     * Return activity
     *
     * @return
     */
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
        Toast.makeText(this@CreateCourseActivity,"Error: Unable to process request, please try again",Toast.LENGTH_SHORT).show()
    }

    override fun onResponseFailure() {
        Toast.makeText(this@CreateCourseActivity,"Error: Failure to connect to service",Toast.LENGTH_SHORT).show()
    }

    override fun onLocationUpdateFailure() {
        Toast.makeText(this@CreateCourseActivity,"Error: Failure to acquire location",Toast.LENGTH_SHORT).show()
    }

    /**
     * start activity to view newly created course, end current activity
     *
     * @param courseId
     */
    override fun onPostResponseSuccess(courseId: Int) {
        Toast.makeText(this@CreateCourseActivity,"Success creating course course",Toast.LENGTH_SHORT).show()
        val intent = Intent(this@CreateCourseActivity, CourseActivity::class.java).apply {}
        intent.putExtra("COURSE_ID", courseId)
        this.startActivity(intent)
        finish()
    }


    /**
     * onactivity result allows locaiton update success to update state of location updates value
     * provide ability to save image uri on return from camera and gallery intent
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.updateLocationUpdateState()
            }
        }
        if (requestCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1001 -> {
                    // camera intent return
                    if (resultCode == Activity.RESULT_OK) {
                        presenter.setImage(imageSelect.tempImageUri)
                    } else {
                        Toast.makeText(this@CreateCourseActivity,"Error: Cannot use image",Toast.LENGTH_SHORT).show()
                    }
                }
                1002 -> {
                    // gallery intent return
                    imageSelect.checkExternalStoragePermission()
                    // set image if data is not empty
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        data.data?.let { presenter.setImage(it) }

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

