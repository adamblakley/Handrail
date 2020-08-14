package com.orienteering.handrail.event

import com.orienteering.handrail.models.Event

interface IEventContract {

    interface IEventPerformer{
        fun onDestroy()
        fun requestDataFromServer()
        fun updateEventStatus()
        fun startEvent()
        fun joinEvent()
        fun leaveEvent()
        fun deleteEvent()
        fun showResults()
        fun setEvent(event : Event)

    }

    interface IEventView{
        var eventId : Int
        fun fillInformation(name : String, note : String, date : String, time : String)
        fun createButtons()
        fun createImages()
        fun intialiseTextView()
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
        fun eventAction()
        fun startCourseParticipationActivity(eventId : Int)
        fun startViewEventsActivity()
        fun startEventResultsActivity(eventId: Int)
        fun setupForUser(ventStatus : Integer,userIsParticipant : Boolean)
        fun setupForOrganizer(eventStatus : Integer)
        fun setupImage(imageUrl : String)
        fun makeToast(message : String)
    }
}