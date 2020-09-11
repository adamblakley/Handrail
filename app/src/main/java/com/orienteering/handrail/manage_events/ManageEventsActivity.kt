package com.orienteering.handrail.manage_events

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.create_event.CreateEventActivity
import com.orienteering.handrail.home_menu.HomeActivity
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
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();

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
        progressDialog.setMessage("Loading Content...")
        progressDialog.show()
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
        progressDialog = ProgressDialog(this@ManageEventsActivity)
        progressDialog.setCancelable(false)
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
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val eventsAdapter : ManageEventsAdapter = ManageEventsAdapter(eventsList)
        recyclerView.adapter = eventsAdapter
    }

    override fun onResponseFailure(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast = Toast.makeText(this@ManageEventsActivity,"Error: Connectivity Error, unable to retreive events", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast = Toast.makeText(this@ManageEventsActivity,"No Events available", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseForbidden() {
        val intent = Intent(this@ManageEventsActivity, HomeActivity::class.java).apply {}
        Toast.makeText(this@ManageEventsActivity,"Contact an admin to grant access to create and manage events",Toast.LENGTH_LONG).show()
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

}