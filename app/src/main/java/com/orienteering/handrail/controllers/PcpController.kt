package com.orienteering.handrail.controllers

import com.orienteering.handrail.classes.ParticipantControlPerformance
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
    fun create(participantId : Int?, pcps : List<ParticipantControlPerformance>, callback : Callback<StatusResponseEntity<List<ParticipantControlPerformance>>?>){
        val call = pcpService.createMany(participantId,pcps)
        call.enqueue(callback)
    }
}