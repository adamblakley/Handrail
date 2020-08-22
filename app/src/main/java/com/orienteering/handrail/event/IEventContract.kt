package com.orienteering.handrail.event

import com.orienteering.handrail.models.Event

/**
 * MVP contract for relations between view and presenter for view event use case
 *
 */
interface IEventContract {

    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface IEventPresenter{
        fun onDestroy()

        /**
         * retrieve event data from backend
         *
         */
        fun requestDataFromServer()

        /**
         * update event status, before - current - after
         *
         */
        fun updateEventStatus()

        /**
         * initiate participation in event
         *
         */
        fun startEvent()

        /**
         * request user join event
         *
         */
        fun joinEvent()

        /**
         * request user leave event
         *
         */
        fun leaveEvent()

        /**
         * request organiser delete event
         *
         */
        fun deleteEvent()

        /**
         * show results of participants of event
         *
         */
        fun showResults()

        /**
         * set presenter event from passed event (retrieved)
         *
         * @param event
         */
        fun setEvent(event : Event)

    }
    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface IEventView{
        // event id retrieved from previous activity as intent extra
        var eventId : Int

        /**
         * display all event information to user
         *
         * @param name
         * @param note
         * @param date
         * @param time
         */
        fun fillInformation(name : String, note : String, date : String, time : String)

        /**
         * inform user of connectivity failure on response of event retrieval
         *
         * @param throwable
         */
        fun onResponseFailure(throwable : Throwable)

        /**
         * inform user of response error of event retrieval
         *
         */
        fun onResponseError()

        /**
         * request view start course participation
         *
         * @param eventId
         */
        fun startCourseParticipationActivity(eventId : Int)

        /**
         * view events on request of view
         *
         */
        fun startViewEventsActivity()

        /**
         * request results view on request of view
         *
         * @param eventId
         */
        fun startEventResultsActivity(eventId: Int)

        /**
         * setup display for user if not organiser
         *
         * @param ventStatus
         * @param userIsParticipant
         */
        fun setupForUser(ventStatus : Integer,userIsParticipant : Boolean)

        /**
         * setup display for organiser
         *
         * @param eventStatus
         */
        fun setupForOrganizer(eventStatus : Integer)

        /**
         * bind image uri from retrieval and set for view
         *
         * @param imageUrl
         */
        fun setupImage(imageUrl : String)

        /**
         * send message to view for display
         *
         * @param message
         */
        fun makeToast(message : String)
    }
}