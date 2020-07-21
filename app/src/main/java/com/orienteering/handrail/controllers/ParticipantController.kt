package com.orienteering.handrail.controllers

import com.orienteering.handrail.classes.Participant
import com.orienteering.handrail.services.ParticipantService
import com.orienteering.handrail.services.ServiceFactory
import retrofit2.Callback


class ParticipantController {

    val TAG: String = "EventController"
    var participantService: ParticipantService

    init {
        participantService = ServiceFactory.makeService(ParticipantService::class.java)
    }

    fun getParticipants(eventId : Int, callback: Callback<List<Participant>>){
        val call = participantService.readEventParticipants(eventId)
        call.enqueue(callback)
    }

}