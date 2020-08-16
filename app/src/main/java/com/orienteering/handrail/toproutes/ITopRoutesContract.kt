package com.orienteering.handrail.toproutes

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.models.Participant

interface ITopRoutesContract {

    interface ITopRoutesPerformer{
        fun onDestroy()
        fun setPerformerParticipants(participants : List<Participant>)
        fun requestDataFromServer(eventId: Int)
        fun processInformation()
        fun getPerformerParticipants()
        fun getControls()
    }

    interface ITopRoutesView{
        fun showRecyclerInformation(names:List<String>, times:List<String>, positions:List<Int>, ids: MutableList<Int?>, imageUrls:List<String>)
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
        fun showRoute(participants : List<Participant>, bounds : LatLngBounds)
        fun addControls(controlsNameLatLng : Map<String, LatLng>)

    }

}