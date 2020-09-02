package com.orienteering.handrail.course_participation

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.interactors.PCPInteractor
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.models.PerformanceUploadRequest
import com.orienteering.handrail.utilities.App
import retrofit2.Response

/**
 * Manages access and posting of data from participate in event use case
 *
 * @constructor
 *
 * @param eventId
 * @param view
 * @param eventInteractor
 * @param pcpInteractor
 */
class CourseParticipationPresenter(eventId : Int, view: ICourseParticipationContract.ICourseActivity, eventInteractor: EventInteractor, pcpInteractor: PCPInteractor) : ICourseParticipationContract.ICoursePresenter{

    private var eventId: Int
    private var view : ICourseParticipationContract.ICourseActivity?
    private var eventInteractor : EventInteractor
    private var pcpInteractor : PCPInteractor

    init{
        this.eventId=eventId
        this.view=view
        this.eventInteractor=eventInteractor
        this.pcpInteractor=pcpInteractor
    }

    /**
     * use interactor to retrieve data
     *
     */
    override fun getDataFromDatabase(){
        val eventInteractor = EventInteractor()
        view?.let { GetEventOnFinishedListener(it) }?.let { eventInteractor.retreiveByID(eventId, it) }
    }

    /**
     * use interactor to push performance upload request to server
     *
     * @param performanceUploadRequest
     */
    override fun uploadParticipantControlPerformances(performanceUploadRequest : PerformanceUploadRequest){
        view?.let { PostPerformanceOnFinishedListener(it) }?.let { pcpInteractor.create(eventId,App.sharedPreferences.getLong(App.SharedPreferencesUserId,0),performanceUploadRequest, it) }
    }

    override fun onDestroy() {
        view = null
    }
}

/**
 * Onfinished listener aquires event information, calls activity to fill screen information and use to display route.
 * Provides error on failure
 */
class GetEventOnFinishedListener(view : ICourseParticipationContract.ICourseActivity) :
    IOnFinishedListener<Event> {
    // participants view
    private var view : ICourseParticipationContract.ICourseActivity = view

    /**
     * On successful response, ask view add event information via map information
     * If unsuccessful call view error response handler to display to user
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                response.body()!!.entity?.let { view.onEventGetSuccess(it) }
            } else {
                view.onEventGetError()
            }
        } else {
            view.onEventGetError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (view!=null){
            view.onEventGetFailure()
        }
    }
}

/**
 * Manages onfinished actions from posting performance, notifies user of success or failure
 *
 */
class PostPerformanceOnFinishedListener(view : ICourseParticipationContract.ICourseActivity) :
    IOnFinishedListener<Participant> {
    // Events view
    private var view : ICourseParticipationContract.ICourseActivity = view

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Participant>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                response.body()!!.entity?.let { view.onParticipantPostSuccess(it)
                }
            } else {
                view.onParticipantPostError()
            }
        } else {
            view.onParticipantPostError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (view!=null){
            view.onParticipantPostFailure()
        }
    }
}