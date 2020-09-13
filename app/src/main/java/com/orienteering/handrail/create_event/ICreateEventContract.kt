package com.orienteering.handrail.create_event

import android.net.Uri
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.models.Event

/**
 * MVP Contract for the create event use case between view and presenter
 *
 */
interface ICreateEventContract {

    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface ICreateEventView{
        /**
         * Notify user on success of event upload
         *
         * @param eventId
         */
        fun onPostResponseSuccess(eventId : Int)

        /**
         * Notify user on error of event upload
         *
         */
        fun onResponseError()

        /**
         * Notify user on connectivity failure
         *
         */
        fun onResponseFailure()

        /**
         * Notify user of image choice error
         *
         */
        fun onImageResponseError()

        /**
         * Provide information on response of retreival of courses on course select for create event
         *
         * @param courses
         */
        fun onGetResponseSuccess(courses : List<Course>)

        /**
         * capture image uri, display image for event
         *
         * @param imageUri
         */
        fun setupImage(imageUri : Uri)
    }

    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface ICreateEventPresenter{
        /**
         * Destroy view
         *
         */
        fun onDestroy()

        /**
         * Upload event to server
         *
         * @param event
         */
        fun postDataOnServer(event : Event)

        /**
         * Retrieve courses from server
         *
         */
        fun getDataFromServer()

        /**
         * Select image to accompany event
         *
         */
        fun selectImage()

        /**
         * Set image Uri from selected image to accompany event
         *
         * @param imageUri
         */
        fun setImage(imageUri : Uri)

        /**
         * Check image has been selected
         *
         * @return
         */
        fun checkImage() : Boolean
    }

}