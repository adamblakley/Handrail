package com.orienteering.handrail.manage_events

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.edit_event.EditEventActivity
import com.orienteering.handrail.event.EventActivity
import com.orienteering.handrail.models.Event
import de.hdodenhof.circleimageview.CircleImageView

class ManageEventsAdapter(eventsList : ArrayList<Event>) : RecyclerView.Adapter<ManageEventsAdapter.EventsViewHolder>() {


    var eventsList : MutableList<Event> = mutableListOf<Event>()
    var imagePaths = mutableListOf<String>()


    init {
        this.eventsList = eventsList
        for (event in eventsList) {
            for (photo in event.eventPhotographs){
                if (photo.active!!){
                    imagePaths.add(photo.photoPath)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_manage_events_item,parent,false)
        return EventsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.drawable.ic_action_event).error(R.drawable.ic_action_event)
        if (eventsList.size>=1){
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(imagePaths.get(position))
                .apply(options)
                .into(holder.eventimage)
            holder.eventName.text = eventsList[position].eventName
            when (eventsList[position].eventStatus){
                Integer(1)->{holder.eventName.setTextColor(Color.GRAY)}
                Integer(2)->{holder.eventName.setTextColor(Color.GREEN)}
                Integer(3)->{holder.eventName.setTextColor(Color.RED)}
            }
            holder.eventNote.text = eventsList[position].eventNote
        }

        holder.eventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(holder.itemView.context, EventActivity::class.java).apply {}
                intent.putExtra("EVENT_ID", eventsList[position].eventId)
                view?.context?.startActivity(intent)
            }
        })
        holder.editButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(holder.itemView.context, EditEventActivity::class.java).apply {}
                intent.putExtra("EVENT_ID", eventsList[position].eventId)
                view?.context?.startActivity(intent)
            }
        })
    }


    override fun getItemCount(): Int {
        return eventsList.size
    }

    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var eventimage : CircleImageView
        var eventName : TextView
        var eventNote : TextView
        var eventButton : Button
        var editButton : Button
        var parentLayout : LinearLayout

        init{
            eventimage = itemView.findViewById(R.id.imageCircle_manage_events_item_image)
            eventName = itemView.findViewById(R.id.textView_manage_events_item_name)
            eventNote = itemView.findViewById(R.id.textView_manage_events_item_note)
            eventButton = itemView.findViewById(R.id.button_manage_events_item_open)
            editButton = itemView.findViewById(R.id.button_manage_events_item_edit)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }
    }
}