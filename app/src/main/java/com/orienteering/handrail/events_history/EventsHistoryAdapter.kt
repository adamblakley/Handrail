package com.orienteering.handrail.events_history

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
import com.orienteering.handrail.activities.ViewPerformanceActivity
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.performance.PerformanceActivity
import de.hdodenhof.circleimageview.CircleImageView

class EventsHistoryAdapter(eventsList : ArrayList<Event>) : RecyclerView.Adapter<EventsHistoryAdapter.EventsHistoryViewHolder>()  {

    val TAG = "EventsHistoryAdapter"
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_event_history_item,parent,false)
        return EventsHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (eventsList.size>0){
            return eventsList.size
        } else{
            return 0
        }
    }

    override fun onBindViewHolder(holder: EventsHistoryViewHolder, position: Int) {
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(
            R.mipmap.ic_launcher_round)

        if (eventsList.size>=1){
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(imageUrls.get(position))
                .apply(options)
                .into(holder.image)
        }

        holder.imageName.text = eventsList[position].eventName
        holder.viewButton.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {
                val intent = Intent(holder.itemView.context, PerformanceActivity::class.java).apply {}
                intent.putExtra("EVENT_ID", eventsList[position].eventId)
                view?.context?.startActivity(intent)
            }
        })
    }

    /**
     *     View Holder holds widgets in memory of each item
     */
    class EventsHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var image : CircleImageView
        var imageName : TextView
        var viewButton : Button
        var parentLayout : RelativeLayout

        init{
            image = itemView.findViewById(R.id.imageCircle_event_history_image)
            imageName = itemView.findViewById(R.id.textView_event_history_name)
            viewButton = itemView.findViewById(R.id.button_event_history_view)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }
    }
}