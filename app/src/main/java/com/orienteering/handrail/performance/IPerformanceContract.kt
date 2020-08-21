package com.orienteering.handrail.performance

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.models.RoutePoint

interface IPerformanceContract {

    interface IPerformanceView{
        fun fillRecyclerView(imageUrls : List<String>,controlPositions : List<Int>,controlNames : List<String>,times : List<String>,altitudes : List<Double>)
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
        fun showRoute(routePoints : List<LatLng>, bounds : LatLngBounds, totalDistance : Double, pace : Double)
        fun addControls(controlsNameLatLng : Map<String, LatLng>)
    }

    interface IPerformancePresenter{
        fun requestDataFromServer(eventId : Int)
        fun setPresenterParticipant(participant : Participant)
        fun getRoute()
        fun getControls()
        fun getPerformanceInformation()
        fun onDestroy()
    }
}