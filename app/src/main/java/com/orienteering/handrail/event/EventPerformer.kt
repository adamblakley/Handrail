package com.orienteering.handrail.event

import android.util.Log
import com.orienteering.handrail.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.models.Event;
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.interactors.ParticipantInteractor
import com.orienteering.handrail.utilities.App
import retrofit2.Response
import java.text.SimpleDateFormat

class EventPerformer(eventId : Int, eventView : IEventContract.IEventView, eventInteractor: EventInteractor, participantInteractor: ParticipantInteractor) : IEventContract.IEventPerformer {

    private var event : Event? = null
    private var eventId : Int = 0
    private var eventView : IEventContract.IEventView?
    private var eventInteractor: EventInteractor
    private var participantInteractor : ParticipantInteractor

    private var getEventOnFinishedListener : GetEventOnFinishedListener
    private var joinEventOnFinishedListener : JoinEventOnFinishedListener
    private var updateEventOnFinishedListener : UpdateEventOnFinishedListener
    private var leaveEventOnFinishedListener : LeaveEventOnFinishedListener
    private var deleteEventOnFinishedListener : DeleteEventOnFinishedListener

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

    override fun requestDataFromServer() {
        Log.e("TAG","I've been hit!")
         eventId?.let {eventInteractor.retreiveByID(it,getEventOnFinishedListener) }
    }

    override fun updateEventStatus() {
        eventId?.let { eventInteractor.updateStatus(it,updateEventOnFinishedListener) }
    }

    override fun startEvent() {
        eventId?.let { eventView?.startCourseParticipationActivity(it) }
    }

    override fun joinEvent() {
        eventId?.let { participantInteractor.createParticipant(it, App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),joinEventOnFinishedListener) }
    }

    override fun leaveEvent() {
        eventId?.let { participantInteractor.removeParticipant(it,App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),leaveEventOnFinishedListener) }
    }

    override fun deleteEvent() {
        eventId?.let { eventInteractor.deleteEvent(it,deleteEventOnFinishedListener) }
    }

    override fun showResults(){
        eventId?.let { eventView?.startEventResultsActivity(it) }
    }
}

class GetEventOnFinishedListener(eventPerformer : IEventContract.IEventPerformer, eventView : IEventContract.IEventView) : IOnFinishedListener<Event> {

    lateinit var event : Event
    private var eventView : IEventContract.IEventView
    private var eventPerformer : IEventContract.IEventPerformer

    init{
        this.eventView = eventView
        this.eventPerformer = eventPerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {

        lateinit var eventDate : String
        lateinit var eventTime : String
        var userIsParticipant : Boolean = false

        if(response.isSuccessful){
            if (response.body() != null) {
                event = response.body()!!.entity!!

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

                val dateformatted = sdf.parse(event.eventDate)

                val dateFormatter = SimpleDateFormat("dd-MM-YYYY")
                val timeFormatter = SimpleDateFormat("HH:mm")
                eventDate = dateFormatter.format(dateformatted)
                eventTime = timeFormatter.format(dateformatted)

                if (event.eventOrganiser.userId == App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0)) {
                    eventView.setupForOrganizer(event.eventStatus)
                } else {

                    if (!event.participants.isEmpty()) {
                        for (participant in event.participants) {
                            if (participant.participantUser.userId?.equals(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0))!! && participant.participantControlPerformances.isEmpty()) {
                                userIsParticipant=true
                                break
                            }
                        }
                    }
                    eventView.setupForUser(event.eventStatus,userIsParticipant)
                }

                eventPerformer.setEvent(event)
                response.body()!!.entity?.let { eventView?.fillInformation(event.eventName,event.eventNote,eventDate,eventTime) }
                for (photo in response.body()!!.entity?.eventPhotographs!!){
                    if (photo.active!!){
                        eventView.setupImage(photo.photoPath)
                        break
                    }
                }
            } else {
                Log.e("TAG","problem 1")
                eventView?.onResponseError()
            }
        } else {
            Log.e("TAG","problem 2")
            eventView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}

class JoinEventOnFinishedListener(eventPerformer : IEventContract.IEventPerformer, eventView : IEventContract.IEventView) : IOnFinishedListener<Event> {

    private var eventView : IEventContract.IEventView
    private var eventPerformer : IEventContract.IEventPerformer

    init{
        this.eventView = eventView
        this.eventPerformer = eventPerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventView.makeToast("Event Successfully Joined")
                response.body()!!.entity?.let { eventView.setupForUser(it.eventStatus,true) }
            } else {
                eventView?.onResponseError()
            }
        } else {
            eventView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}

class UpdateEventOnFinishedListener(eventPerformer : IEventContract.IEventPerformer, eventView : IEventContract.IEventView) : IOnFinishedListener<Event> {

    private var eventView : IEventContract.IEventView
    private var eventPerformer : IEventContract.IEventPerformer

    init{
        this.eventView = eventView
        this.eventPerformer = eventPerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventView.makeToast("Event Status Updated")
                response.body()!!.entity?.let { eventPerformer.setEvent(it) }
                response.body()!!.entity?.let { eventView.setupForOrganizer(it.eventStatus) }
            } else {
                eventView?.onResponseError()
            }
        } else {
            eventView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}

class LeaveEventOnFinishedListener(eventPerformer : IEventContract.IEventPerformer, eventView : IEventContract.IEventView) : IOnFinishedListener<Event> {

    private var eventView : IEventContract.IEventView
    private var eventPerformer : IEventContract.IEventPerformer

    init{
        this.eventView = eventView
        this.eventPerformer = eventPerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventView.makeToast("Event Successfully Left")
                eventView.startViewEventsActivity()
            } else {
                eventView?.onResponseError()
            }
        } else {
            eventView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}

class DeleteEventOnFinishedListener(eventPerformer : IEventContract.IEventPerformer, eventView : IEventContract.IEventView) : IOnFinishedListener<Boolean> {

    private var eventView : IEventContract.IEventView
    private var eventPerformer : IEventContract.IEventPerformer

    init{
        this.eventView = eventView
        this.eventPerformer = eventPerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventView.makeToast("Event Successfully Deleted")
                eventView.startViewEventsActivity()
            } else {
                eventView?.onResponseError()
            }
        } else {
            eventView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (eventView!=null){
            eventView?.onResponseFailure(t)
        }
    }
}