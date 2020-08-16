package com.orienteering.handrail.courses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.models.Course

class CoursesActivity : AppCompatActivity(), ICoursesContract.ICoursesView{

    private lateinit var recyclerView : RecyclerView
    private lateinit var performer : ICoursesContract.ICoursesPerformer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)
        initRecyclerView()

        performer = CoursesPerformer(this, CourseInteractor())
        performer.requestDataFromServer()
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
