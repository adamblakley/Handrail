package com.orienteering.handrail.utilities

import android.util.Log
import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.httprequests.CourseService
import com.orienteering.handrail.httprequests.EventService
import com.orienteering.handrail.httprequests.ServiceFactory
import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EventCreator {


    companion object{

        val TAG : String = "EventCreator"

        fun uploadEvent(event : Event){
            ServiceFactory.makeService(EventService::class.java).create(event).enqueue(object :
                Callback<StatusResponseEntity<Event>?> {
                override fun onFailure(call: Call<StatusResponseEntity<Event>?>, t: Throwable) {
                    Log.e(EventCreator.TAG,"Failure adding Event")
                }

                override fun onResponse(
                    call: Call<StatusResponseEntity<Event>?>,
                    response: Response<StatusResponseEntity<Event>?>
                ) {
                    Log.e(EventCreator.TAG,"Success adding Event")
                }
            })


        }
    }



}