package com.orienteering.handrail.edit_event

import android.net.Uri
import com.orienteering.handrail.models.Event

/**
 * MVP Contract to declare the relationship between view and presenter within the edit event use case
 *
 */
interface IEditEventContract {

    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface IEditEventView{
        /**
         * Provide user feedback on connectivity failure of get event
         *
         * @param throwable
         */
        fun onGetResponseFailure(throwable : Throwable)

        /**
         * Provide user feedback on response error of get event
         *
         */
        fun onGetResponseError()

        /**
         * Provide user feedback on connectivity failure of edit event
         *
         * @param throwable
         */
        fun onUpdateResponseFailure(throwable : Throwable)
        /**
         * Provide user feedback on response error of edit event
         *
         */
        fun onUpdateResponseError()
        /**
         * Provide user feedback on response error of edit event relating to file upload error
         * @param eventId
         */
        fun onUpdatePartialResponseError(eventId: Int)
        /**
         * Provide user feedback on response success of update event
         *
         */
        fun onUpdateResponseSuccess(eventId : Int)

        /**
         * Fill view with event information upon retrieval
         *
         * @param eventName
         * @param eventNote
         * @param eventTime
         * @param eventDate
         * @param courseName
         */
        fun fillInformation(eventName : String, eventNote : String, eventTime : String, eventDate : String, courseName : String)

        /**
         * assign imageuri variable upon image select for update
         *
         * @param imageUrl
         */
        fun setupImage(imageUrl : Uri)

        /**
         * setup image for display from retrieval
         *
         * @param imagepath
         */
        fun setupImage(imagepath : String)
    }
    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface IEditEventPresenter{
        /**
         * onDestroy, implement @override for use case
         *
         */
        fun onDestroy()

        /**
         * post event data to backend
         *
         * @param eventName
         * @param eventDescription
         * @param eventDate
         */
        fun putDataOnServer(eventName: String, eventDescription: String, eventDate: String? = null)

        /**
         * retrieve event data from backend by event id
         *
         */
        fun getDataFromServer()

        /**
         * initiate select image dialog
         *
         */
        fun selectImage()

        /**
         * set presenter event from retrieved
         *
         * @param event
         */
        fun setEvent(event : Event)

        /**
         * set image uri from select image
         *
         * @param imageUri
         */
        fun setImage(imageUri : Uri)
    }

}