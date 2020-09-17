package com.orienteering.handrail.splash

/**
 * Contract for the splash screen defining presenter and view
 *
 */
interface ISplashContract {

    /**
     * Handles splash screen logic
     *
     */
    interface ISplashPresenter{
        /**
         * Check user login
         *
         */
        fun getData()
    }

    /**
     * Handles splash screen UI
     *
     */
    interface ISplashView{
        /**
         * On get login success
         *
         */
        fun onResponseSuccess()

        /**
         * On get login failure
         *
         */
        fun onResponseFailure()

        /**
         * On HTTP error
         *
         */
        fun onresponseError()
    }

}