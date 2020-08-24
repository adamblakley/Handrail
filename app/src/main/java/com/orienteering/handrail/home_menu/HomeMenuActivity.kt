package com.orienteering.handrail.home_menu


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.orienteering.handrail.*
import com.orienteering.handrail.courses.CoursesActivity
import com.orienteering.handrail.events.EventsActivity
import com.orienteering.handrail.events_history.EventsHistoryActivity
import com.orienteering.handrail.manage_events.ManageEventsActivity
import com.orienteering.handrail.utilities.App


class HomeActivity : AppCompatActivity() {

    lateinit var buttonCompete: Button
    lateinit var buttonCreateEvent: Button
    lateinit var buttonEventHistory: Button
    lateinit var buttonViewCourses: Button
    lateinit var buttonEditProfile: Button
    lateinit var buttonLogout: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_menu)

        buttonCompete = findViewById<Button>(R.id.btn_compete_event)
        buttonCreateEvent = findViewById<Button>(R.id.btn_create_event)
        buttonEventHistory = findViewById<Button>(R.id.btn_view_performances)
        buttonViewCourses = findViewById<Button>(R.id.btn_view_courses)
        buttonEditProfile = findViewById(R.id.btn_edit_profile)
        buttonLogout = findViewById<Button>(R.id.btn_logout)

        buttonCompete?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, EventsActivity::class.java).apply {}
                startActivity(intent)
            }
        })

        buttonCreateEvent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, ManageEventsActivity::class.java).apply {}
                startActivity(intent)
            }
        })



        buttonEventHistory?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, EventsHistoryActivity::class.java).apply {}
                startActivity(intent)
            }

        })

        buttonViewCourses?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, CoursesActivity::class.java).apply {}
                startActivity(intent)
            }

        })

        buttonEditProfile?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, com.orienteering.handrail.edit_profile.EditProfileActivity::class.java).apply {}
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
                val intent = Intent(this@HomeActivity, com.orienteering.handrail.welcome.WelcomeActivity::class.java).apply {}
                startActivity(intent)
            }

        })

    }
}

private const val TAG = "HomeActivity"

