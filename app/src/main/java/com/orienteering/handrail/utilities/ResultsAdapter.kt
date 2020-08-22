package com.orienteering.handrail.utilities

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
import com.orienteering.handrail.course.CourseActivity
import com.orienteering.handrail.models.Course
import de.hdodenhof.circleimageview.CircleImageView

class ResultsAdapter(courseList : ArrayList<Course>) : RecyclerView.Adapter<ResultsAdapter.CoursesViewHolder>(){

    val TAG = "EventsAdapter"
    var courseList : ArrayList<Course>
    var imageUrls = mutableListOf<String>()

    init{
        this.courseList = courseList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_course_item,parent,false)
        return CoursesViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    override fun onBindViewHolder(holder: CoursesViewHolder, position: Int) {
        Log.e(TAG,"onBindViewHolder: Called")
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        if (imageUrls.size>=1){
            Glide.with(holder.itemView.context)
                .asBitmap()
                .apply(options)
                .load(imageUrls.get(position))
                .into(holder.courseImage)
        }

        holder.courseName.text = courseList[position].courseName
        holder.courseButton.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {

                val intent = Intent(holder.itemView.context, CourseActivity::class.java).apply {}
                intent.putExtra("COURSE_ID", courseList[position].courseId)
                view?.context?.startActivity(intent)

            }
        })
    }

    /**
     *     View Holder holds widgets in memory of each item
     */
    class CoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

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