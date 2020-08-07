package com.orienteering.handrail.controllers

import android.util.Log
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.services.EventService
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.httprequests.StatusResponseEntity
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Controller controls event request services
 *
 */
class EventController {

    // Tag for log
    val TAG: String = "EventController"
    // Event Service
    var eventService: EventService

    //Init block initialise service
    init {
        eventService = ServiceFactory.makeService(EventService::class.java)
    }

    /**
     * create an event
     *
     * @param userId
     * @param event
     * @param file
     * @param callback
     */
    fun create(userId : Long, event : Event, file : MultipartBody.Part, callback: Callback<StatusResponseEntity<Event>>) {
        val call = eventService.create(userId,event,file)
        call.enqueue(callback)
    }

    /**
     * retrieve all events
     *
     * @param callback
     */
    fun retreive(callback: Callback<StatusResponseEntity<List<Event>>>){
        val call = eventService.readAll()
        call.enqueue(callback)
    }

    /**
     * retrieve event by id
     *
     * @param id
     * @param callback
     */
    fun retreiveByID(id : Int, callback: Callback<Event>){
        val call = eventService.read(id)
        call.enqueue(callback)
    }

    fun retreiveByUserHistory(id : Long, callback: Callback<StatusResponseEntity<List<Event>>>){
        val call = eventService.readAllByUserHistory(id)
        call.enqueue(callback)
    }

    /**
     * update event status
     *
     * @param id
     * @param callback
     */
    fun updateStatus(id : Int, callback: Callback<StatusResponseEntity<Event>>){
        val call = eventService.updateStatus(id)
        call.enqueue(callback)
    }

    /**
     * TODOdelete event
     *
     * @param id
     * @param callback
     */
    fun deleteEvent(id : Int, callback: Callback<StatusResponseEntity<Boolean>>){
        val call = eventService.deleteEvent(id)
        call.enqueue(callback)
    }


}