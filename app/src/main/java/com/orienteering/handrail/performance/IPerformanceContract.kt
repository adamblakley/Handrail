package com.orienteering.handrail.performance

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.models.Participant

/**
 * Contract defines relationships between view and presenter for view performance use case
 *
 */
interface IPerformanceContract {
    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface IPerformanceView{
        /**
         * Fill performance inoformation in user interface
         *
         * @param imageUrls
         * @param controlPositions
         * @param controlNames
         * @param times
         * @param altitudes
         */
        fun fillInformation(imageUrls : List<String>,controlPositions : List<Int>,controlNames : List<String>,times : List<String>,altitudes : List<Double>, position : Int)

        /**
         * handle connection failure
         *
         * @param throwable
         */
        fun onResponseFailure(throwable : Throwable)

        /**
         * Handle connection error
         *
         */
        fun onResponseError()

        /**
         * Show route in user interface from routepoints list
         *
         * @param routePoints
         * @param bounds
         * @param totalDistance
         * @param pace
         */
        fun showRoute(routePoints : List<LatLng>, bounds : LatLngBounds, totalDistance : Double, pace : Double)

        /**
         * Show controls on user interface
         *
         * @param controlsNameLatLng
         */
        fun addControls(controlsNameLatLng : Map<String, LatLng>)
    }

    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface IPerformancePresenter{
        /**
         * Request performance data from source
         *
         * @param eventId
         */
        fun requestDataFromServer(eventId : Int)

        /**
         * Set presenter participant object
         *
         * @param participant
         */
        fun setPresenterParticipant(participant : Participant)

        /**
         * Aquire participant route values
         *
         */
        fun getRoute()

        /**
         * Aquire event controls
         *
         */
        fun getControls()

        /**
         * Disseminate performance information from participant
         *
         */
        fun getPerformanceInformation()

        /**
         * Destroy view on destroy of presenter
         *
         */
        fun onDestroy()
    }
}