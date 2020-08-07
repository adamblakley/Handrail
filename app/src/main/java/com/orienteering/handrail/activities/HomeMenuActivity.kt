package com.orienteering.handrail.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.orienteering.handrail.*
import com.orienteering.handrail.classes.Control
import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.classes.User
import com.orienteering.handrail.services.*
import com.orienteering.handrail.utilities.App
import retrofit2.Call
import retrofit2.Response


class HomeActivity : AppCompatActivity() {

    lateinit var buttonCreateCourse: Button
    lateinit var buttonCompete: Button
    lateinit var buttonCreateEvent: Button
    lateinit var buttonEventHistory: Button
    lateinit var buttonViewCourses: Button
    lateinit var buttonLogout: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_menu)

        buttonCreateCourse = findViewById<Button>(R.id.btn_create_course)
        buttonCompete = findViewById<Button>(R.id.btn_compete_event)
        buttonCreateEvent = findViewById<Button>(R.id.btn_create_event)
        buttonEventHistory = findViewById<Button>(R.id.btn_view_performances)
        buttonViewCourses = findViewById<Button>(R.id.btn_view_courses)
        buttonLogout = findViewById<Button>(R.id.btn_logout)


        buttonCreateCourse?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, CreateMapsActivity::class.java).apply {}
                startActivity(intent)
            }

        })

        buttonCompete?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, EventsActivity::class.java).apply {}
                startActivity(intent)
            }
        })

        buttonCreateEvent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, CreateEventActivity::class.java).apply {}
                startActivity(intent)
            }
        })

        buttonLogout?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val sharedPreferences = App.sharedPreferences
                val sharedPreferencesEditor = sharedPreferences.edit()
                sharedPreferencesEditor.remove(App.SharedPreferencesAuthToken).commit()
                sharedPreferencesEditor.remove(App.SharedPreferencesAuthToken).commit()
                sharedPreferencesEditor.remove(App.SharedPreferencesUserId).commit()
                val intent = Intent(this@HomeActivity, WelcomeActivity::class.java).apply {}
                startActivity(intent)
            }

        })

        buttonEventHistory?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, EventHistory::class.java).apply {}
                startActivity(intent)
            }

        })

        buttonViewCourses?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, CoursesActivity::class.java).apply {}
                startActivity(intent)
            }

        })
    }
}

private const val TAG = "HomeActivity"

