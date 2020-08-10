package com.orienteering.handrail.utilities

import android.content.Context
import android.graphics.Color
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

class PerformanceRecyclerViewAdapter(
    participantNames: MutableList<String>,
    participantTimes: MutableList<String>,
    participantImages: MutableList<String>,
    participantIds: List<Int?>,
    participantPositions: MutableList<Int>, context: Context ) : RecyclerView.Adapter<PerformanceRecyclerViewAdapter.ViewHolder>() {

    private val TAG : String = "PerformanceAdapter"

    var participantNames = mutableListOf<String>()
    var participantTimes = mutableListOf<String>()
    var participantImages = mutableListOf<String>()

    var participantIds = mutableListOf<Int>()
    var participantPositions = mutableListOf<Int>()
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

        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(
            R.mipmap.ic_launcher_round)
        if (participantImages.size>=1){
            Glide.with(context)
                .asBitmap()
                .load(participantImages.get(position))
                .apply(options)
                .into(holder.image)
        }

        when (position){
            0 -> holder.image.borderColor = Color.MAGENTA
            1 -> holder.image.borderColor = Color.BLUE
            2 -> holder.image.borderColor = Color.GREEN
            3 -> holder.image.borderColor= Color.YELLOW
            5 -> holder.image.borderColor = Color.CYAN
        }

        holder.name.text=participantNames[position]
        holder.time.append(participantTimes[position])
        holder.position.text=participantPositions[position].toString()
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