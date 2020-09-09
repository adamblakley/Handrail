package com.orienteering.handrail.create_course

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.google.android.gms.maps.model.LatLng

/**
 * Contract for the create course use case
 *
 */
interface ICreateCourseContract {

    /**
     * Presenter interface, provides logic and data aquisition for create course use case
     *
     */
    interface ICreateCoursePresenter{
        /**
         * Create and send a course object to backend
         *
         * @param name
         * @param note
         */
        fun createCourse(name: String, note :String)

        /**
         * Create a control for the course
         *
         * @param name
         * @param note
         */
        fun createControl(name : String, note : String)

        /**
         * Get control information for individual control on request
         *
         * @param nameOfControl
         */
        fun getControlInformation(nameOfControl : String)

        /**
         * Save the current latitude and longitude for use in creating a control
         *
         */
        fun saveLatLng()

        /**
         * Setup up upon permission check
         *
         */
        fun setUpMap()

        /**
         * Initiate locaiton request on request from view
         *
         */
        fun createLocationRequest()

        /**
         * update location update status if location updates are enabled
         *
         */
        fun updateLocationUpdateState()

        /**
         * Set image uri of chosen image from device to associate to control
         *
         * @param imageUri
         */
        fun setImage(imageUri : Uri)

        /**
         * Return true if course length > 0
         *
         * @return
         */
        fun getCourseLength() : Boolean
    }

    /**
     * View, provides view and binding of user input for create course use case
     *
     */
    interface ICreateCourseView{
        /**
         * Return view context
         *
         * @return
         */
        fun returnContext() : Context

        /**
         * return view activity
         *
         * @return
         */
        fun returnActivity() : Activity

        /**
         * Update view with information pertaining to user route
         *
         * @param currentLatlng
         * @param routePoints
         */
        fun updateDisplay(currentLatlng : LatLng,routePoints : List<LatLng>)

        /**
         * Add control markers to the map
         *
         * @param name
         * @param note
         * @param currentLatLng
         */
        fun addMapControl(name: String, note: String, currentLatLng: LatLng)

        /**
         * Provide user reponse on data error
         *
         */
        fun onResponseError()

        /**
         * Provide user response on connectivity error
         *
         */
        fun onResponseFailure()

        /**
         * Provide user response on location failure
         *
         */
        fun onLocationUpdateFailure()

        /**
         * Provide user response on successul course upload
         *
         * @param courseId
         */
        fun onPostResponseSuccess(courseId : Int)

        /**
         * Provide control information in association to request of control
         *
         * @param nameOfMarker
         * @param noteOfMarker
         * @param positionOfMarker
         * @param imageUriOfMarker
         */
        fun onControlinformationSucess(nameOfMarker:String? = "Control", noteOfMarker: String? = "Example Text", positionOfMarker: Int? = 0, imageUriOfMarker: Uri? = null)

        /**
         * Allow dialog to display on location save request for control
         *
         */
        fun onSaveLatLngSuccess()

        /**
         * setup map elements and options
         *
         */
        fun setUpMap()

        /**
         * Make a toast message
         *
         * @param message
         */
        fun makeToast(message : String)
    }
}