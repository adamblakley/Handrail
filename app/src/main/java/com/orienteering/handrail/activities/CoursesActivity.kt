package com.orienteering.handrail.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.orienteering.handrail.*
import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.httprequests.CourseService
import com.orienteering.handrail.httprequests.ServiceFactory
import com.orienteering.handrail.utilities.CoursesRecyclerViewAdapter
import com.orienteering.handrail.utilities.RecyclerViewAdapter
import retrofit2.Call
import retrofit2.Response

class CoursesActivity : AppCompatActivity() {

    val TAG : String = "CourseActivity"
    private lateinit var courseMap: GoogleMap
    lateinit var myCourse : Course

    var mNames = mutableListOf<String>()
    var mNotes = mutableListOf<String>()
    var mIds = mutableListOf<Int?>()
    var mImageUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)

        initImageBitmaps()
    }


    private fun initImageBitmaps(){
        Log.e(TAG,"Prepping bitmaps")
        getCourses()
    }

    private fun initRecyclerView(){
        Log.e(TAG,"initReyclerView")
        val recyclerView : RecyclerView = findViewById(R.id.rv_courses)
        val mIdsToList = mIds.toList()
        val adapter = CoursesRecyclerViewAdapter(mNames,mNotes,mImageUrls,mIdsToList,this)
        Log.e(TAG,"$mNames")
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun getCourses() {
        ServiceFactory.makeService(CourseService::class.java).readAll()
            .enqueue(object : retrofit2.Callback<List<Course>?> {
                override fun onFailure(call: Call<List<Course>?>, t: Throwable) {
                    Log.e(TAG, "Failure getting courses")
                }

                override fun onResponse(
                    call: Call<List<Course>?>,
                    response: Response<List<Course>?>
                ) {
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
            })
    }

}

private const val TAG = "CoursesActivity"
