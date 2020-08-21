package com.orienteering.handrail.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event

class EventsActivity : AppCompatActivity(), IEventsContract.IEventsView {

    private lateinit var recyclerView : RecyclerView
    private lateinit var eventsPerformer : IEventsContract.IEventsPerformer

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        initRecyclerView()

        eventsPerformer = EventsEventsPerformer(this, EventInteractor())
        eventsPerformer.requestDataFromServer()
    }

    fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_events)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun fillRecyclerView(eventsList: ArrayList<Event>) {
        val eventsAdapter : EventsAdapter = EventsAdapter(eventsList)
        recyclerView.adapter = eventsAdapter
    }

    override fun onResponseError() {
        val toast = Toast.makeText(this@EventsActivity,"No Events available",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseFailure(throwable: Throwable) {
        val toast = Toast.makeText(this@EventsActivity,"Error: Connectivity Error, unable to retreive events",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onDestroy() {
        eventsPerformer.onDestroy()
        super.onDestroy()
    }
}