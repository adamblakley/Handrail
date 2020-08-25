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
// TAG for Logs
private val TAG: String = CoursePresenter::class.java.name

/**
 * Class manages the logic of obtaining, manipulating and providing course information for display to view
 *
 * @constructor
 *
 * @param courseId
 * @param courseView
 * @param courseInteractor
 */
class CoursePresenter(courseId : Int, courseView : ICourseContract.ICourseView, courseInteractor: CourseInteractor) : ICourseContract.ICoursePresenter {
    // course id for use in obtaining course
    private var courseId : Int
    // course obtained from data source
    lateinit var course : Course
    // view required to display course data
    private var courseView : ICourseContract.ICourseView
    // interactor to obtain course information
    private var courseInteractor : CourseInteractor
    // on finished listener handle receipt of course data
    private var getCourseOnFinishedListener : GetCourseOnFinishedListener
    // on finished listener handle receipt of delete course response
    private var deleteCourseOnFinishedListener : DeleteCourseOnFinishedListener

    /**
     * Initialise class variables
     */
    init{
        this.courseId = courseId
        this.courseView=courseView
        this.courseInteractor=courseInteractor
        this.getCourseOnFinishedListener = GetCourseOnFinishedListener(this,courseView)
        this.deleteCourseOnFinishedListener = DeleteCourseOnFinishedListener(this,courseView)
    }

    /**
     * Request data directly from interactor
     *
     */
    override fun requestDataFromServer() {
        courseInteractor.retrieve(courseId,getCourseOnFinishedListener)
    }

    /**
     * utilize interactor to request removal of data from source
     *
     */
    override fun removeDataFromServer() {
        courseInteractor.deleteCourse(courseId,deleteCourseOnFinishedListener)
    }

    /**
     * Set this course as passed
     * @param course
     */
    override fun setPresenterCourse(course: Course) {
        this.course=course
    }

    /**
     * Disseminate course information from interactor response and choose correct control and altitude values
     * Calculate total distance
     * Provide information to view
     */
    override fun courseInformation() {
        val mapUtilities = MapUtilities()
        val courseAltitudes = mutableListOf<Double>()
        val courseLatLngs = mutableListOf<LatLng>()
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

    /**
     *  method determines control by passed title string
     * @param controlName
     * @return
     */
    private fun findControlByTitle(controlName : String): Control? {
        for (control in course.courseControls) {
            if (controlName != null) {
                if (control.controlName == controlName) {
                    return control
                }
            }
        }
        return null
    }

    /**
     * get all controls and output to map for courseview
     *
     */
    override fun getControls() {
        val controlNameLatLng = mutableMapOf<String,LatLng>()
        for (control in course.courseControls){
            control.createLatLng()
            controlNameLatLng[control.controlName] = control.controlLatLng
        }
        courseView.addControls(controlNameLatLng)
    }

    /**
     * Obtain control information and disseminate, pass to view to show data
     *
     * @param markerTitle
     */
    override fun controlInformation(markerTitle : String) {
        val control = findControlByTitle(markerTitle)
        val nameOfControl: String? = control?.controlName
        val positionOfControl: Int? = control?.controlPosition
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
        val noteOfControl: String? = control?.controlNote
        courseView.showControlInformation(nameOfControl,noteOfControl,positionOfControl,imagePathOfControl)
    }

    /**
     * Provide latitude and longitude bounds for display in map
     *
     * @param controls
     * @return
     */
    private fun provideBounds(controls : List<Control>) : LatLngBounds {
        val mapUtilities = MapUtilities()
        val controlLatLngs = mutableListOf<LatLng>()
        for (control in controls){
            control.createLatLng()
            controlLatLngs.add(control.controlLatLng)
        }
        // utilize map utilities method to determine NESW
        return mapUtilities.determineNESW(controlLatLngs)
    }

    /**
     * get route from all controls and provide control points and bound to view
     * @param controls
     */
    override fun getRoute(controls: List<Control>) {
        val mapUtilities = MapUtilities()
        val allControlPoints = mapUtilities.getAllControlPoints(controls)
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
 * Listener class, handles response from interactor callback of get course
 *
 * @constructor
 *
 * @param presenter
 * @param view
 */
class GetCourseOnFinishedListener(presenter : ICourseContract.ICoursePresenter, view : ICourseContract.ICourseView) :
    IOnFinishedListener<Course> {
    // Course view
    private var view : ICourseContract.ICourseView
    // Course presenter
    private var presenter : ICourseContract.ICoursePresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.presenter = presenter
        this.view = view
    }

    /**
     * On successful response, ask presenter to set course, get controls and view to display via the ongetresponsesuccess method
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Course>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                presenter.setPresenterCourse(response.body()?.entity!!)
                presenter.getControls()
                view.onGetResponseSuccess(response.body()?.entity!!.courseControls,response.body()?.entity!!.courseName)
            } else {
                view.onResponseError()
            }
        } else {
            view.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        view.onResponseFailure(t)
    }
}

/**
 * Listener handles interactor responses for deleting course, success provides positive message for view to handle
 * Failure and error mapped to view responses
 * @constructor
 *
 * @param presenter
 * @param view
 */
class DeleteCourseOnFinishedListener(presenter : ICourseContract.ICoursePresenter, view : ICourseContract.ICourseView) :
    IOnFinishedListener<Boolean> {
    // Course view
    private var view : ICourseContract.ICourseView
    // Course presenter
    private var presenter : ICourseContract.ICoursePresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.presenter = presenter
        this.view = view
    }

    /**
     * On successful response, ask view to fill recycler view with course information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                view.onDeleteResponseSuccess()
            } else {
                view.onResponseError()
            }
        } else {
            view.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        view.onResponseFailure(t)
    }
}