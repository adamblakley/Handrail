package com.orienteering.handrail.events

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
import com.orienteering.handrail.event.EventActivity
import com.orienteering.handrail.models.Event
import de.hdodenhof.circleimageview.CircleImageView

class EventsAdapter(eventsList : ArrayList<Event>) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>(){

    val TAG = "EventsAdapter"
    var eventsList : ArrayList<Event>
    var imageUrls = mutableListOf<String>()

    init{
        this.eventsList = eventsList
        for (event in eventsList) {
            for (photo in event.eventPhotographs){
                if (photo.active!!){
                    imageUrls.add(photo.photoPath)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_event_item,parent,false)
        return EventsViewHolder(view)
    }


    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")

        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        if (eventsList.size>=1){
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(imageUrls.get(position))
                .apply(options)
                .into(holder.eventimage)
            holder.eventName.text = eventsList[position].eventName
            holder.eventNote.text = eventsList[position].eventNote
        }

        holder.eventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(holder.itemView.context, EventActivity::class.java).apply {}
                intent.putExtra("EVENT_ID", eventsList[position].eventId)
                view?.context?.startActivity(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    /**
     *     View Holder holds widgets in memory of each item
     */
    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

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