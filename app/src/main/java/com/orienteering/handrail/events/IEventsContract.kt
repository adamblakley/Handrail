package com.orienteering.handrail.events

import com.orienteering.handrail.models.Event

/**
 * Contract records required jobs of view and presenter for the Events activity
 *
 */
interface IEventsContract {

    /**
     * Handles logic for Events and requests model from Interactor
     * Passes information to IEventsView for display
     */
    interface IEventsPerformer{
        fun onDestroy()
        fun requestDataFromServer()
    }

    /**
     * Handles information display of events
     * Information is received from IPresenter
     */
    interface IEventsView{
        fun fillRecyclerView(eventsList : ArrayList<Event>)
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
    }
}