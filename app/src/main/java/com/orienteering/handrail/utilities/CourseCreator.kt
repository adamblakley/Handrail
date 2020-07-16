package com.orienteering.handrail.utilities

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

import com.orienteering.handrail.activities.HomeActivity

import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.httprequests.CourseService
import com.orienteering.handrail.httprequests.ServiceFactory
import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseCreator {



    companion object{

        val TAG = CourseCreator::class.qualifiedName


        fun uploadCourse(course: Course, context : Context){
            ServiceFactory.makeService(CourseService::class.java).create(course).enqueue(object :
                Callback<StatusResponseEntity<Course>?> {
                override fun onFailure(call: Call<StatusResponseEntity<Course>?>, t: Throwable) {
                    Log.e(TAG,"Failure adding Course")
                    Toast.makeText(context,"Failure Creating Course. Network Error.",Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<StatusResponseEntity<Course>?>,
                    response: Response<StatusResponseEntity<Course>?>
                ) {
                    Log.e(TAG,"Success adding Course")
                    Toast.makeText(context,"Success Creating Course",Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, HomeActivity::class.java).apply {}
                    context.startActivity(intent)
                }
            })
        }

    }

}