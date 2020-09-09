package com.orienteering.handrail.events_history

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        initRecyclerView()
        progressDialog = ProgressDialog(this@EventsHistoryActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading Content...")
        progressDialog.show()
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
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val eventsHistoryAdapter : EventsHistoryAdapter = EventsHistoryAdapter(eventsList)
        recyclerView.adapter = eventsHistoryAdapter
    }

    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast = Toast.makeText(this@EventsHistoryActivity,"No Events available", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseFailure(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast = Toast.makeText(this@EventsHistoryActivity,"Error: Connectivity Error, unable to retreive events", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
