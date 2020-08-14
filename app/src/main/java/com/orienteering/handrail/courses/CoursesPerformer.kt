package com.orienteering.handrail.courses

import com.orienteering.handrail.IOnFinishedListener
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
class CoursesPerformer(coursesView : ICoursesContract.ICoursesView, courseInteractor: CourseInteractor) : ICoursesContract.ICoursesPerformer {

    // Coursesview
    private var coursesView : ICoursesContract.ICoursesView?
    // Courses interactor for service requests
    private var courseInteractor: CourseInteractor
    // Listener to handles interactor responses
    private var getCoursesOnFinishedListener : IOnFinishedListener<List<Course>>

    init{
        this.coursesView = coursesView
        this.courseInteractor = courseInteractor
        this.getCoursesOnFinishedListener = GetCoursesOnFinishedListener(this,coursesView)
    }
    override fun onDestroy() {
        coursesView=null
    }

    override fun requestDataFromServer() {
        courseInteractor.retrieveAllByUser(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getCoursesOnFinishedListener)
    }
}

/**
 * Listener handles interactor responses
 *
 * @constructor
 *
 *
 * @param eventsPresenter
 * @param eventsView
 */
class GetCoursesOnFinishedListener(coursePerformer : ICoursesContract.ICoursesPerformer, coursesView : ICoursesContract.ICoursesView) : IOnFinishedListener<List<Course>>{
    // Events view
    private var coursesView : ICoursesContract.ICoursesView
    // Events presenter
    private var coursesPerformer : ICoursesContract.ICoursesPerformer

    /**
     * Initialises view, presenter
     */
    init{
        this.coursesPerformer = coursePerformer
        this.coursesView = coursesView
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<List<Course>>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                coursesView.fillRecyclerView(response.body()!!.entity as ArrayList<Course>)
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