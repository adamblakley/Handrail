package com.orienteering.handrail.course

import android.content.Context
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.gpx_utilities.GPXBuilder
import com.orienteering.handrail.map_utilities.MapUtilities
import retrofit2.Response

class ICoursePresenter(courseId : Int,courseView : ICourseContract.ICourseView, courseInteractor: CourseInteractor) : ICourseContract.ICoursePresenter {
    var courseId : Int
    lateinit var course : Course

    var courseView : ICourseContract.ICourseView
    var courseInteractor : CourseInteractor

    var getCourseOnFinishedListener : GetCourseOnFinishedListener
    var deleteCourseOnFinishedListener : DeleteCourseOnFinishedListener

    init{
        this.courseId = courseId
        this.courseView=courseView
        this.courseInteractor=courseInteractor
        this.getCourseOnFinishedListener = GetCourseOnFinishedListener(this,courseView)
        this.deleteCourseOnFinishedListener = DeleteCourseOnFinishedListener(this,courseView)
    }

    override fun requestDataFromServer() {
        courseInteractor.retrieve(courseId,getCourseOnFinishedListener)
    }

    override fun removeDataFromServer() {
        courseInteractor.deleteCourse(courseId,deleteCourseOnFinishedListener)
    }

    override fun setPresenterCourse(course: Course) {
        this.course=course
    }

    override fun courseInformation() {
        val mapUtilities = MapUtilities()
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
        courseView.showInformation(course.courseName,course.courseNote,courseAltitudes,totalDistance)
    }

    fun findControlByTitle(controlName : String): Control? {
        for (control in course.courseControls) {
            if (controlName != null) {
                if (control.controlName.equals(controlName)) {
                    return control
                    break;
                }
            }
        }
        return null
    }

    override fun getControls() {
        var controlNameLatLng = mutableMapOf<String,LatLng>()
        for (control in course.courseControls){
            control.createLatLng()
            controlNameLatLng.put(control.controlName,control.controlLatLng)
        }
        courseView.addControls(controlNameLatLng)
    }

    override fun controlInformation(markerTitle : String) {
        val control = findControlByTitle(markerTitle)
        val nameOfControl: String? = control?.controlName
        var positionOfControl: Int? = control?.controlPosition
        var imagePathOfControl: String? = null
        if (control != null) {
            if (control.isControlPhotographInitialised()){
                for (photo in control.controlPhotographs){
                    if (photo.active!!){
                        imagePathOfControl=photo.photoPath
                    }
                }
            }
        }
        var noteOfControl: String? = control?.controlNote
        courseView.showControlInformation(nameOfControl,noteOfControl,positionOfControl,imagePathOfControl)
    }

    fun provideBounds(controls : List<Control>) : LatLngBounds {
        val mapUtilities = MapUtilities()
        var controlLatLngs = mutableListOf<LatLng>()
        for (control in controls){
            control.createLatLng()
            controlLatLngs.add(control.controlLatLng)
        }
        val bounds : LatLngBounds = mapUtilities.determineNESW(controlLatLngs)
        return bounds
    }

    override fun getRoute(controls: List<Control>) {
        val mapUtilities = MapUtilities()
        var allControlPoints = mapUtilities.getAllControlPoints(controls)
        val bounds = provideBounds(controls)
        courseView.showRoute(allControlPoints,bounds)
    }

    /**
     * Check build version
     * Request GPX Build with GPXBuilder
     * @param context
     */
    override fun generateFile(context : Context) {
        // utilities to build gpx file on request
        val gpxBuilder = GPXBuilder(context)

        // check build version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            gpxBuilder.buildGPX(course.courseControls)
        } else {
            courseView.showMessage("Error: Incompatible Device")
        }
    }
}

/**
 * Listener handles interactor responses
 *
 * @param eventsPresenter
 * @param eventsView
 */
class GetCourseOnFinishedListener(coursePerformer : ICourseContract.ICoursePresenter, courseView : ICourseContract.ICourseView) : IOnFinishedListener<Course> {
    // Events view
    private var courseView : ICourseContract.ICourseView
    // Events presenter
    private var coursePerformer : ICourseContract.ICoursePresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.coursePerformer = coursePerformer
        this.courseView = courseView
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Course>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                coursePerformer.setPresenterCourse(response.body()?.entity!!)
                coursePerformer.getControls()
                courseView.onGetResponseSuccess(response.body()?.entity!!.courseControls,response.body()?.entity!!.courseName)
            } else {
                courseView.onResponseError()
            }
        } else {
            courseView.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (courseView!=null){
            courseView.onResponseFailure(t)
        }
    }
}

/**
 * Listener handles interactor responses
 *
 * @param eventsPresenter
 * @param eventsView
 */
class DeleteCourseOnFinishedListener(coursePerformer : ICourseContract.ICoursePresenter, courseView : ICourseContract.ICourseView) : IOnFinishedListener<Boolean> {
    // Events view
    private var courseView : ICourseContract.ICourseView
    // Events presenter
    private var coursePerformer : ICourseContract.ICoursePresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.coursePerformer = coursePerformer
        this.courseView = courseView
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                courseView.onDeleteResponseSuccess()
            } else {
                courseView.onResponseError()
            }
        } else {
            courseView.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (courseView!=null){
            courseView.onResponseFailure(t)
        }
    }
}