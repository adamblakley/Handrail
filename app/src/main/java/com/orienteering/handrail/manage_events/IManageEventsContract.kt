package com.orienteering.handrail.manage_events

import com.orienteering.handrail.models.Event

interface IManageEventsContract {

    interface IManageEventsView{
        fun fillRecyclerView(eventsList: ArrayList<Event>)
        fun onResponseFailure(throwable: Throwable)
        fun onResponseError()
    }

    interface IManageEventsPresenter{
        fun requestDataFromServer()
        fun onDestroy()
    }

}