package com.orienteering.handrail.controllers

import com.orienteering.handrail.classes.RoutePoint
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.services.RoutePointService
import com.orienteering.handrail.services.ServiceFactory
import retrofit2.Callback

class RoutePointController {

    val TAG: String = "PcpController"
    var routePointService : RoutePointService

    init {
        routePointService = ServiceFactory.makeService(RoutePointService::class.java)
    }

    /**
     * function to call service method to upload route points
     */
    fun create(participantId : Int?, routePoints : List<RoutePoint>, callback : Callback<StatusResponseEntity<List<RoutePoint>>?>){
        val call = routePointService.createMany(participantId,routePoints)
        call.enqueue(callback)
    }
}