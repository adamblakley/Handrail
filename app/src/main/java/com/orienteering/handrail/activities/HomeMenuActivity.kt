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

    var button1: Button? = null
    var button2: Button? = null
    var button3: Button? = null

    var button8: Button? = null
    var button9: Button? = null

    lateinit var imageview1: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_menu)

        button1 = findViewById<Button>(R.id.btn_create_course)
        button2 = findViewById<Button>(R.id.btn_compete_event)
        button3 = findViewById<Button>(R.id.btn_create_event)
        button8 = findViewById<Button>(R.id.btn_upload_photo)

        button9 = findViewById<Button>(R.id.btn_logout)

        imageview1 = findViewById<ImageView>(R.id.imageview_myphoto)

        button9?.setOnClickListener(object : View.OnClickListener {
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

        button1?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val intent = Intent(this@HomeActivity, CreateMapsActivity::class.java).apply {}
                startActivity(intent)
            }

        })

        button2?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val intent = Intent(this@HomeActivity, EventsActivity::class.java).apply {}
                startActivity(intent)
            }
        })

        button3?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, CreateEventActivity::class.java).apply {}
                startActivity(intent)
            }
        })



        fun getUsers() {
            ServiceFactory.makeService(UserService::class.java).readAll()
                .enqueue(object : retrofit2.Callback<List<User>?> {
                    override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                        Log.e(TAG, "Failure getting users")
                    }

                    override fun onResponse(
                        call: Call<List<User>?>,
                        response: Response<List<User>?>
                    ) {
                        Log.e(TAG, "Success getting users")
                        val usersgot: List<User>? = response.body()
                        if (usersgot != null) {
                            for (user in usersgot) {
                                Log.e(TAG, user.toString())
                            }
                        }
                    }
                })
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
                                Log.e(TAG, course.toString())
                            }
                        }
                    }
                })
        }

        fun getControls() {
            ServiceFactory.makeService(ControlService::class.java).readAll()
                .enqueue(object : retrofit2.Callback<List<Control>?> {
                    override fun onFailure(call: Call<List<Control>?>, t: Throwable) {
                        Log.e(TAG, "Failure getting controls")
                    }

                    override fun onResponse(
                        call: Call<List<Control>?>,
                        response: Response<List<Control>?>
                    ) {
                        Log.e(TAG, "Success getting controls")
                        val controlsgot: List<Control>? = response.body()
                        if (controlsgot != null) {
                            for (control in controlsgot) {
                                Log.e(TAG, control.toString())
                            }
                        }
                    }
                })
        }


    }
}

private const val TAG = "HomeActivity"

