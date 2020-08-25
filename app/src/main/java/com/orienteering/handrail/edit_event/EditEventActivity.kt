package com.orienteering.handrail.edit_event

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.event.EventActivity

import com.orienteering.handrail.events.EventsActivity
import com.orienteering.handrail.image_utilities.ImageSelect
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.permissions.PermissionManager
import kotlinx.android.synthetic.main.activity_create_event.*
import java.util.*

// TAG for Logs
private val TAG: String = EditEventActivity::class.java.getName()

/**
 * View for edit event use case, displays event information and binds user input ofr upadted information
 *
 */
class EditEventActivity : AppCompatActivity(), IEditEventContract.IEditEventView {

    // presenter to handle edit event logic
    lateinit var editEventPresenter : IEditEventContract.IEditEventPresenter

    // event name for display and creation
    var eventName : String = ""
    // event discritpion for display and creation
    var eventDescription : String = ""
    // event date for creation
    var eventDate : String = ""
    // event time for creation
    var eventTime : String = ""
    // check event change date
    var eventDateChange : Boolean = false

    // calendar for date and time values to correctly display and modify for creation
    val calendar: Calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    lateinit var imageSelect : ImageSelect

    // Image uri for image selection and display
    var imageUri: Uri? = null

    // image view for event image
    private lateinit var eventImageView : ImageView
    // edit text for name
    lateinit var editTextEventName : EditText
    // edit text for description
    lateinit var editTextEventDescription : EditText
    //text view for date
    lateinit var textViewEventDate : TextView
    //text view for time
    lateinit var textViewEventTime : TextView
    //text view for course
    private lateinit var textViewEventCourse : TextView

    // button to select photo for event
    private var buttonUpdatePhoto: Button? = null
    // button to select date for event
    private var buttonSelectDate: Button? = null
    // button to select time for event
    private var buttonSelectTime: Button? = null
    //button to select course for event
    private var buttonSelectCourse: Button? = null
    // button to create event
    private var buttonCreateEvent: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageSelect = ImageSelect(this, this@EditEventActivity)
        if (intent.extras!=null){
            this.editEventPresenter = EditEventPresenter(intent.getSerializableExtra("EVENT_ID") as Int,this,imageSelect, EventInteractor())
            editEventPresenter.getDataFromServer()
        } else {
            val intent = Intent(this@EditEventActivity, EventsActivity::class.java).apply {}
            startActivity(intent)
        }
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
        buttonSelectCourse?.visibility=View.INVISIBLE
        buttonCreateEvent = findViewById<Button>(R.id.button_event_create)

