package com.orienteering.handrail.utilities

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

class ViewPerformanceRecyclerViewAdapter(controlImages : MutableList<String>, controlPositions : MutableList<Int>, controlNames: MutableList<String>, performanceTimes: MutableList<String>, controlAltitudes: MutableList<Double>, context: Context) : RecyclerView.Adapter<ViewPerformanceRecyclerViewAdapter.ViewHolder>() {

    private val TAG = "PerformanceRVA"

    var controlImages = mutableListOf<String>()
    var controlPositions = mutableListOf<Int>()
    var controlNames = mutableListOf<String>()
    var performanceTimes = mutableListOf<String>()
    var controlAltitudes = mutableListOf<Double>()
    var performanceDistance = mutableListOf<Float>()
    var context : Context

    init {
        this.controlImages=controlImages
        this.controlPositions=controlPositions
        this.controlNames=controlNames
        this.performanceTimes=performanceTimes
        this.controlAltitudes= controlAltitudes
        this.performanceDistance=performanceDistance
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_performance_item,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return position
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

        holder.name.text=controlNames[position]
        holder.time.text=("Time: "+performanceTimes[position])
        holder.altitude.text=("Altitude Metres  %.2f".format(controlAltitudes[position]))
        holder.position.text=controlPositions[position].toString()
        holder.parentLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

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