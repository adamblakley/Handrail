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
    }

}