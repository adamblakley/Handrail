package com.orienteering.handrail.course

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
import com.orienteering.handrail.models.Control
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Adapter to add control passed elements to recyclerview
 *
 * @constructor
 *
 * @param controls
 */
class CourseAdapter(controls : List<Control>) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    var controls : MutableList<Control> = mutableListOf<Control>()
    // image url paths to display in image view
    private var imagePaths : Array<String?>


    /**
     * initialise controls and image paths, find active control photos
     */
    init {
        this.controls= controls as MutableList<Control>
        this.imagePaths = arrayOfNulls(controls.size)

        for(control in controls){
            if (control.isControlPhotographInitialised()){

                for (photo in control.controlPhotographs){
                    if (photo.active!!){
                        imagePaths[controls.indexOf(control)] = photo.photoPath
                    }
                }
            }
        }
    }

    /**
     * inflate view
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_control_item,parent,false)
        return CourseViewHolder(
            view
        )
    }

    /**
     * Add images and control information to each item in the list by binding to textview and images
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        if (controls.size>=1){
            // Glide utilized to load images into view
            Glide.with(holder.itemView.context)
                .asBitmap()
                .apply(options)
                .load(imagePaths[position])
                .into(holder.controlImage)
        }
        holder.controlPosition.text = controls[position].controlPosition.toString()
        holder.controlName.text = controls[position].controlName
        holder.controlButton.setOnClickListener(object : View.OnClickListener {
            // load control information via dialog on click
            override fun onClick(view: View?) {
                if (holder.itemView.context is CourseActivity){
                    (holder.itemView.context as CourseActivity).presenter.controlInformation(controls[position].controlName)
                }
            }
        })
    }


    /**
     * @return
     */
    override fun getItemCount(): Int {
        return controls.size
    }

    /**
     * Bind items to variables
     *
     * @constructor
     *
     * @param itemView
     */
    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var controlImage: CircleImageView
        var controlPosition: TextView
        var controlName: TextView
        var controlButton: Button
        var parentLayout: RelativeLayout


        init {
            controlImage = itemView.findViewById(R.id.imageCircle_control_item_image)
            controlPosition = itemView.findViewById(R.id.textView_control_item_position)
            controlName = itemView.findViewById(R.id.textView_control_item_name)
            controlButton = itemView.findViewById(R.id.button_control_item_info)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }
    }
}

