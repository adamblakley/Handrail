package com.orienteering.handrail.welcome
/**
 * Determines relationships between view and presenter for viewing the welcome screen
 *
 */
interface IWelcomeContract {

    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface IWelcomeView{
        /**
         * On successful login check response
         *
         */
        fun onResponseSuccess()

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
         * Handle connection failure
         *
         * @param throwable
         */
        fun onResponseFailureLogin(throwable : Throwable)

        /**
         * Handle service error
         *
         */
        fun onResponseErrorLogin()

        /**
         * Handle incorrect password
         *
         */
        fun onResponseIncorrect()

        /**
         * Redirect user to hub
         *
         */
        fun startHomeMenuActivity()

        /**
         * Display message to user
         *
         * @param message
         */
        fun makeToast(message : String)
    }

    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface IWelcomePresenter{
        /**
         * Check with service is user is logged in
         *
         */
        fun checkLogin()

        /**
         * Destroy view on presenter destroy to avoid model errors mid-transaction
         *
         */
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

}