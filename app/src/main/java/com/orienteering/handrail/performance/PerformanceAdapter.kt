package com.orienteering.handrail.performance

import android.content.Context
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
import de.hdodenhof.circleimageview.CircleImageView

class PerformanceAdapter(controlImages : List<String>, controlPositions : List<Int>, controlNames: List<String>, performanceTimes: List<String>, controlAltitudes: List<Double>): RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder>() {

    private val TAG = "PerformanceAdapter"

    var controlImages = mutableListOf<String>()
    var controlPositions = mutableListOf<Int>()
    var controlNames = mutableListOf<String>()
    var performanceTimes = mutableListOf<String>()
    var controlAltitudes = mutableListOf<Double>()

    init {
        this.controlImages= controlImages as MutableList<String>
        this.controlPositions= controlPositions as MutableList<Int>
        this.controlNames= controlNames as MutableList<String>
        this.performanceTimes= performanceTimes as MutableList<String>
        this.controlAltitudes= controlAltitudes as MutableList<Double>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_performance_item,parent,false)
        return PerformanceViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: PerformanceViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        if (controlPositions.size>=1){
            Glide.with(holder.itemView.context)
                .asBitmap()
                .apply(options)
                .load(controlImages.get(position))
                .into(holder.image)
        }

        holder.name.text=controlNames[position]
        holder.time.text=("Time: "+performanceTimes[position])
        holder.altitude.text=("Altitude Metres  %.2f".format(controlAltitudes[position]))
        holder.position.text=controlPositions[position].toString()
        holder.parentLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // no onclick
            }
        })

    }

    /**
     *     View Holder holds widgets in memory of each item
     */
    class PerformanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var image : CircleImageView
        var name : TextView
        var time : TextView
        var altitude : TextView
        var position : TextView
        var parentLayout : RelativeLayout

        init{
            image = itemView.findViewById(R.id.imageView_performance_control)
            name = itemView.findViewById(R.id.textView_performance_control_name)
            time = itemView.findViewById(R.id.textView_performance_control_time)
            altitude = itemView.findViewById(R.id.textView_performance_control_altitude)
            position = itemView.findViewById(R.id.textView_performance_control_position)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }

    }

    override fun getItemCount(): Int {
        return controlNames.size
    }

}