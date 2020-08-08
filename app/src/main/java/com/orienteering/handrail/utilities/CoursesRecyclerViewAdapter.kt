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
import com.orienteering.handrail.activities.CourseActivity
import de.hdodenhof.circleimageview.CircleImageView

class CoursesRecyclerViewAdapter(courseNames : MutableList<String>, courseImages: MutableList<String>, courseIds : List<Int?>, context: Context) : RecyclerView.Adapter<CoursesRecyclerViewAdapter.ViewHolder>() {

    private val TAG : String = "CRVAdapter"

    var courseNames = mutableListOf<String>()
    var courseImages = mutableListOf<String>()
    var courseIds = mutableListOf<Int>()
    var context : Context

    init {
        this.courseNames = courseNames
        this.courseImages = courseImages
        this.courseIds = courseIds as MutableList<Int>
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_course_item,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return courseNames.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        if (courseImages.size>=1){
            Glide.with(context)
                .asBitmap()
                .apply(options)
                .load(courseImages.get(position))
                .into(holder.courseImage)
        }

        holder.courseName.text = courseNames[position]
        holder.courseButton.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {
                Log.e(TAG,"onClick : clicked ${courseNames[position]}")

                val intent = Intent(context, CourseActivity::class.java).apply {}

                intent.putExtra("COURSE_ID", courseIds[position])

                view?.context?.startActivity(intent)

            }
        })
    }


    /**
     *     View Holder holds widgets in memory of each item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var courseImage : CircleImageView
        var courseName : TextView
        var courseButton : Button
        var parentLayout : RelativeLayout

        init{
            courseImage = itemView.findViewById(R.id.imageCircle_course_item_image)
            courseName = itemView.findViewById(R.id.textView_course_item_name)
            courseButton = itemView.findViewById(R.id.button_course_item_open)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }

    }



}