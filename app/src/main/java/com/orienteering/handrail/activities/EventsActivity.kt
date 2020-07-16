package com.orienteering.handrail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.*
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.httprequests.EventService
import com.orienteering.handrail.httprequests.ServiceFactory
import com.orienteering.handrail.utilities.EventsRecyclerViewAdapter

import retrofit2.Call
import retrofit2.Response

class EventsActivity : AppCompatActivity() {

    var mNames = mutableListOf<String>()
    var mNotes = mutableListOf<String>()
    var mIds = mutableListOf<Int?>()
    var mImageUrls = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        initImageBitmaps()

    }

    private fun initImageBitmaps(){
        Log.e(TAG,"Prepping bitmaps")
        getEvents()
    }

    fun getEvents() {
        ServiceFactory.makeService(EventService::class.java).readAll()
            .enqueue(object : retrofit2.Callback<List<Event>?> {
                override fun onFailure(call: Call<List<Event>?>, t: Throwable) {
                    Log.e(TAG, "Failure getting events")
                }
                override fun onResponse(
                    call: Call<List<Event>?>,
                    response: Response<List<Event>?>
                ) {
                    Log.e(TAG, "Success getting events")
                    val eventgot: List<Event>? = response.body()
                    if (eventgot != null) {
                        for (event in eventgot) {
                            mNames.add(event.eventName)
                            mNotes.add(event.eventNote)
                            mIds.add(event.eventId)
                            Log.e(TAG,"event id = ${event.toString()}")
                        }
                    }
                    initRecyclerView()
                }
            })
    }

    private fun initRecyclerView(){
        Log.e(TAG,"initReyclerView")
        val recyclerView : RecyclerView = findViewById(R.id.rv_events)
        val mIdsToList = mIds.toList()
        val adapter = EventsRecyclerViewAdapter(mNames,mNotes,mImageUrls,mIdsToList,this)
        Log.e(TAG,"$mNames")
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }



}

private const val TAG = "EventsActivity"
