package com.orienteering.handrail.create_event

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.activities.ViewEventActivity
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.utilities.ImageSelect
import com.orienteering.handrail.utilities.PermissionManager
import kotlinx.android.synthetic.main.activity_create_event.*
import java.util.*

class CreateEventActivity : AppCompatActivity(), ICreateEventContract.ICreateEventView {

    // Tag for class log
    val TAG : String = "CreateEventActivity"

    lateinit var createEventPerformer : ICreateEventContract.ICreateEventPerformer

    // event name for display and creation
    var eventName : String = ""
    // event discritpion for display and creation
    var eventDescription : String = ""
    // event date for creation
    var eventDate : String = ""
    // event time for creation
    var eventTime : String = ""
    // event course for creation
    lateinit var eventcourse : Course

    // calendar for date and time values to correctly display and modify for creation
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    lateinit var imageSelect : ImageSelect

    // Image uri for image selection and display
    var imageUri: Uri? = null

    // image view for event image
    lateinit var eventImageView : ImageView
    // edit text for name
    lateinit var editTextEventName : EditText
    // edit text for description
    lateinit var editTextEventDescription : EditText
    //text view for date
    lateinit var textViewEventDate : TextView
    //text view for time
    lateinit var textViewEventTime : TextView
    //text view for course
    lateinit var textViewEventCourse : TextView

