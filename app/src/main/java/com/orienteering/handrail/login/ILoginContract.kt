package com.orienteering.handrail.login

/**
 * Contract defines relationships between view and presenter for login use case
 *
 */
interface ILoginContract {

    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface ILoginPresenter{
        fun onDestroy()

        /**
         * Request user information from sever
         *
         * @param email
         * @param password
         */
        fun requestDataFromServer(email : String, password : String)

        /**
         * Insert authorization into shared preferences for storage
         *
         * @param authToken
         * @param tokenType
         * @param userId
         */
        fun insertSharedPreferences(authToken : String, tokenType : String,userId : Long)
    }

    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface ILoginView{
        /**
         * Handle connection failure
         *
         * @param throwable
         */
        fun onResponseFailure(throwable : Throwable)

        /**
         * Handle service error
         *
         */
        fun onResponseError()

        /**
         * Redirect user to hub
         *
         */
        fun startHomeMenuActivity()

        /**
         * Create edit texts
         *
         */
        fun createEditText()

        /**
         * Create buttons for interaction
         *
         */
        fun createButtons()

        /**
         * Display message to user
         *
         * @param message
         */
        fun makeToast(message : String)
    }
}