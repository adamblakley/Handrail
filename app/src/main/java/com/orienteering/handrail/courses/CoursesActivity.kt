package com.orienteering.handrail.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.create_course.CreateCourseActivity
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.models.Course

class CoursesActivity : AppCompatActivity(), ICoursesContract.ICoursesView{

    private lateinit var recyclerView : RecyclerView
    private lateinit var btnCreateCourse : Button
    private lateinit var performer : ICoursesContract.ICoursesPerformer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)
        initRecyclerView()
        createButtons()
        performer = CoursesPerformer(this, CourseInteractor())
        performer.requestDataFromServer()
    }

    private fun createButtons(){
        btnCreateCourse = findViewById(R.id.button_create_course_courses)
        btnCreateCourse?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@CoursesActivity, CreateCourseActivity::class.java).apply {}
                startActivity(intent)
            }
        })
    }

    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_courses)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun fillRecyclerView(coursesList: ArrayList<Course>) {
        val coursesAdapter : ResultsAdapter = ResultsAdapter(coursesList)
        recyclerView.adapter = coursesAdapter
    }

    override fun onResponseFailure(throwable: Throwable) {
        val toast = Toast.makeText(this@CoursesActivity,"Error: Connectivity Error, unable to retreive courses", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseError() {
        val toast = Toast.makeText(this@CoursesActivity,"No Courses available",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onDestroy() {
        performer.onDestroy()
        super.onDestroy()
    }
}
