package com.orienteering.handrail.password_update

import com.orienteering.handrail.models.PasswordUpdateRequest

/**
 * Contract defines relationships between view and presenter for update password use case
 *
 */
interface IPasswordUpdateContract {
    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface IPasswordUpdateView{
        /**
         * Handle connection failure response
         *
         * @param throwable
         */
        fun onResponseFailure(throwable : Throwable)

        /**
         * Handle password error response
         *
         */
        fun onResponsePasswordError()

        /**
         * Handle service error response
         *
         */
        fun onResponseError()

        /**
         * Handle successful password update response
         *
         */
        fun onResponseSuccess()

    }
    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface IPasswordUpdatePresenter{
        /**
         * provide onDestroy mechanisms to destroy view on request
         *
         */
        fun onDestroy()

        /**
         * request password update from source
         *
         * @param passwordUpdateRequest
         */
        fun putDataToServer(passwordUpdateRequest: PasswordUpdateRequest)
    }

}