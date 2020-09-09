package com.orienteering.handrail.courses

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orienteering.handrail.R
import com.orienteering.handrail.course.CourseAdapter
import com.orienteering.handrail.create_course.CreateCourseActivity
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.models.Course

/**
 * Class handles view for view courses use case, displays each course with an action prompt for the user. allows user to create new views
 *
 */
class CoursesActivity : AppCompatActivity(), ICoursesContract.ICoursesView{

    private lateinit var recyclerView : RecyclerView
    private lateinit var btnCreateCourse : Button
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();
    // presenter to handle all logic and aquire data
    private lateinit var presenter : ICoursesContract.ICoursesPresenter

    /**
     * initialise view and elements, request data from presenter
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)
        initRecyclerView()
        createButtons()
        presenter = CoursesPresenter(this, CourseInteractor())
        presenter.requestDataFromServer()
        progressDialog.setMessage("Loading Content...")
        progressDialog.show()
    }

    /**
     * create all buttons for view, add onclick
     *
     */
    private fun createButtons(){
        btnCreateCourse = findViewById(R.id.button_create_course_courses)
        progressDialog = ProgressDialog(this@CoursesActivity)
        progressDialog.setCancelable(false)
        // start create course on click
        btnCreateCourse.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@CoursesActivity, CreateCourseActivity::class.java).apply {}
                startActivity(intent)
            }
        })
    }

    /**
     * Initialise recycler view
     *
     */
    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.rv_courses)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * fill recycler view with information from presenter
     *
     * @param coursesList
     */
    override fun fillInformation(coursesList: ArrayList<Course>) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val coursesAdapter = CoursesAdapter(coursesList)
        recyclerView.adapter = coursesAdapter
    }

    /**
     * Respond with failure to user
     *
     * @param throwable
     */
    override fun onResponseFailure(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Toast.makeText(this@CoursesActivity,"Error: Connectivity Error, unable to retrieve courses", Toast.LENGTH_SHORT).show()
    }

    /**
     * Responsd with error to user
     *
     */
    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Toast.makeText(this@CoursesActivity,"No Courses available",Toast.LENGTH_SHORT).show()
    }

    /**
     * Override ondestroy add presenter ondestroy
     *
     */
    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
