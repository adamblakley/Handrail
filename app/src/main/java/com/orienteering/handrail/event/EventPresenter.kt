package com.orienteering.handrail.event

import android.util.Log
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.models.Event;
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.interactors.ParticipantInteractor
import com.orienteering.handrail.utilities.App
import retrofit2.Response
import java.text.SimpleDateFormat

/**
 * Presenter contains all logic to retrieve, post and manipulate model for view event, join, leave, start and delete event
 *
 * @constructor
 *
 * @param eventId
 * @param eventView
 * @param eventInteractor
 * @param participantInteractor
 */
class EventPresenter(eventId : Int, eventView : IEventContract.IEventView, eventInteractor: EventInteractor, participantInteractor: ParticipantInteractor) : IEventContract.IEventPresenter {

    private var event : Event? = null
    private var eventId : Int = 0
    private var eventView : IEventContract.IEventView?
    private var eventInteractor: EventInteractor
    private var participantInteractor : ParticipantInteractor
    // on finished listeners for each request from backend
    private var getEventOnFinishedListener : GetEventOnFinishedListener
    private var joinEventOnFinishedListener : JoinEventOnFinishedListener
    private var updateEventOnFinishedListener : UpdateEventOnFinishedListener
    private var leaveEventOnFinishedListener : LeaveEventOnFinishedListener
    private var deleteEventOnFinishedListener : DeleteEventOnFinishedListener

    /**
     * initialise view, interactors and onfinished listeners
     */
    init{
        this.eventView = eventView
        this.eventInteractor = eventInteractor
        this.participantInteractor = participantInteractor
        this.getEventOnFinishedListener = GetEventOnFinishedListener(this,eventView)
        this.joinEventOnFinishedListener = JoinEventOnFinishedListener(this,eventView)
        this.updateEventOnFinishedListener = UpdateEventOnFinishedListener(this,eventView)
        this.leaveEventOnFinishedListener = LeaveEventOnFinishedListener(this,eventView)
        this.deleteEventOnFinishedListener = DeleteEventOnFinishedListener(this,eventView)
        this.eventId = eventId
    }

    override fun setEvent(event: Event) {
        this.event = event
    }

    override fun onDestroy() {
        eventView = null
    }

    /**
     * provide event id to interactor to request event information
     *
     */
    override fun requestDataFromServer() {
        eventId.let {eventInteractor.retreiveByID(it,getEventOnFinishedListener) }
    }

    /**
     * user interactor to make update request for event
     *
     */
    override fun updateEventStatus() {
        eventId.let { eventInteractor.updateStatus(it,updateEventOnFinishedListener) }
    }

    override fun startEvent() {
        eventId.let { eventView?.startCourseParticipationActivity(it) }
    }

    /**
     * request to join event by padding user id and event id to interactor
     *
     */
    override fun joinEvent() {
        eventId.let { participantInteractor.createParticipant(it, App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),joinEventOnFinishedListener) }
    }

    /**
     * request to leave event by padding user id and event id to interactor
     *
     */
    override fun leaveEvent() {
        eventId.let { participantInteractor.removeParticipant(it,App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),leaveEventOnFinishedListener) }
    }

    /**
     * request to delete event by padding user id and event id to interactor
     *
     */
    override fun deleteEvent() {
        eventId.let { eventInteractor.deleteEvent(it,deleteEventOnFinishedListener) }
    }

    override fun showResults(){
        eventId.let { eventView?.startEventResultsActivity(it) }
    }
}

/**
 *  get event information onfinsihed listener, determines success of response and provides information to view toupdate interface
 *
 * @constructor
 * TODO
 *
 * @param eventPresenter
 * @param eventView
 */
