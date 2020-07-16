package com.orienteering.handrail.utilities

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orienteering.handrail.R
import com.orienteering.handrail.activities.ViewEventActivity
import de.hdodenhof.circleimageview.CircleImageView

class ResultsRecylcerViewAdapter(participantNames : MutableList<String>,participantTimes : MutableList<String>,participantImages : MutableList<String>,participantIds : List<Int?>,participantPositions : MutableList<String>, context : Context ) : RecyclerView.Adapter<ResultsRecylcerViewAdapter.ViewHolder>() {

    private val TAG : String = "ResultsAdapter"

    var participantNames = mutableListOf<String>()
    var participantTimes = mutableListOf<String>()
    var participantImages = mutableListOf<String>()
    var participantIds = mutableListOf<Int>()
    var participantPositions = mutableListOf<String>()
    var context : Context

    init {
        this.participantNames = participantNames
        this.participantTimes = participantTimes
        this.participantImages = participantImages
        this.participantIds = participantIds as MutableList<Int>
        this.participantPositions = participantPositions
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_results_item,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {

        return participantNames.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")
        if (participantImages.size>=1){
            Glide.with(context)
                .asBitmap()
                .load(participantImages.get(position))
                .into(holder.image)
        }

        holder.name.text=participantNames[position]
        holder.time.text=participantTimes[position]
        holder.position.text=participantPositions[position]
        holder.parentLayout.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {
                Log.e(TAG,"onClick : clicked ${participantNames[position]}")

                val intent = Intent(context, ViewEventActivity::class.java).apply {}

                intent.putExtra("PARTICIPANT_ID", participantIds[position])

                view?.context?.startActivity(intent)

            }
        })

    }

    /**
     *     View Holder holds widgets in memory of each item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var image : CircleImageView
        var name : TextView
        var time : TextView
        var position : TextView
        var parentLayout : RelativeLayout

        init{
            image = itemView.findViewById(R.id.results_image)
            name = itemView.findViewById(R.id.results_name)
            time = itemView.findViewById(R.id.results_time)
            position = itemView.findViewById(R.id.results_position)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }

    }


}