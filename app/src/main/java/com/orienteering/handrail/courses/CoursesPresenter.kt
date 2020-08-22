package com.orienteering.handrail.courses

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.utilities.App
import retrofit2.Response

/**
 * Handles CoursesActivity logic and access courseInteractor for service requests
 *
 * @constructor
 *
 * @param coursesView
 * @param courseInteractor
 */
class CoursesPresenter(coursesView : ICoursesContract.ICoursesView, courseInteractor: CourseInteractor) : ICoursesContract.ICoursesPresenter {

    // Coursesview
    private var coursesView : ICoursesContract.ICoursesView?
    // Courses interactor for service requests
    private var courseInteractor: CourseInteractor
    // Listener to handles interactor responses
    private var getCoursesOnFinishedListener : IOnFinishedListener<List<Course>>

    /**
     * Initialise views, interactor and onfinished listener for interactor
     */
    init{
        this.coursesView = coursesView
        this.courseInteractor = courseInteractor
        this.getCoursesOnFinishedListener = GetCoursesOnFinishedListener(coursesView)
    }

    /**
     * Destroy view
     *
     */
    override fun onDestroy() {
        coursesView=null
    }

    /**
     * Request data via interactor, use user id from shared preferences and apply onfinished listener
     *
     */
    override fun requestDataFromServer() {
        courseInteractor.retrieveAllByUser(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getCoursesOnFinishedListener)
    }
}


/**
 * handle on finished interactions from retreival of course data
 *
 * @constructor
 *
 * @param coursePresenter
 * @param coursesView
 */
class GetCoursesOnFinishedListener(coursesView : ICoursesContract.ICoursesView) :
    IOnFinishedListener<List<Course>> {

    private var coursesView : ICoursesContract.ICoursesView = coursesView

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<List<Course>>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                coursesView.fillInformation(response.body()!!.entity as ArrayList<Course>)
            } else {
                coursesView.onResponseError()
            }
        } else {
            coursesView.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (coursesView!=null){
            coursesView.onResponseFailure(t)
        }
    }

}