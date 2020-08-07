package com.orienteering.handrail.utilities

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.activities.ViewEventActivity
import de.hdodenhof.circleimageview.CircleImageView

class EventsRecyclerViewAdapter(eventNames : MutableList<String>, eventNotes: MutableList<String>, eventImages: MutableList<String>, eventIds : List<Int?>, context: Context) : RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder>() {

    private val TAG : String = "ERVAdapter"

    var eventNames = mutableListOf<String>()
    var eventNotes = mutableListOf<String>()
    var eventImages = mutableListOf<String>()
    var eventIds = mutableListOf<Int>()
    var context : Context



    init {
        this.eventNames = eventNames
        this.eventNotes = eventNotes
        this.eventImages = eventImages
        this.eventIds = eventIds as MutableList<Int>
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_event_item,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return eventNames.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")

        val options : RequestOptions  = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)

        if (eventImages.size>=1){
            Glide.with(context)
                .asBitmap()
                .load(eventImages.get(position))
                .apply(options)
                .into(holder.eventimage)
        }

        holder.eventName.text = eventNames[position]
        holder.eventNote.text = eventNotes[position]
        holder.eventButton.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {
                Log.e(TAG,"onClick : clicked ${eventNames[position]}")

                val intent = Intent(context, ViewEventActivity::class.java).apply {}

                intent.putExtra("EVENT_ID", eventIds[position])

                view?.context?.startActivity(intent)

            }
        })
    }


    /**
     *     View Holder holds widgets in memory of each item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var eventimage : CircleImageView
        var eventName : TextView
        var eventNote : TextView
        var eventButton : Button
        var parentLayout : RelativeLayout

        init{
            eventimage = itemView.findViewById(R.id.imageCircle_event_item_image)
            eventName = itemView.findViewById(R.id.textView_event_item_name)
            eventNote = itemView.findViewById(R.id.textView_event_item_note)
            eventButton = itemView.findViewById(R.id.button_event_item_open)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }

    }
}