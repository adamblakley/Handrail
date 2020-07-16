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
import com.orienteering.handrail.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_course_item.view.*

class RecyclerViewAdapter(mImageNames : MutableList<String>, mImageNotes: MutableList<String>, mImages: MutableList<String>, mContext: Context) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val TAG : String = "RecyclerViewAdapter"

    var mImageNames = mutableListOf<String>()
    var mImageNotes = mutableListOf<String>()
    var mImages = mutableListOf<String>()
    var mContext : Context



    init {
        this.mImageNames = mImageNames
        this.mImageNotes = mImageNotes
        this.mImages = mImages
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_course_item,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mImageNames.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")

        if (mImages.size>=1){
            Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image)
        }

        holder.imageName.setText(mImageNames.get(position))
        holder.imageDescription.setText(mImageNotes.get(position))
        holder.parentLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                Log.e(TAG,"onClick : clicked ${mImageNames.get(position)}")
            }
        })
    }


    /**
     *     View Holder holds widgets in memory of each item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var image : CircleImageView
        var imageName : TextView
        var imageDescription : TextView
        var parentLayout : RelativeLayout

        init{
            image = itemView.findViewById(R.id.image)
            imageName = itemView.findViewById(R.id.image_name)
            imageDescription = itemView.findViewById(R.id.image_note)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }

    }



}