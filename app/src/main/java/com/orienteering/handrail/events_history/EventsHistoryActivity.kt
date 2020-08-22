package com.orienteering.handrail.events_history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event

// TAG for Logs
private val TAG: String = EventsHistoryActivity::class.java.getName()

/**
 * Handles view of event history and displays event history information and event history onclick
 *
 */
class EventsHistoryActivity : AppCompatActivity(), IEventsHistoryContract.IEventsHistoryView{

    private lateinit var recyclerView : RecyclerView
    private lateinit var presenter : IEventsHistoryContract.IEventsHistoryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        initRecyclerView()

        presenter = EventsHistoryPresenter(this, EventInteractor())
        presenter.requestDataFromServer()
    }

    /**
     * initialise recyclerview
     *
     */
    fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_events)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * display event information via recyclerview
     *
     * @param eventsList
     */
    override fun fillInformation(eventsList: ArrayList<Event>) {
        val eventsHistoryAdapter : EventsHistoryAdapter = EventsHistoryAdapter(eventsList)
        recyclerView.adapter = eventsHistoryAdapter
    }

    override fun onResponseError() {
        val toast = Toast.makeText(this@EventsHistoryActivity,"No Events available", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseFailure(throwable: Throwable) {
        val toast = Toast.makeText(this@EventsHistoryActivity,"Error: Connectivity Error, unable to retreive events", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
