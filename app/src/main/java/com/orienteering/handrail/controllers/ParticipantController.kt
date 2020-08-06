package com.orienteering.handrail.controllers

import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.classes.Participant
import com.orienteering.handrail.classes.PerformanceResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.services.ParticipantService
import com.orienteering.handrail.services.ServiceFactory
import retrofit2.Callback

/**
 * Controls participant service calls
 *
 */
class ParticipantController {

    //classifiying tag for log
    val TAG: String = "EventController"
    //participant service
    var participantService: ParticipantService

    /**
     * Initialise participant service
     */
    init {
        participantService = ServiceFactory.makeService(ParticipantService::class.java)
    }

    /**
     * Participant service get participants
     *
     * @param eventId
     * @param callback
     */
    fun getParticipants(eventId : Int, callback: Callback<List<Participant>>){
        val call = participantService.readEventParticipants(eventId)
        call.enqueue(callback)
    }

    /**
     * participant service post participant
     *
     * @param eventId
     * @param userId
     * @param callback
     */
    fun createParticipant(eventId: Int, userId : Long, callback: Callback<StatusResponseEntity<Event>>){
        val call = participantService.create(eventId, userId)
        call.enqueue(callback)
    }

    /**
     * Remove participant from participant service controller
     *
     * @param eventId
     * @param userId
     * @param callback
     */
    fun removeParticipant(eventId: Int, userId: Long,callback: Callback<StatusResponseEntity<Event>>){
        val call = participantService.removeParticipant(eventId,userId)
        call.enqueue(callback)
    }

    /**
     * retrieve performance response from participant service controller
     *
     * @param eventId
     * @param userId
     * @param callback
     */
    fun getParticipantPerformance(eventId: Int, userId: Long,callback: Callback<StatusResponseEntity<PerformanceResponse>>){
        val call = participantService.getPerformance(eventId,userId)
        call.enqueue(callback)
    }

}