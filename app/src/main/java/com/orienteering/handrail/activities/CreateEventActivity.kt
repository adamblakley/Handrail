package com.orienteering.handrail.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.*
import com.orienteering.handrail.controllers.CourseController
import com.orienteering.handrail.controllers.EventController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.ImageSelect
import com.orienteering.handrail.utilities.PermissionManager
import kotlinx.android.synthetic.main.activity_create_event.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

/**
 * Class that manages creation of event
 */
class CreateEventActivity : AppCompatActivity() {

    // Tag for class log
    val TAG : String = "CreateEventActivity"

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

    val imageSelect : ImageSelect = ImageSelect(this,this)

    // Image capture codes and uri for image selection
    private val IMAGE_CAPTURE_CODE = 1001
    private val PICK_IMAGE_CODE = 1002
    var image_uri: Uri? = null

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

    // controller to manage course services
    val courseController = CourseController()
    // controller to manage event services
    val eventController = EventController()

    // callback create event
    val createEventCallback = object :
        Callback<StatusResponseEntity<Event>> {
        override fun onFailure(call: Call<StatusResponseEntity<Event>?>, t: Throwable) {
            Log.e(TAG, "Failure connecting successfully")
            val toast = Toast.makeText(this@CreateEventActivity,"Connection Failure, please try again later",Toast.LENGTH_SHORT)
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<Event>?>,
            response: Response<StatusResponseEntity<Event>?>
        ) {
            if (response.isSuccessful){
                Log.e(TAG, "Success adding Event")
                val toast = Toast.makeText(this@CreateEventActivity,"Success creating event.",Toast.LENGTH_SHORT)
                toast.show()
                val intent : Intent = Intent(this@CreateEventActivity,ViewEventActivity::class.java)
                intent.putExtra("EVENT_ID", response.body()?.entity?.eventId)
                startActivity(intent)
            } else {
                Log.e(TAG, "Failure adding Event")
                val toast = Toast.makeText(this@CreateEventActivity,"Failure to create event, try again. If problem persists, please contact admin.",Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    // callback to manage course request response
    val getCoursesCallback = object : retrofit2.Callback<List<Course>?> {
        override fun onFailure(call: Call<List<Course>?>, t: Throwable) {
            Log.e(TAG, "Failure getting courses")
        }

        override fun onResponse(
            call: Call<List<Course>?>,
            response: Response<List<Course>?>
        ) {
            Log.e(TAG, "Success getting courses")
            val courses = mutableListOf<Course>()
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
                        textViewEventCourse.text=eventcourse.courseName
                    })
                builder.show()
            }
        }
    }

    /**
     * On create, initiates buttons, views and text
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                if (PermissionManager.checkPermission(
                        this@CreateEventActivity,
                        this@CreateEventActivity,
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        PermissionManager.MULTIPLE_REQUEST_CODES
                    )
                ) {
                    image_uri=imageSelect.selectImage()
                }
            }
        })

        buttonSelectDate?.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(p0: View?) {

                val datePickerDialog = DatePickerDialog(this@CreateEventActivity,DatePickerDialog.OnDateSetListener{ view: DatePicker?, Tyear: Int, Tmonth: Int, TdayOfMonth: Int ->
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

                val timePickerDialog = TimePickerDialog(this@CreateEventActivity,TimePickerDialog.OnTimeSetListener{ view: TimePicker?, Thour: Int, Tminute: Int ->
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
                getCourses()
            }
        })

        buttonCreateEvent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (checkFields()){
                    createEvent()
                } else {
                    val toast : Toast = Toast.makeText(this@CreateEventActivity,"Please check all fields",Toast.LENGTH_SHORT)
                    toast.show()
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
     * function to retrieve courses
     */
    fun getCourses(){
        courseController.retrieveAllByUser(App.sharedPreferences.getLong(App.SharedPreferencesUserId,0), getCoursesCallback)
    }

    /**
     * Check user input fields for createEvent
     *
     */
    fun checkFields() : Boolean{
        var inputsOk = true
        if(eventName?.trim()?.isEmpty()!!){
            editText_event_name_create.setError("Enter an Event name")
            inputsOk = false
        }
        if(eventDescription?.trim()?.isEmpty()!!){
            editText_event_description_create.setError("Enter an Event description")
            inputsOk = false
        }
        if(eventDate.trim().isEmpty()){
            textViewEventDate.setError("Please enter a valid date")
            inputsOk = false
        }
        if (eventTime.trim().isEmpty()){
            textViewEventTime.setError("Please enter a valid time")
            inputsOk = false
        }
        if (textViewEventCourse.text.trim().isEmpty()){
            buttonSelectCourse?.setError("Please select a course")
            inputsOk = false
        }
        if (image_uri==null){
            buttonUpdatePhoto?.setError("Please select a photo")
        }
        return inputsOk
    }

    /**
     * function to call service to upload event
     */
    fun createEvent(){
        val imageMultipartBodyPart : MultipartBody.Part? = image_uri?.let { createImageMultipartBody(it) }
        val eventDateString = "$eventDate $eventTime"
        if (eventName!=null && eventDescription!=null){
            val event = Event(eventName.toString(),eventcourse,eventDateString,eventDescription.toString())
            if (imageMultipartBodyPart != null) {
                eventController.create(App.sharedPreferences.getLong(App.SharedPreferencesUserId,0),event,imageMultipartBodyPart,createEventCallback)
            } else {
                Log.e(TAG,"Imagemultipart body null")
                val toast = Toast.makeText(this,"System Error. Please contact admin.",Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }


    /**
     * Create image upload Request and return
     *
    */
    fun createImageMultipartBody(fileUri : Uri) : MultipartBody.Part{
        val file = File(imageSelect.getImagePath(fileUri))
        val requestBody : RequestBody = RequestBody.create(contentResolver.getType(fileUri)?.let {
            it
                .toMediaTypeOrNull()
        },file)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file",file.name,requestBody)
        return body
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
        Log.e(TAG, "Displaying Photo")
        if (requestCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1001 -> {
                    Log.e(TAG, "Request 1001")
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Log.e(TAG, "Result ok, data not null")
                        val selectedImage: Bitmap = data.extras?.get("data") as Bitmap
                        imageview_create_event.setImageBitmap(selectedImage)
                    } else {
                        Log.e(TAG,"Result: $resultCode  Data: $data")
                    }
                }
                1002 -> {
                    val permission = imageSelect.checkExternalStoragePermission()
                    Log.e("FileWriter","Permission check = $permission")

                    Log.e(TAG, "Request 1002")
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Log.e(TAG,"result ok and data doesn't equal null")
                        val selectedImage: Uri? = data.data
                        image_uri = data.data
                        var filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)
                        if (selectedImage != null) {
                            val cursor: Cursor? = contentResolver.query(
                                selectedImage,
                                filePathColumn,
                                null,
                                null,
                                null
                            )
                            if (cursor != null) {
                                cursor.moveToFirst()

                                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                                val picturePath: String = cursor.getString(columnIndex)
                                imageview_create_event.setImageBitmap(
                                    BitmapFactory.decodeFile(
                                        picturePath
                                    )
                                )
                                cursor.close()
                            }
                        }
                    }
                }
            }
        } else {
            Log.e(TAG, "Request cancelled...")
        }
    }

}
