package com.orienteering.handrail.course

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Course

/**
 * Contract for View and Edit Course Use Cases
 */
interface ICourseContract {
    /**
     * The View, handles display of course information
     */
    interface ICourseView{
        /**
         * Fill information from controls to view
         * @param controls
         */
        fun fillRecyclerView(controls: List<Control>)

        /**
         * Respond with error in view from data failure
         * @param throwable
         */
        fun onResponseFailure(throwable : Throwable)

        /**
         * Respond with error in view from data error
         *
         */
        fun onResponseError()

        /**
         * Handle delete success response in view upon data success
         *
         */
        fun onDeleteResponseSuccess()

        /**
         *  Handle getting of course information for display
         * @param controls
         * @param courseName
         */
        fun onGetResponseSuccess(controls: List<Control>, courseName : String)

        /**
         * Show all information from successful data results
         *
         * @param courseName
         * @param courseNote
         * @param courseAltitudes
         * @param courseDistance
         */
        fun showInformation(courseName: String?=null, courseNote : String?=null, courseAltitudes : MutableList<Double>? = null, courseDistance :Double)

        /**
         * show individual control information upon request
         *
         * @param nameOfControl
         * @param noteOfControl
         * @param positionOfControl
         * @param imagePath
         */
        fun showControlInformation(nameOfControl:String? = "No Name Available", noteOfControl: String? = "No Note Available", positionOfControl: Int? = 0, imagePath: String? = null)

        /**
         * Show message with string value in view
         *
         * @param message
         */
        fun showMessage(message : String)

        /**
         * Show polyline route of user in map
         *
         * @param routePoints
         * @param bounds
         */
        fun showRoute(routePoints : List<LatLng>, bounds : LatLngBounds)

        /**
         * Add controls to map from data success
         *
         * @param controlsNameLatLng
         */
        fun addControls(controlsNameLatLng : Map<String,LatLng>)
    }

    /**
     * The presenter, handles logic of course information manipulation and retreival
     */
    interface ICoursePresenter{
        /**
         * Request Course data from source
         *
         */
        fun requestDataFromServer()

        /**
         * Remove course data from source
         *
         */
        fun removeDataFromServer()

        /**
         * Disseminate course information and post to view
         *
         */
        fun courseInformation()

        /**
         * Disseminate Control Information and post to view
         *
         * @param markerTitle
         */
        fun controlInformation(markerTitle : String)

        /**
         * Request a file be generated and saved to the device, utilizes GPXBuilder class
         *
         * @param context
         */
        fun generateFile(context : Context)

        /**
         * Sets the Course within the presenter from the onfinishedlistener
         *
         * @param course
         */
        fun setPresenterCourse(course : Course)

        /**
         * Disseminates route information and post to view
         *
         * @param controls
         */
        fun getRoute(controls : List<Control>)

        /**
         * Disseminate controls information and post to view
         *
         */
        fun getControls()
    }

}