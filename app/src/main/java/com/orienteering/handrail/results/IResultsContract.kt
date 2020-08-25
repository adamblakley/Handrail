package com.orienteering.handrail.results

import com.orienteering.handrail.models.Participant

/**
 * MVP Contract handles relationships between view and presenter classes for view results use case
 *
 */
interface IResultsContract {
    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface IResultsPresenter{
        fun onDestroy()

        /**
         * Request results data from source by providing event id
         *
         * @param eventId
         */
        fun requestDataFromServer(eventId: Int)

        /**
         * Disseminate results information
         *
         * @param participants
         */
        fun processInformation(participants : List<Participant>)
    }
    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface IResultsView{
        /**
         * Handle service error
         *
         */
        fun onResponseError()

        /**
         * Handle connection failure
         *
         */
        fun onResponseFailure()

        /**
         * Show results information via user interface
         *
         * @param names
         * @param times
         * @param positions
         * @param ids
         * @param imageUrls
         */
        fun showInformation(names:List<String>, times:List<String>, positions:List<Int>, ids: MutableList<Int?>, imageUrls:List<String>)
    }
}