class GetEventOnFinishedListener(eventPresenter : IEventContract.IEventPresenter, eventView : IEventContract.IEventView) :
    IOnFinishedListener<Event> {

    lateinit var event : Event
    private var eventView : IEventContract.IEventView
    private var eventPresenter : IEventContract.IEventPresenter

    init{
        this.eventView = eventView
        this.eventPresenter = eventPresenter
    }

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {

        lateinit var eventDate : String
        lateinit var eventTime : String
        var userIsParticipant = false

        if(response.isSuccessful){
            if (response.body() != null) {
                event = response.body()!!.entity!!
                // update date format to text for display
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

                val dateformatted = sdf.parse(event.eventDate)

                val dateFormatter = SimpleDateFormat("dd-MM-YYYY")
                val timeFormatter = SimpleDateFormat("HH:mm")
                eventDate = dateFormatter.format(dateformatted)
                eventTime = timeFormatter.format(dateformatted)
                // check if user is organiser and setup display by checking user id against organiser id
                if (event.eventOrganiser.userId == App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0)) {
                    eventView.setupForOrganizer(event.eventStatus)
                } else {
                    // else check if user is participanty. first check if participants is empty
                    if (!event.participants.isEmpty()) {
                        for (participant in event.participants) {
                            // check user id against participants list
                            if (participant.participantUser.userId?.equals(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0))!! && participant.participantControlPerformances.isEmpty()) {
                                userIsParticipant=true
                                break
                            }
                        }
                    }
                    eventView.setupForUser(event.eventStatus,userIsParticipant)
                }
                //set event in presenter
                eventPresenter.setEvent(event)
                response.body()!!.entity?.let { eventView.fillInformation(event.eventName,event.eventNote,eventDate,eventTime) }
                for (photo in response.body()!!.entity?.eventPhotographs!!){
                    if (photo.active!!){
                        eventView.setupImage(photo.photoPath)
                        break
                    }
                }
            } else {
                Log.e("TAG","problem 1")
                eventView.onResponseError()
            }
        } else {
            Log.e("TAG","problem 2")
            eventView.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView.onResponseFailure(t)
        }
    }
}

/**
 * handles response from join event and updates user interface on success, displaying error on failure
 *
 * @constructor
 * TODO
 *
 * @param eventPresenter
 * @param eventView
 */
class JoinEventOnFinishedListener(eventPresenter : IEventContract.IEventPresenter, eventView : IEventContract.IEventView) :
    IOnFinishedListener<Event> {

    private var eventView : IEventContract.IEventView = eventView
    private var eventPresenter : IEventContract.IEventPresenter = eventPresenter

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventView.makeToast("Event Successfully Joined")
                response.body()!!.entity?.let { eventView.setupForUser(it.eventStatus,true) }
            } else {
                eventView.onResponseError()
            }
        } else {
            eventView.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}

/**
 * handles update request response, updating display on success, informing user on error
 *
 * @constructor
 * TODO
 *
 * @param eventPresenter
 * @param eventView
 */
class UpdateEventOnFinishedListener(eventPresenter : IEventContract.IEventPresenter, eventView : IEventContract.IEventView) :
    IOnFinishedListener<Event> {

    private var eventView : IEventContract.IEventView = eventView
    private var eventPresenter : IEventContract.IEventPresenter = eventPresenter

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventView.makeToast("Event Status Updated")
                response.body()!!.entity?.let { eventPresenter.setEvent(it) }
                response.body()!!.entity?.let { eventView.setupForOrganizer(it.eventStatus) }
            } else {
                eventView.onResponseError()
            }
        } else {
            eventView.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}

/**
 * Handles leave event response from callback
 *
 * @constructor
 * TODO
 *
 * @param eventPresenter
 * @param eventView
 */
class LeaveEventOnFinishedListener(eventPresenter : IEventContract.IEventPresenter, eventView : IEventContract.IEventView) :
    IOnFinishedListener<Event> {

    private var eventView : IEventContract.IEventView = eventView
    private var eventPresenter : IEventContract.IEventPresenter = eventPresenter

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventView.makeToast("Event Successfully Left")
                eventView.startViewEventsActivity()
            } else {
                eventView.onResponseError()
            }
        } else {
            eventView.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}

/**
 * Handles delete event response from callback
 *
 * @constructor
 * TODO
 *
 * @param eventPresenter
 * @param eventView
 */
class DeleteEventOnFinishedListener(eventPresenter : IEventContract.IEventPresenter, eventView : IEventContract.IEventView) :
    IOnFinishedListener<Boolean> {

    private var eventView : IEventContract.IEventView = eventView
    private var eventPresenter : IEventContract.IEventPresenter = eventPresenter

    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventView.makeToast("Event Successfully Deleted")
                eventView.startViewEventsActivity()
            } else {
                eventView.onResponseError()
            }
        } else {
            eventView.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}