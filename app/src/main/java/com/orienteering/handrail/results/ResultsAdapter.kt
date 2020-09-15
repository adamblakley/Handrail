package com.orienteering.handrail.results

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

/**
 * Class responsible for adapting results information to list items fopr display
 *
 * @constructor
 *
 * @param participantNames
 * @param participantTimes
 * @param participantImages
 * @param participantIds
 * @param participantPositions
 */
class ResultsAdapter(participantNames: List<String>, participantTimes: List<String>, participantImages: List<String>, participantIds: List<Int?>, participantPositions: List<Int>) : RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    /**
     * Variable lists of performer results information for display
     */
    private var participantNames = mutableListOf<String>()
    private var participantTimes = mutableListOf<String>()
    private var participantImages = mutableListOf<String>()
    private var participantIds = mutableListOf<Int>()
    private var participantPositions = mutableListOf<Int>()


    /**
     * Initiate lists of all results information
     */
    init {
        this.participantNames = participantNames as MutableList<String>
        this.participantTimes = participantTimes as MutableList<String>
        this.participantImages = participantImages as MutableList<String>

        this.participantIds = participantIds as MutableList<Int>
        this.participantPositions = participantPositions as MutableList<Int>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_results_item,parent,false)
        val viewHolder = ResultsViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return participantNames.size
    }


    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        // utilise glide to load performer images if available
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.drawable.ic_action_user).error(R.drawable.ic_action_user)
        if (participantImages.size>=1){
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(participantImages[position])
                .apply(options)
                .into(holder.image)
        }
        // set performer colour based on position
        when (position){
            0 -> holder.image.borderColor = Color.MAGENTA
            1 -> holder.image.borderColor = Color.BLUE
            2 -> holder.image.borderColor = Color.GREEN
            3 -> holder.image.borderColor= Color.YELLOW
            4 -> holder.image.borderColor = Color.RED
        }

        holder.name.text=participantNames[position]
        holder.time.append(participantTimes[position])
        holder.position.text=(participantPositions[position].toInt()+1).toString()
        holder.parentLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // no onclick
            }
        })

    }

    /**
     *     View Holder holds widgets in memory of each item
     * @constructor
     * @param itemView
     */
    class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

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
