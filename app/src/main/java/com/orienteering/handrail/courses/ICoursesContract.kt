package com.orienteering.handrail.courses

import com.orienteering.handrail.models.Course

/**
 * Contract records all requisites for view courses use case
 *
 */
interface ICoursesContract {

    /**
     * Provides features for presenter to implement
     *
     */
    interface ICoursesPresenter{
        /**
         *  On destroy, remove view
         *
         */
        fun onDestroy()

        /**
         * Request data from server on user request
         *
         */
        fun requestDataFromServer()
    }

    /**
     * Provides view for courses use case
     *
     */
    interface ICoursesView{
        /**
         * Fill recycler view with information
         * @param coursesList
         */
        fun fillInformation(coursesList : ArrayList<Course>)

        /**
         * Provide response on data error
         *
         * @param throwable
         */
        fun onResponseFailure(throwable : Throwable)

        /**
         * Provide response on data error
         *
         */
        fun onResponseError()

        /**
         * Handle forbidden reqeust
         *
         */
        fun onResponseForbidden()
    }
}