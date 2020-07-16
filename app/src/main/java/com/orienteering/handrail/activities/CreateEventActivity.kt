package com.orienteering.handrail.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.httprequests.CourseService
import com.orienteering.handrail.httprequests.ServiceFactory
import com.orienteering.handrail.utilities.EventCreator
import com.orienteering.handrail.utilities.GeofencingConstants
import retrofit2.Call
import retrofit2.Response
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    val TAG : String = "CreateEventActivity"

    var eventName : String? = null
    var eventDescription : String? = null
    lateinit var eventDate : String
    lateinit var eventTime : String
    lateinit var eventcourse : Course

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    lateinit var eventNameDisplay : TextView
    lateinit var eventDescriptionDisplay : TextView

    var buttonUpdatePhoto: Button? = null
    var buttonSelectDate: Button? = null
    var buttonSelectTime: Button? = null
    var buttonSelectCourse: Button? = null
    var buttonCreateEvent: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        eventNameDisplay = findViewById(R.id.text_event_name_display_create)
        eventDescriptionDisplay  = findViewById(R.id.text_event_description_display_create)

        val editTextEventName : EditText = findViewById(R.id.editText_event_name_create)
        val editTextEventDescription: EditText = findViewById(R.id.editText_event_description_create)


        editTextEventName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                eventName = editTextEventName.text.toString()
                updateEventDisplay()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        editTextEventDescription.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                eventDescription = editTextEventDescription.text.toString()
                updateEventDisplay()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        buttonUpdatePhoto = findViewById<Button>(R.id.button_event_image)
        buttonSelectDate = findViewById<Button>(R.id.button_event_date)
        buttonSelectTime = findViewById(R.id.button_event_time)
        buttonSelectCourse = findViewById<Button>(R.id.button_event_course)
        buttonCreateEvent = findViewById<Button>(R.id.button_event_create)

        buttonUpdatePhoto?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

            }
        })

        buttonSelectDate?.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(p0: View?) {

                val datePickerDialog = DatePickerDialog(this@CreateEventActivity,DatePickerDialog.OnDateSetListener{ view: DatePicker?, Tyear: Int, Tmonth: Int, TdayOfMonth: Int ->
                    Log.e(TAG,"$Tyear,$Tmonth,$TdayOfMonth")
                    eventDate = "$Tyear-$Tmonth-$TdayOfMonth"
                }, year, month, day)
                datePickerDialog.show()
            }
        })

        buttonSelectTime?.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(p0: View?) {

                val timePickerDialog = TimePickerDialog(this@CreateEventActivity,TimePickerDialog.OnTimeSetListener{ view: TimePicker?, Thour: Int, Tminute: Int ->
                    Log.e(TAG,"$Thour,$Tminute")
                    eventTime = "$Thour:$Tminute:00"
                }, hour, minute, false)
                timePickerDialog.show()
            }
        })

        buttonSelectCourse?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                getCourses()

            }
        })

        buttonCreateEvent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                createEvent()
            }
        })

    }


    fun getCourses() : List<Course> {

        val courses = mutableListOf<Course>()

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
                            courses.add(course)
                        }

                        val courseNames = mutableListOf<String>()

                        for (course in courses){
                            courseNames.add(course.courseName)
                        }

                        val options : Array<CharSequence> = courseNames.toTypedArray()

                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@CreateEventActivity)
                        builder.setTitle("Choose Control Photo")
                        builder.setItems(
                            options,
                            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, item: Int ->

                                lateinit var selectedCourse : Course

                                for (course in courses){
                                    if (options[item] == course.courseName){
                                        selectedCourse = course
                                    }
                                }
                                eventcourse=selectedCourse
                            })
                        builder.show()

                    }
                }
            })

        return courses
    }

    fun updateEventDisplay(){
        eventNameDisplay.text=eventName
        eventDescriptionDisplay.text=eventDescription
    }

    fun createEvent(){
        val eventDateString = "$eventDate $eventTime"
        Log.e(TAG,eventDateString)
        if (eventName!=null && eventDescription!=null){
            val event = Event(eventName.toString(),eventcourse,eventDateString,"2020-06-19 14:27:28",eventDescription.toString())
            event.eventOrganiser=GeofencingConstants.userTest
            event.eventOrganiser.userId=3
            event.eventStatus=1 as Integer

            EventCreator.uploadEvent(event)
        }

    }

}
