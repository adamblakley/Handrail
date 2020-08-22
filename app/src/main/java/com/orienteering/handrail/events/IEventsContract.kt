package com.orienteering.handrail.events

import com.orienteering.handrail.models.Event

/**
 * MVP Contract records required jobs of view and presenter for the Events activity
 *
 */
interface IEventsContract {

    /**
     * Handles logic for Events and requests model from Interactor
     * Passes information to IEventsView for display
     */
    interface IEventsPresenter{
        fun onDestroy()

        /**
         * request events information from server
         *
         */
        fun requestDataFromServer()
    }

    /**
     * Handles information display of events
     * Information is received from IPresenter
     */
    interface IEventsView{
        /**
         * fill view with events information
         *
         * @param eventsList
         */
        fun fillInformation(eventsList : ArrayList<Event>)

        /**
         * respond on get events connectivity failure
         *
         * @param throwable
         */
        fun onResponseFailure(throwable : Throwable)

        /**
         * repond on get events response error
         *
         */
        fun onResponseError()
    }
}