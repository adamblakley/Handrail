package com.orienteering.handrail.events_history

import com.orienteering.handrail.models.Event

interface IEventsHistoryContract {

    interface IEventsHistoryPresenter{
        fun onDestroy()
        fun requestDataFromServer()
    }

    interface IEventsHistoryView{
        fun fillRecyclerView(eventsList : ArrayList<Event>)
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
    }
}