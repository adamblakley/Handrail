package com.orienteering.handrail.results

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.interactors.ParticipantInteractor
import com.orienteering.handrail.toproutes.TopRoutesActivity

/**
 * Class manages the view of event results
 *
 */
class ResultsActivity : AppCompatActivity(), IResultsContract.IResultsView {

    // Recycler view to display list of participants and their positions within the event results
    private lateinit var recyclerView : RecyclerView

    // Handles all logic and retrieves the model for display
    private lateinit var presenter : IResultsContract.IResultsPresenter

    //button for top routes
    lateinit var viewTopRoutesButton : Button
    var eventId : Int = 0

    /**
     * Handles the functions associated to start up, including UI elements and data retrieval
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set view to corresponding xml
        setContentView(R.layout.activity_results_list)
        // initate recycler view adapter
        initRecyclerView()
        // initiate buttons and declare onclick listeners
        createButtons()
        // initiate presenter property
        presenter = ResultsPresenter(this, ParticipantInteractor())

        if(intent.extras!=null) {
            eventId = intent.getSerializableExtra("EVENT_ID") as Int
            presenter.requestDataFromServer(eventId)
        }
    }

    /**
     * initialises buttons after on create
     */
    private fun createButtons(){
        viewTopRoutesButton = findViewById(R.id.button_view_top_routes)
        viewTopRoutesButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intentRoutes = Intent(this@ResultsActivity, TopRoutesActivity::class.java)
                intentRoutes.putExtra("EVENT_ID",eventId)
                if (eventId!=0){
                    startActivity(intentRoutes)
                }
            }
        })
    }

    override fun showInformation(names: List<String>, times: List<String>, positions: List<Int>, ids: MutableList<Int?>, imageUrls: List<String>) {
        val resultsAdapter : ResultsAdapter = ResultsAdapter(names,times,imageUrls,ids,positions)
        recyclerView.adapter = resultsAdapter
    }

    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_results)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResponseFailure() {
        Toast.makeText(this@ResultsActivity,"Error: Connectivity Error, unable to retreive results", Toast.LENGTH_SHORT).show()
    }

    override fun onResponseError() {
        Toast.makeText(this@ResultsActivity,"No results available", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}