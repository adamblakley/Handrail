package com.orienteering.handrail.manage_events

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.create_event.CreateEventActivity
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.utilities.ResultsAdapter

class ManageEventsActivity : AppCompatActivity(), IManageEventsContract.IManageEventsView {

    private lateinit var recyclerView : RecyclerView
    private lateinit var btnCreateEvent : Button
    private lateinit var presenter : IManageEventsContract.IManageEventsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_events)
        initRecyclerView()
        createButtons()
        presenter = ManageEventsPresenter(this, EventInteractor())
        presenter.requestDataFromServer()
    }

    private fun createButtons(){
        btnCreateEvent = findViewById(R.id.button_create_manage_events)
        btnCreateEvent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@ManageEventsActivity, CreateEventActivity::class.java).apply {}
                startActivity(intent)
            }
        })
    }

    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_manage_events)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun fillRecyclerView(eventsList: ArrayList<Event>) {
        val eventsAdapter : ManageEventsAdapter = ManageEventsAdapter(eventsList)
        recyclerView.adapter = eventsAdapter
    }

    override fun onResponseFailure(throwable: Throwable) {
        val toast = Toast.makeText(this@ManageEventsActivity,"Error: Connectivity Error, unable to retreive events", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseError() {
        val toast = Toast.makeText(this@ManageEventsActivity,"No Events available", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

}