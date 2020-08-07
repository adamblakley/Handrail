package com.orienteering.handrail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.controllers.CourseController
import com.orienteering.handrail.utilities.CoursesRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoursesActivity : AppCompatActivity() {

    // Course Controller
    lateinit var courseController: CourseController

    private val callback = object: Callback<List<Course>> {
        override fun onFailure(call: Call<List<Course>?>, t: Throwable) {
            Log.e(TAG, "Failure getting courses")
        }

        override fun onResponse(call: Call<List<Course>?>, response: Response<List<Course>?>) {
            Log.e(TAG, "Success getting courses")
            val coursegot: List<Course>? = response.body()
            if (coursegot != null) {
                for (course in coursegot) {
                    mNames.add(course.courseName)
                    mNotes.add("NOTE EXAMPLE")
                    mIds.add(course.courseId)
                }
            }
            initRecyclerView()
        }
    }

    var mNames = mutableListOf<String>()
    var mNotes = mutableListOf<String>()
    var mIds = mutableListOf<Int?>()
    var mImageUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)

        this.courseController= CourseController()
        getCourses()
    }

    private fun initRecyclerView(){
        val recyclerView : RecyclerView = findViewById(R.id.rv_courses)
        val adapter = CoursesRecyclerViewAdapter(mNames,mImageUrls,mIds,this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun getCourses(){
        courseController.retreive(callback)
    }

}

private const val TAG = "CoursesActivity"
