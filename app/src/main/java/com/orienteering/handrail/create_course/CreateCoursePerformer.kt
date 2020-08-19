package com.orienteering.handrail.create_course

import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.image_utilities.ImageSelect
import com.orienteering.handrail.image_utilities.MultipartBodyFactory
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.location_updates.ILocationResponder
import com.orienteering.handrail.location_updates.LocationPerformer
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.permissions.PermissionManager
import com.orienteering.handrail.utilities.*
import okhttp3.MultipartBody
import retrofit2.Response

class CreateCoursePerformer(createCourseView : ICreateCourseContract.ICreateCourseView, courseInteractor: CourseInteractor, imageSelect: ImageSelect) : ICreateCourseContract.ICreateCoursePerformer,
    ILocationResponder {
    var courseInteractor : CourseInteractor
    var createCourseView : ICreateCourseContract.ICreateCourseView
    var postCoursetOnFinishedListener : IOnFinishedListener<Course>
    var locationPerfomer: LocationPerformer
    // image select
    var imageSelect : ImageSelect
    // create image files
    var multipartBodyFactory : MultipartBodyFactory
    // potential control latlng
    var potentialLatLng: LatLng = LatLng(0.0, 0.0)
    // potential control altitude
    var potentialAltitude: Double = 0.0
    // hashmap for image files
    var fileUris: HashMap<Int, Uri> = HashMap()

    // course for controls
    var controlsForCourse: MutableList<Control> = mutableListOf<Control>()

    //coordinates array for polyline
    val listOfRoutePoints: MutableList<LatLng> = mutableListOf()

    init{
        this.courseInteractor = courseInteractor
        this.createCourseView = createCourseView
        this.postCoursetOnFinishedListener = PostCourseOnFinishedListener(this,createCourseView)
        this.imageSelect=imageSelect
        this.multipartBodyFactory = MultipartBodyFactory(imageSelect)
        this.locationPerfomer= LocationPerformer(this, createCourseView.returnContext(), createCourseView.returnActivity())
    }

    /**
     * Update location
     */
    override fun createLocationRequest() {
        locationPerfomer.createLocationRequest()
    }

    override fun updateLocationUpdateState(){
        locationPerfomer.updateLocationUpdateState()
    }

    /**
     * Store image uri in hashmap, key = control, value = imageURI
     *
     * @param imageUri
     */
    override fun setImage(imageUri: Uri) {
        if (imageUri != null) {
            fileUris.put(controlsForCourse.size, imageUri)
        }
    }

    /**
     * Request fine location, find current location and locate device
     */
    override fun setUpMap() {
        if (PermissionManager.checkPermission(createCourseView.returnActivity(), createCourseView.returnContext(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)) {

            createCourseView.setUpMap()
        }
    }

    /**
     * Save current latlng for potential control
     * @return
     */
    override fun saveLatLng(){
        if (potentialLatLng.latitude != 0.0 && potentialLatLng.longitude != 0.0) {
            if (PermissionManager.checkPermission(createCourseView.returnActivity(), createCourseView.returnContext(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)) {
                createCourseView.onSaveLatLngSuccess()
            }
        } else {
            createCourseView.onLocationUpdateFailure()
        }
    }

    override fun createControl(name : String, note : String) {
        val calendar = java.util.Calendar.getInstance()
        val date = calendar.time
        val control = Control(name, note, potentialLatLng.latitude, potentialLatLng.longitude, potentialAltitude, date)
        controlsForCourse.add(control)
        control.controlPosition = controlsForCourse.size
        createCourseView.addMapControl(name,note,potentialLatLng)
        imageSelect.selectImage()
    }

    /**
     * create an course using variable course and passed through name and note
     *
     * @param name
     */
    override fun createCourse(name: String, note : String) {
        val course = Course(controlsForCourse, name,note)
        var files: Array<MultipartBody.Part?> = arrayOfNulls(controlsForCourse.size)
        for ((key, value) in fileUris) {
            val position = key
            val positionMinusOne = position - 1
            val multipartBodyPart: MultipartBody.Part = multipartBodyFactory.createImageMultipartBody(createCourseView.returnActivity(),value, controlsForCourse[positionMinusOne].controlName)
            files.set(positionMinusOne, multipartBodyPart)
        }
        courseInteractor.uploadCourse(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0), course, files, postCoursetOnFinishedListener)
    }

    override fun getCourseLength(): Boolean {
        return controlsForCourse.size>0
    }

    override fun getControlInformation(nameOfControl: String) {
        val nameOfMarker: String? = nameOfControl
        var positionOfMarker: Int? = null
        var imageUriOfMarker: Uri? = null
        var noteOfMarket: String? = null

        for (control in controlsForCourse) {
            if (control.controlName.equals(nameOfControl)) {
                positionOfMarker = control.controlPosition
                noteOfMarket = control.controlNote
                if (fileUris.containsKey(positionOfMarker)) {
                    imageUriOfMarker = fileUris.get(positionOfMarker)
                }
            }
        }
        createCourseView.onControlinformationSucess(nameOfMarker,noteOfMarket,positionOfMarker,imageUriOfMarker)
    }

    override fun locationCallback(lastLocation: Location) {
        if (lastLocation != null) {
            potentialAltitude = lastLocation.altitude
            potentialLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        } else {
            potentialLatLng = LatLng(0.0, 0.0)
            createCourseView.onLocationUpdateFailure()
        }
        listOfRoutePoints.add(potentialLatLng)

        createCourseView.updateDisplay(potentialLatLng,listOfRoutePoints)
    }

    override fun startLocationUpdatesFailure() {
        Log.e("TAG", "Unable to find location -> Location = null")
        Toast.makeText(createCourseView.returnContext(), "Unable to find current location", Toast.LENGTH_SHORT).show()
    }
}


class PostCourseOnFinishedListener(createCoursePerformer : ICreateCourseContract.ICreateCoursePerformer, createCourseView : ICreateCourseContract.ICreateCourseView) : IOnFinishedListener<Course> {
    // Events view
    private var createCourseView : ICreateCourseContract.ICreateCourseView
    // Events presenter
    private var createCoursePerformer : ICreateCourseContract.ICreateCoursePerformer

    /**
     * Initialises view, presenter
     */
    init{
        this.createCoursePerformer = createCoursePerformer
        this.createCourseView = createCourseView
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Course>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                response.body()!!.entity?.courseId?.let { createCourseView.onPostResponseSuccess(it) }
            } else {
                createCourseView.onResponseError()
            }
        } else {
            createCourseView.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (createCourseView!=null){
            createCourseView.onResponseFailure()
        }
    }
}