package com.orienteering.handrail.toproutes

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.models.Participant

/**
 * Determines relationships between view and presenter for viewing top routes
 *
 */
interface ITopRoutesContract {

    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface ITopRoutesPresenter{
        fun onDestroy()
        /**
         * Set presenter participant objects
         *
         * @param participant
         */
        fun setPerformerParticipants(participants : List<Participant>)
        /**
         * Request performance data from source
         *
         * @param eventId
         */
        fun requestDataFromServer(eventId: Int)
        /**
         * Disseminate performance information from participant
         *
         */
        fun processInformation()
        /**
         * Get participants routes and send to view
         *
         */
        fun getPerformerParticipants()
        /**
         * Aquire event controls
         *
         */
        fun getControls()

        /**
         * diplay control information on supply of title
         *
         * @param markerTitle
         */
        fun controlInformation(markerTitle : String)
    }

    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface ITopRoutesView{
        /**
         * Fill performance inoformation in user interface
         *
         * @param imageUrls
         * @param controlPositions
         * @param controlNames
         * @param times
         * @param altitudes
         */
        fun showInformation(names:List<String>, times:List<String>, positions:List<Int>, ids: MutableList<Int?>, imageUrls:List<String>)
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
        fun showRoute(participants : List<Participant>, bounds : LatLngBounds)
        /**
         * Show controls on user interface
         *
         * @param controlsNameLatLng
         */
        fun addControls(controlsNameLatLng : Map<String, LatLng>)

        /**
         * get control information from passed title
         *
         * @param nameOfControl
         * @param noteOfControl
         * @param positionOfControl
         * @param imagePath
         */
        fun showControlInformation(nameOfControl: String?, noteOfControl: String?, positionOfControl: Int?, imagePath: String?)
    }

}