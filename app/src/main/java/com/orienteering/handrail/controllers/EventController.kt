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

class EventController {

    val TAG: String = "EventController"
    var eventService: EventService

    init {
        eventService = ServiceFactory.makeService(EventService::class.java)
    }

    fun create(event : Event, file : MultipartBody.Part) {
        eventService.create(event,file).enqueue(object :
            Callback<StatusResponseEntity<Event>?> {
            override fun onFailure(call: Call<StatusResponseEntity<Event>?>, t: Throwable) {
                Log.e(TAG, "Failure adding Event")
            }

            override fun onResponse(
                call: Call<StatusResponseEntity<Event>?>,
                response: Response<StatusResponseEntity<Event>?>
            ) {
                Log.e(TAG, "Success adding Event")
            }
        })
    }

    fun retreive(callback: Callback<List<Event>>){
        val call = eventService.readAll()
        call.enqueue(callback)
    }

    fun retreiveByID(id : Int, callback: Callback<Event>){
        val call = eventService.read(id)
        call.enqueue(callback)
    }


}