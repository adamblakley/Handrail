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

/**
 * Class handles all user interface elements and event calls from viewing organiser events
 *
 */
class ManageEventsActivity : AppCompatActivity(), IManageEventsContract.IManageEventsView {

    // list for all events
    private lateinit var recyclerView : RecyclerView
    // button to create new event
    private lateinit var btnCreateEvent : Button
    // presenter handles retrieval of events
    private lateinit var presenter : IManageEventsContract.IManageEventsPresenter

    /**
     * initialise elements, initialise presenter and request event information
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_events)
        initRecyclerView()
        createButtons()
        presenter = ManageEventsPresenter(this, EventInteractor())
        presenter.requestDataFromServer()
    }

    /**
     * Create all buttons and associate onclick
     *
     */
    private fun createButtons(){
        btnCreateEvent = findViewById(R.id.button_create_manage_events)
        // initiate the create event use case
        btnCreateEvent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@ManageEventsActivity, CreateEventActivity::class.java).apply {}
                startActivity(intent)
            }
        })
    }

    /**
     * Initiate the recycler view to display events
     *
     */
    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_manage_events)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Utilise ManageEvents Adapter to display passed events information via recycler view
     *
     * @param eventsList
     */
    override fun fillInformation(eventsList: ArrayList<Event>) {
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