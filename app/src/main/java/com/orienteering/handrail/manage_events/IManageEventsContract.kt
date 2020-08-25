package com.orienteering.handrail.manage_events

import com.orienteering.handrail.models.Event
/**
 * Contract defines relationships between view and presenter for event management and selection
 *
 */
interface IManageEventsContract {

    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface IManageEventsView{
        /**
         * Display retrieves information to user
         *
         * @param eventsList
         */
        fun fillInformation(eventsList: ArrayList<Event>)

        /**
         * Handle connection failure
         *
         * @param throwable
         */
        fun onResponseFailure(throwable: Throwable)

        /**
         * Handle service error
         *
         */
        fun onResponseError()
    }

    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface IManageEventsPresenter{
        /**
         * Retrieve events data from source
         *
         */
        fun requestDataFromServer()

        /**
         * Manage on-destroy finish
         *
         */
        fun onDestroy()
    }

}