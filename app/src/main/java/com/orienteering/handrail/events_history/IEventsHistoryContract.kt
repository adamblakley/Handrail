package com.orienteering.handrail.events_history

import com.orienteering.handrail.models.Event

/**
 * MVP Contract records required jobs of view and presenter for the Events history activity
 *
 */
interface IEventsHistoryContract {
    /**
     * Handles logic for Events and requests model from Interactor
     * Passes information to IEventsView for display
     */
    interface IEventsHistoryPresenter{
        fun onDestroy()

        /**
         * Request data from server of events history
         *
         */
        fun requestDataFromServer()
    }
    /**
     * Handles information display of events
     * Information is received from IPresenter
     */
    interface IEventsHistoryView{
        /**
         * fills events information on display after retrieval
         *
         * @param eventsList
         */
        fun fillInformation(eventsList : ArrayList<Event>)
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
    }
}