package com.orienteering.handrail.results

import com.orienteering.handrail.models.Participant

interface IResultsContract {

    interface IResultsPresenter{
        fun onDestroy()
        fun requestDataFromServer(eventId: Int)
        fun processInformation(participants : List<Participant>)
    }

    interface IResultsView{
        fun onResponseError()
        fun onResponseFailure()
        fun showInformation(names:List<String>, times:List<String>, positions:List<Int>, ids: MutableList<Int?>, imageUrls:List<String>)
    }
}