package com.orienteering.handrail.results

import com.orienteering.handrail.models.Participant

interface IResultsContract {

    interface IResultsPerformer{
        fun onDestroy()
        fun setPerformerParticipants(participants : List<Participant>)
        fun requestDataFromServer(eventId: Int)
        fun processInformation()
    }

    interface IResultsView{
        fun onResponseError()
        fun onResponseFailure()
        fun showRecyclerInformation(names:List<String>, times:List<String>, positions:List<Int>, ids: MutableList<Int?>, imageUrls:List<String>)
    }
}