    // button to select photo for event
    var buttonUpdatePhoto: Button? = null
    // button to select date for event
    var buttonSelectDate: Button? = null
    // button to select time for event
    var buttonSelectTime: Button? = null
    //button to select course for event
    var buttonSelectCourse: Button? = null
    // button to create event
    var buttonCreateEvent: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageSelect = ImageSelect(this,this@CreateEventActivity)
        this.createEventPerformer = CreateEventPerformer(this,imageSelect, EventInteractor(), CourseInteractor())
        setContentView(R.layout.activity_create_event)
        initiateText()
        createButtons()
    }

    /**
     * Function to create buttons and their uses
     */
    fun createButtons(){
        buttonUpdatePhoto = findViewById<Button>(R.id.button_event_image)
        buttonSelectDate = findViewById<Button>(R.id.button_event_date)
        buttonSelectTime = findViewById(R.id.button_event_time)
        buttonSelectCourse = findViewById<Button>(R.id.button_event_course)
        buttonCreateEvent = findViewById<Button>(R.id.button_event_create)

        buttonUpdatePhoto?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (PermissionManager.checkPermission(this@CreateEventActivity, this@CreateEventActivity, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionManager.MULTIPLE_REQUEST_CODES)) {
                    imageUri=imageSelect.selectImage()
                }
            }
        })

        buttonSelectDate?.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(p0: View?) {

                val datePickerDialog = DatePickerDialog(this@CreateEventActivity,
                    DatePickerDialog.OnDateSetListener{ view: DatePicker?, Tyear: Int, Tmonth: Int, TdayOfMonth: Int ->
                        var yearString : String = Tyear.toString()
                        var monthString : String = Tmonth.toString()
                        var dayString : String = TdayOfMonth.toString()

                        if (monthString.length==1){
                            monthString="0"+monthString
                        }
                        if (dayString.length==1){
                            dayString="0"+dayString
                        }
                        eventDate = "$yearString-$monthString-$dayString"
                        textViewEventDate.text = eventDate
                    }, year, month, day)
                datePickerDialog.show()
            }
        })

        buttonSelectTime?.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(p0: View?) {

                val timePickerDialog = TimePickerDialog(this@CreateEventActivity,
                    TimePickerDialog.OnTimeSetListener{ view: TimePicker?, Thour: Int, Tminute: Int ->
                        Log.e(TAG,"$Thour,$Tminute")

                        var hourString : String = Thour.toString()
                        var minuteString : String = Tminute.toString()

                        if (hourString.length==1){
                            hourString="0"+hourString
                        }
                        if (minuteString.length==1){
                            minuteString="0"+minuteString
                        }
                        eventTime = "$hourString:$minuteString"
                        textViewEventTime.text=eventTime
                    }, hour, minute, false)
                timePickerDialog.show()
            }
        })

        buttonSelectCourse?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                createEventPerformer.getDataFromServer()
            }
        })

        buttonCreateEvent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (checkFields()) {
                    val eventDateString = "$eventDate $eventTime"
                    if (eventName != null && eventDescription != null) {
                        val event = Event(eventName, eventcourse, eventDateString, eventDescription)
                        createEventPerformer.postDataOnServer(event)
                    }
                } else {
                    Toast.makeText(this@CreateEventActivity, "Please check all fields", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * function to initiate text displays
     */
    fun initiateText(){
        textViewEventDate = findViewById(R.id.textView_event_date)
        textViewEventTime = findViewById(R.id.textView_event_time)
        textViewEventCourse = findViewById(R.id.textView_event_course)
        eventImageView = findViewById(R.id.imageview_create_event)
        editTextEventName  = findViewById(R.id.editText_event_name_create)
        editTextEventDescription = findViewById(R.id.editText_event_description_create)
        editTextEventName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                eventName = editTextEventName.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editTextEventDescription.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                eventDescription = editTextEventDescription.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /**
     * Check user input fields for createEvent
     *
     */
    fun checkFields() : Boolean{
        var inputsOk = true
        if(eventName?.trim()?.isEmpty()!!){
            editText_event_name_create.error = "Enter an Event name"
            inputsOk = false
        }
        if(eventDescription?.trim()?.isEmpty()!!){
            editText_event_description_create.error = "Enter an Event description"
            inputsOk = false
        }
        if(eventDate.trim().isEmpty()){
            textViewEventDate.error = "Please enter a valid date"
            inputsOk = false
        }
        if (eventTime.trim().isEmpty()){
            textViewEventTime.error = "Please enter a valid time"
            inputsOk = false
        }
        if (textViewEventCourse.text.trim().isEmpty()){
            buttonSelectCourse?.error = "Please select a course"
            inputsOk = false
        }
        return inputsOk
    }

    override fun setupImage(imageUri : Uri) {
        val options: RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        Glide.with(this).asBitmap().load(imageUri).apply(options).into(eventImageView)
    }

    override fun onImageResponseError() {
        Log.e(TAG, "No Image Selected")
        Toast.makeText(this@CreateEventActivity,"Error: Please select an event image",Toast.LENGTH_SHORT).show()
    }

    override fun onResponseFailure() {
        Log.e(TAG, "Failure connecting successfully")
        Toast.makeText(this@CreateEventActivity,"Error: Connection Failure, please try again later",Toast.LENGTH_SHORT).show()
    }

    override fun onResponseError() {
        Log.e(TAG, "Error with response")
        Toast.makeText(this@CreateEventActivity,"Error: If problem persists, please contact admin.",Toast.LENGTH_SHORT).show()
    }

    override fun onPostResponseSuccess(eventId : Int) {
        Log.e(TAG, "Success adding Event")
        Toast.makeText(this@CreateEventActivity,"Success creating event.",Toast.LENGTH_SHORT).show()
        val intent : Intent = Intent(this@CreateEventActivity, ViewEventActivity::class.java)
        intent.putExtra("EVENT_ID", eventId)
        startActivity(intent)
    }

    override fun onGetResponseSuccess(courses: List<Course>) {
        val courseNames = mutableListOf<String>()
        for (course in courses){
            courseNames.add(course.courseName)
        }
        val options : Array<CharSequence> = courseNames.toTypedArray()
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@CreateEventActivity)
        builder.setTitle("Choose Course")
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
                textViewEventCourse.text=eventcourse.courseName
            })
        builder.show()
    }

    /**
     * On Activity Result for image select
     * Displays image view of event
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1001 -> {
                    if (resultCode == Activity.RESULT_OK) {
                        createEventPerformer.setImage(imageSelect.tempImageUri)
                        setupImage(imageSelect.tempImageUri)
                    } else {
                        Log.e(TAG,"Result: $resultCode  Data: $data")
                        Toast.makeText(this@CreateEventActivity,"Error: Cannot use image",Toast.LENGTH_SHORT).show()
                    }
                }
                1002 -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        data.data?.let { createEventPerformer.setImage(it) }
                        data.data?.let { setupImage(it) }
                    } else {
                        Log.e(TAG,"Result: $resultCode  Data: $data")
                        Toast.makeText(this@CreateEventActivity,"Error: Cannot use image",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Log.e(TAG, "Request cancelled...")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        createEventPerformer.onDestroy()
    }
}
