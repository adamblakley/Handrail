package com.orienteering.handrail.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.controllers.ParticipantController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.GeofencePerformanceCalculator
import com.orienteering.handrail.utilities.PerformanceRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ResultsListActivity"

/**
 * Class to manage list of participant results sorted, and views of each participant
 */
class ResultsListActivity : AppCompatActivity() {

    // event id passed as serialisable extra
    var eventIdPassed : Int = 0
    // participant names
    var mNames = mutableListOf<String>()
    //participant time
    var mTime = mutableListOf<String>()
    //participant positions
    var mPosition = mutableListOf<Int>()
    //participant ids
    var mIds = mutableListOf<Int?>()
    //participant image urls
    var mImageUrls = mutableListOf<String>()

    //button for top routes
    lateinit var viewTopRoutesButton : Button

    //geofence performance calculator to calculate performance times
    val geofencePerformanceCalculator : GeofencePerformanceCalculator = GeofencePerformanceCalculator()

    // controller for participant services
    val participantController : ParticipantController = ParticipantController()

    //participant callback to manage response of get participants service
    val getParticipantsCallback = object : Callback<StatusResponseEntity<List<Participant>>> {
        override fun onFailure(call: Call<StatusResponseEntity<List<Participant>>>, t: Throwable) {
            Log.e(TAG, "Failure getting participants")
        }
        override fun onResponse(call: Call<StatusResponseEntity<List<Participant>>>, response: Response<StatusResponseEntity<List<Participant>>>) {
            Log.e(TAG, "Success getting participants")
            val participants: List<Participant>? = response.body()?.entity
            if (participants != null) {
                for (participant in participants){
                    mNames.add(participant.participantUser.userFirstName)
                    mTime.add(geofencePerformanceCalculator.convertMilliToMinutes(participant.participantControlPerformances[participant.participantControlPerformances.size-1].controlTime))
                    mPosition.add(participants.indexOf(participant)+1)
                    mIds.add(participant.participantId)

                    if (participant.participantUser.userPhotographs?.size!! >=1){
                        for (photo in participant.participantUser.userPhotographs!!){
                            if (photo.active==true){
                                mImageUrls.add(photo.photoPath)
                            }
                        }

                    } else {
                        mImageUrls.add("dummy")
                    }
                }
            }
            createButtons()
            initRecyclerView()
        }
    }

    /**
     * onCreate manages content view and initialises bitmap and recyclerview
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_list)
        this.eventIdPassed = intent.getSerializableExtra("EVENT_ID") as Int
        initImageBitmaps()
    }

    /**
     * initialises buttons after on create
     */
    private fun createButtons(){
        viewTopRoutesButton = findViewById(R.id.button_view_top_routes)
        viewTopRoutesButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intentRoutes = Intent(this@ResultsListActivity,ViewTopRoutesActivity::class.java)
                intentRoutes.putExtra("EVENT_ID",eventIdPassed)
                startActivity(intentRoutes)
            }
        })
    }

    /**
     * initialises bitmaps and calls getParticipants()
     */
    private fun initImageBitmaps(){
        Log.e(TAG,"Prepping bitmaps")
        getParticipants()
    }

    /**
     * initialises recycler view of participants
     */
    private fun initRecyclerView(){
        Log.e(TAG,"initReyclerView")
        val recyclerView : RecyclerView = findViewById(R.id.rv_results)
        val mIdsToList = mIds.toList()
        val adapter = PerformanceRecyclerViewAdapter(mNames,mTime,mImageUrls,mIdsToList,mPosition,this)
        Log.e(TAG,"$mNames")
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * calls getParticipants() from participantController, response List<Participant>
     */
    fun getParticipants(){
        participantController.getParticipants(eventIdPassed,getParticipantsCallback)
    }
}