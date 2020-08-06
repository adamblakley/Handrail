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
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.activities.ViewEventActivity
import de.hdodenhof.circleimageview.CircleImageView

class PerformanceReyclerViewAdapter(controlImages : MutableList<String>, controlPositions : MutableList<String>, controlNames: MutableList<String>, performanceTimes: MutableList<String>, context: Context) : RecyclerView.Adapter<PerformanceReyclerViewAdapter.ViewHolder>() {

    private val TAG = "PerformanceRVA"

    var controlImages = mutableListOf<String>()
    var controlPositions = mutableListOf<String>()
    var controlNames = mutableListOf<String>()
    var performanceTimes = mutableListOf<String>()
    var performanceDistance = mutableListOf<Float>()
    var context : Context

    init {
        this.controlImages=controlImages
        this.controlPositions=controlPositions
        this.controlNames=controlNames
        this.performanceTimes=performanceTimes
        this.performanceDistance=performanceDistance
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_results_item,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return controlPositions.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        if (controlPositions.size>=1){
            Glide.with(context)
                .asBitmap()
                .apply(options)
                .load(controlImages.get(position))
                .into(holder.image)
        }

        holder.name.text=participantNames[position]
        holder.time.text=participantTimes[position]
        holder.position.text=participantPositions[position].toString()
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

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


}