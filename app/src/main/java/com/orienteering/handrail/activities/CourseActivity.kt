package com.orienteering.handrail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.services.CourseService
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.utilities.RecyclerViewAdapter

import retrofit2.Call
import retrofit2.Response

class CourseActivity : AppCompatActivity() {

    val TAG : String = "CoursesActivity"
    lateinit var myCourse : Course

    var mNames = mutableListOf<String>()
    var mNotes = mutableListOf<String>()
    var mImageUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)



        initImageBitmaps()
    }


    private fun initImageBitmaps(){
        Log.e(TAG,"Prepping bitmaps")
        getCourse()

    }

    private fun initRecyclerView(){
        Log.e(TAG,"initReyclerView")
        val recyclerView : RecyclerView = findViewById(R.id.rv_course)
        val adapter = RecyclerViewAdapter(mNames,mNotes,mImageUrls,this)
        Log.e(TAG,"$mNames")
        recyclerView.adapter=adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun getCourse() {
        if (intent.extras!=null){

         val courseIdPassed =  intent.getSerializableExtra("COURSE_ID") as Int

        ServiceFactory.makeService(
            CourseService::class.java).read(courseIdPassed)
            .enqueue(object : retrofit2.Callback<Course> {
                override fun onFailure(call: Call<Course>, t: Throwable) {
                    Log.e(TAG, "Failure getting course")
                }

                override fun onResponse(
                    call: Call<Course>,
                    response: Response<Course>
                ) {
                    Log.e(TAG, "Success getting course")
                    val coursegot: Course? = response.body()
                    if (coursegot != null) {
                        myCourse = coursegot
                        for (control in myCourse.courseControls){
                            mNames.add(control.controlName)
                            Log.e(TAG,"${control.controlName}")
                            mNotes.add(control.controlNote)
                        }
                    }
                    initRecyclerView()
                }
            })
        }
    }
}
