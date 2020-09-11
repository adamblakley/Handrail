package com.orienteering.handrail.events

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event

/**
 * Handles view of the events list
 *
 */
class EventsActivity : AppCompatActivity(), IEventsContract.IEventsView {

    private lateinit var recyclerView : RecyclerView
    private lateinit var eventsPresenter : IEventsContract.IEventsPresenter
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        initRecyclerView()
        progressDialog = ProgressDialog(this@EventsActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading Content...")
        progressDialog.show()
        eventsPresenter = EventsPresenter(this, EventInteractor())
        eventsPresenter.requestDataFromServer()
    }

    fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_events)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }



    /**
     * initialise reycler view adapter and fill information via recyclerview
     *
     * @param eventsList
     */
    override fun fillInformation(eventsList: ArrayList<Event>) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val eventsAdapter : EventsAdapter = EventsAdapter(eventsList)
        recyclerView.adapter = eventsAdapter
    }

    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast = Toast.makeText(this@EventsActivity,"No Events available",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseFailure(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast = Toast.makeText(this@EventsActivity,"Error: Connectivity Error, unable to retreive events",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onDestroy() {
        eventsPresenter.onDestroy()
        super.onDestroy()
    }
}