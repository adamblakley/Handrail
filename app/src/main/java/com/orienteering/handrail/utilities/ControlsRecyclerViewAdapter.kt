package com.orienteering.handrail.utilities

import android.content.Context
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
import com.orienteering.handrail.activities.CourseActivity
import com.orienteering.handrail.models.Control
import de.hdodenhof.circleimageview.CircleImageView

class ControlsRecyclerViewAdapter(controls : MutableList<Control>, context: Context) : RecyclerView.Adapter<ControlsRecyclerViewAdapter.ViewHolder>() {
    private val TAG : String = "ControlRVAdapter"

    var controls : MutableList<Control> = mutableListOf<Control>()
    var imagePaths : Array<String?>
    var context : Context

    init {
        this.context = context
        this.controls=controls
        this.imagePaths = arrayOfNulls(controls.size)

        for(control in controls){
            if (control.isControlPhotographInitialised()){

                for (photo in control.controlPhotographs){
                    if (photo.active!!){
                        imagePaths.set(controls.indexOf(control),photo.photoPath)
                    }
                }


            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_control_item,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return controls.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        if (controls.size>=1){
            Glide.with(context)
                .asBitmap()
                .apply(options)
                .load(imagePaths[position])
                .into(holder.controlImage)
        }
        holder.controlPosition.text = controls.get(position).controlPosition.toString()
        holder.controlName.text = controls.get(position).controlName
        holder.controlButton.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {
                if (context is CourseActivity){
                    (context as CourseActivity).displayControlDialog(controls[position])
                }
            }
        })
    }


    /**
     *     View Holder holds widgets in memory of each item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var controlImage : CircleImageView
        var controlPosition : TextView
        var controlName : TextView
        var controlButton : Button
        var parentLayout : RelativeLayout

        init{
            controlImage = itemView.findViewById(R.id.imageCircle_control_item_image)
            controlPosition = itemView.findViewById(R.id.textView_control_item_position)
            controlName = itemView.findViewById(R.id.textView_control_item_name)
            controlButton = itemView.findViewById(R.id.button_control_item_info)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }

    }
}