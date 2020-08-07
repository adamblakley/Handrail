package com.orienteering.handrail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.controllers.EventController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.EventHistoryRecyclerViewAdapter
import com.orienteering.handrail.utilities.EventsRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "EventHistory"

class EventHistory : AppCompatActivity() {
    // event names for display in recycler view
    var mNames = mutableListOf<String>()
    // event notes for display in recycler view
    var mNotes = mutableListOf<String>()
    // event ids for use in intent launch for ViewEventActivity
    var mIds = mutableListOf<Int?>()
    // event image for display in recycler view
    var mImageUrls = mutableListOf<String>()

    // event controller to manage event services
    val eventController : EventController = EventController()

    // callback to manage response of getEvents
    private val getEventsCallback = object: Callback<StatusResponseEntity<List<Event>>> {
        override fun onFailure(call: Call<StatusResponseEntity<List<Event>>>, t: Throwable) {
            Log.e(TAG, "Failure getting events")
            val toast = Toast.makeText(this@EventHistory,"Failure getting events, please contact admin",
                Toast.LENGTH_SHORT)
            toast.show()
        }
        override fun onResponse(call: Call<StatusResponseEntity<List<Event>>>, response: Response<StatusResponseEntity<List<Event>>>) {
            if (response.isSuccessful){
                Log.e(TAG, "Success getting events")
                val eventgot: List<Event>? = response.body()?.entity
                if (eventgot != null) {
                    for (event in eventgot) {
                        mNames.add(event.eventName)
                        mNotes.add(event.eventNote)
                        mIds.add(event.eventId)
                        mImageUrls.add(event.eventPhotograph.photoPath)
                    }
                    initRecyclerView()
                } else {
                    val toast = Toast.makeText(this@EventHistory,"No Events To Show", Toast.LENGTH_SHORT)
                    toast.show()
                }
            } else {
                Log.e(TAG, "No events available events")
                val toast = Toast.makeText(this@EventHistory,"No Events available, please try again later",
                    Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    /**
     * on create set content view and initialise bitmaps for images
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        initImageBitmaps()
    }

    /**
     * initalises bitmaps for images
     */
    private fun initImageBitmaps(){
        Log.e(TAG,"Prepping bitmaps")
        getEvents()
    }

    /* For Reference: Changed to increase loose coupling
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

     */

    /**
     * function to get events to update list
     */
    fun getEvents(){
        eventController.retreiveByUserHistory(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getEventsCallback)
    }

    /**
     * initialising recycler view list of events
     */
    private fun initRecyclerView(){
        val recyclerView : RecyclerView = findViewById(R.id.rv_events)
        val adapter = EventHistoryRecyclerViewAdapter(mNames,mImageUrls,mIds,this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
