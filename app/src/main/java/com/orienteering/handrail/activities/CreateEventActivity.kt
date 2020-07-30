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
import com.orienteering.handrail.utilities.ImageSelect
import com.orienteering.handrail.utilities.PermissionManager
import kotlinx.android.synthetic.main.activity_create_event.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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
    var eventName : String? = null
    // event discritpion for display and creation
    var eventDescription : String? = null
    // event date for creation
    lateinit var eventDate : String
    // event time for creation
    lateinit var eventTime : String
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

    // textview event name
    lateinit var eventNameDisplay : TextView
    // textview event description
    lateinit var eventDescriptionDisplay : TextView
    // image view for event image
    lateinit var eventImageView : ImageView

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

    /**
     * function to initiate text displays
     */
    fun initiateText(){
        eventImageView = findViewById(R.id.imageview_create_event)
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
    }

    /**
     * function to retrieve courses
     */
    fun getCourses(){
        courseController.retrieveAllByUser(3, getCoursesCallback)
    }

    /**
     * function to update event display when text altered by user
     */
    fun updateEventDisplay(){
        eventNameDisplay.text=eventName
        eventDescriptionDisplay.text=eventDescription
    }

    /**
     * function to call service to upload event
     */
    fun createEvent(){

        val imageMultipartBodyPart : MultipartBody.Part? = image_uri?.let { createImageMultipartBody(it) }



        val eventDateString = "$eventDate $eventTime"
        if (eventName!=null && eventDescription!=null){
            val event = Event(eventName.toString(),eventcourse,eventDateString,"2020-06-19 14:27:28",eventDescription.toString())
            event.eventStatus=1 as Integer
            if (imageMultipartBodyPart != null) {
                eventController.create(event,imageMultipartBodyPart )
            } else {
                Log.e(TAG,"Imagemultipart body null")
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
