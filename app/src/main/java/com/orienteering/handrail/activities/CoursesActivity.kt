package com.orienteering.handrail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.controllers.CourseController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.CoursesRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoursesActivity : AppCompatActivity() {

    // Course Controller
    lateinit var courseController: CourseController

    private val callback = object: Callback<StatusResponseEntity<List<Course>>> {
        override fun onFailure(call: Call<StatusResponseEntity<List<Course>>>, t: Throwable) {
            Log.e(TAG, "Failure connecting to service")
            val toast = Toast.makeText(this@CoursesActivity,"Service Unavailable",Toast.LENGTH_SHORT)
            toast.show()
        }

        override fun onResponse(call: Call<StatusResponseEntity<List<Course>>>, response: Response<StatusResponseEntity<List<Course>>>) {

            if (response.isSuccessful){
                Log.e(TAG, "Success getting courses")
                val coursegot: List<Course>? = response.body()?.entity
                if (coursegot != null) {
                    for (course in coursegot) {
                        mNames.add(course.courseName)
                        mNotes.add("NOTE EXAMPLE")
                        mIds.add(course.courseId)
                    }
                }
                initRecyclerView()
            } else {
                Log.e(TAG, "Failure getting courses")
                val toast = Toast.makeText(this@CoursesActivity,"Service Unavailable",Toast.LENGTH_SHORT)
                toast.show()
            }

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
        courseController.retrieveAllByUser(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),callback)
    }

}

private const val TAG = "CoursesActivity"
