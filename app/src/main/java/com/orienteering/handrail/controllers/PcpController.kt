package com.orienteering.handrail.controllers

import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.models.PerformanceUploadRequest
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.services.PcpService
import com.orienteering.handrail.services.ServiceFactory
import retrofit2.Callback

class PcpController {

    val TAG: String = "PcpController"
    var pcpService : PcpService

    init {
        pcpService = ServiceFactory.makeService(PcpService::class.java)
    }

    /**
     * Function to call service method to upload upload pcps
     */
    fun create(eventId : Int, userId: Long, request : PerformanceUploadRequest, callback : Callback<StatusResponseEntity<Participant>?>){
        val call = pcpService.create(eventId,userId,request)
        call.enqueue(callback)
    }
}