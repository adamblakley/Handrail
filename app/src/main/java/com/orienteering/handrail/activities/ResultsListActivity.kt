package com.orienteering.handrail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Participant
import com.orienteering.handrail.httprequests.ParticipantService
import com.orienteering.handrail.httprequests.ServiceFactory
import com.orienteering.handrail.utilities.ResultsRecylcerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ResultsListActivity"

class ResultsListActivity : AppCompatActivity() {

    var mNames = mutableListOf<String>()
    var mTime = mutableListOf<String>()
    var mPosition = mutableListOf<String>()
    var mIds = mutableListOf<Int>()
    var mImageUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_list)

        initImageBitmaps()
    }
    private fun initImageBitmaps(){
        Log.e(TAG,"Prepping bitmaps")
        getParticipants()
    }

    private fun initRecyclerView(){
        Log.e(TAG,"initReyclerView")
        val recyclerView : RecyclerView = findViewById(R.id.rv_events)
        val mIdsToList = mIds.toList()
        val adapter = ResultsRecylcerViewAdapter(mNames,mTime,mImageUrls,mIdsToList,mPosition,this)
        Log.e(TAG,"$mNames")
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun getParticipants(){
        ServiceFactory.makeService(ParticipantService::class.java).readEventParticipants(1)
            .enqueue(object : Callback<List<Participant>?> {
                override fun onFailure(call: Call<List<Participant>?>, t: Throwable) {
                    Log.e(TAG, "Failure getting events")
                }
                override fun onResponse(
                    call: Call<List<Participant>?>,
                    response: Response<List<Participant>?>
                ) {
                    Log.e(TAG, "Success getting events")
                    val eventgot: List<Participant>? = response.body()
                    if (eventgot != null) {

                    }

                }
            })


    }
}