        buttonUpdatePhoto?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (PermissionManager.checkPermission(this@EditEventActivity, this@EditEventActivity, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionManager.MULTIPLE_REQUEST_CODES)) {
                    imageUri=imageSelect.selectImage()
                }
            }
        })

        buttonSelectDate?.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(p0: View?) {
                // creates select date dialog, binds output to date values
                val datePickerDialog = DatePickerDialog(this@EditEventActivity, DatePickerDialog.OnDateSetListener{ view: DatePicker?, Tyear: Int, Tmonth: Int, TdayOfMonth: Int ->
                        eventDateChange = true
                        var yearString : String = Tyear.toString()
                        var monthString : String = Tmonth.toString()
                        var dayString : String = TdayOfMonth.toString()
                        // add leading 0s if month and day are < 10
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
        // creates select time dialog, binds output to time values
        buttonSelectTime?.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(p0: View?) {

                val timePickerDialog = TimePickerDialog(this@EditEventActivity,
                    TimePickerDialog.OnTimeSetListener{ view: TimePicker?, Thour: Int, Tminute: Int ->
                        eventDateChange = true
                        var hourString : String = Thour.toString()
                        var minuteString : String = Tminute.toString()
                        // add leading 0s if time is less than 10am or 10 minutes
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
        // check all fields, and request update of event information via presenter
        buttonCreateEvent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (checkFields()) {
                    // refactor date string to acceptable value format
                    val eventDateString = "$eventDate"+"T"+"$eventTime"
                    if (eventName != null && eventDescription != null) {
                        if (eventDateChange){
                            editEventPresenter.putDataOnServer(eventName,eventDescription,eventDateString)
                        } else {
                            editEventPresenter.putDataOnServer(eventName,eventDescription,null)
                        }

                    }
                } else {
                    Toast.makeText(this@EditEventActivity, "Please check all fields", Toast.LENGTH_SHORT).show()
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
     * Check user input fields for createEvent return boolean
     * @return
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

    override fun onGetResponseFailure(throwable: Throwable) {
        Log.e(TAG, "Failure connecting to service")
        Toast.makeText(this@EditEventActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT).show()
    }

    override fun onGetResponseError() {
        Log.e(TAG, "Error event empty")
        Toast.makeText(this@EditEventActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT).show()
    }


    override fun onUpdateResponseFailure(throwable: Throwable) {
        Log.e(TAG, "Failure connecting to service")
        Toast.makeText(this@EditEventActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateResponseError() {
        Log.e(TAG, "Error event empty")
        Toast.makeText(this@EditEventActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT).show()
    }

    /**
     * start intent view event on success, finish current activity
     *
     * @param eventId
     */
    override fun onUpdatePartialResponseError(eventId: Int) {
        Log.e(TAG, "Partial success updating Event")
        Toast.makeText(this@EditEventActivity,"Error: Partial Success updating event, please reupload event image.",Toast.LENGTH_SHORT).show()
        val intent : Intent = Intent(this@EditEventActivity, EventActivity::class.java)
        intent.putExtra("EVENT_ID", eventId)
        startActivity(intent)
        finish()
    }
    /**
     * start intent view event on success, finish current activity
     *
     * @param eventId
     */
    override fun onUpdateResponseSuccess(eventId: Int) {
        Log.e(TAG, "Success updating Event")
        Toast.makeText(this@EditEventActivity,"Success updating event.",Toast.LENGTH_SHORT).show()
        val intent : Intent = Intent(this@EditEventActivity, EventActivity::class.java)
        intent.putExtra("EVENT_ID", eventId)
        startActivity(intent)
        finish()
    }

    /**
     * set input field information to retrieved event information
     *
     * @param eventName
     * @param eventNote
     * @param eventTime
     * @param eventDate
     * @param courseName
     */
    override fun fillInformation(eventName: String, eventNote: String, eventTime: String, eventDate: String, courseName : String) {
        this.eventName=eventName
        this.eventDescription=eventNote
        this.eventTime=eventTime
        this.eventDate=eventDate
        this.editTextEventName.setText(eventName)
        this.editTextEventDescription.setText(eventNote)
        this.textViewEventTime.setText(eventTime)
        this.textViewEventDate.setText(eventDate)
        this.textViewEventCourse.setText(courseName)
    }


    /**
     * set image display to select uri from device
     *
     * @param imageUri
     */
    override fun setupImage(imageUri : Uri) {
        val options: RequestOptions = RequestOptions().centerCrop().placeholder(R.drawable.ic_action_event).error(R.drawable.ic_action_event)
        Glide.with(this).asBitmap().load(imageUri).apply(options).into(eventImageView)
    }

    /**
     * set image display to select imagepath from retrieves event image
     *
     * @param imagepath
     */
    override fun setupImage(imagepath: String) {
        val options: RequestOptions = RequestOptions().centerCrop().placeholder(R.drawable.ic_action_event).error(R.drawable.ic_action_event)
        Glide.with(this).asBitmap().load(imagepath).apply(options).into(eventImageView)
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
                    // camera intent - bind uri from selected data after check
                    if (resultCode == Activity.RESULT_OK) {
                        editEventPresenter.setImage(imageSelect.tempImageUri)
                        setupImage(imageSelect.tempImageUri)
                    } else {
                        Toast.makeText(this@EditEventActivity,"Error: Cannot use image",Toast.LENGTH_SHORT).show()
                    }
                }
                1002 -> {
                    // gallery intent- bind uri from selected data after check
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        data.data?.let { setupImage(it) }
                        data.data?.let { setupImage(it) }
                    } else {
                        Log.e(TAG,"Result: $resultCode  Data: $data")
                        Toast.makeText(this@EditEventActivity,"Error: Cannot use image",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Log.e(TAG, "Request cancelled...")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editEventPresenter.onDestroy()
    }
}