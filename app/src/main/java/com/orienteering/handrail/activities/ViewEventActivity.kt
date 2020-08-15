package com.orienteering.handrail.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.controllers.EventController
import com.orienteering.handrail.controllers.ParticipantController
import com.orienteering.handrail.home_menu.HomeActivity
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

private const val TAG: String = "ViewEventActivity"

/**
 * class to view selected event, join and participate in event, view results of event if completed
 */
class ViewEventActivity : AppCompatActivity() {

    // image view for event image
    lateinit var eventImageImageView : ImageView

    // textview for event name
    lateinit var eventNameTextView: TextView

    // text view for event note
    lateinit var eventNoteTextView: TextView

    // text view for event date
    lateinit var eventDateTextView: TextView

    // text view for event time
    lateinit var eventTimeTextView: TextView

    // button to action event
    lateinit var buttonAction: Button

    // button to delete event
    lateinit var buttonDelete: Button

    // event for information display and use in http calls to update participants list
    lateinit var event: Event

    //event id passed from intent
    var eventIdPassed: Int? = null

    // control event services
    val eventController: EventController = EventController()

    // control participant services
    val participantController : ParticipantController = ParticipantController()

    // handle event callback, success/failure of retrieval of getEvent
    private val getEventCallback = object : Callback<StatusResponseEntity<Event>> {
        override fun onFailure(call: Call<StatusResponseEntity<Event>>, t: Throwable) {
            Log.e(TAG, "Failure getting event")
            val toast = Toast.makeText(
                this@ViewEventActivity,
                "Failure getting event, service currently unavailable.",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }

        override fun onResponse(call: Call<StatusResponseEntity<Event>>, response: Response<StatusResponseEntity<Event>>) {
            if (response.isSuccessful) {
                Log.e(TAG, "Success getting event")
                if (response.body() != null) {
                    event = response.body()!!.entity!!
                    fillEventInformation()
                    if (event.eventOrganiser.userId == App.sharedPreferences.getLong(
                            App.SharedPreferencesUserId,
                            0
                        )
                    ) {
                        setupForOrganiser()
                    } else {
                        setupForUser()
                    }
                } else {
                    Log.e(TAG, "Problem getting events")
                    val toast = Toast.makeText(
                        this@ViewEventActivity,
                        "Error getting event please try again later.",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            } else {
                Log.e(TAG, "Problem getting events")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Error getting event please try again later.",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }

    // callback for participant creation
    private val createParticipantCallback = object : Callback<StatusResponseEntity<Event>> {
        override fun onFailure(
            call: Call<StatusResponseEntity<Event>>,
            t: Throwable
        ) {
            Log.e(TAG, "Failure connecting to create participant service")
            val toast = Toast.makeText(
                this@ViewEventActivity,
                "Failure joining event, service currently unavailable.",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<Event>>,
            response: Response<StatusResponseEntity<Event>>
        ) {

            if (response.isSuccessful){
                event = response.body()?.entity!!
                setupForUser()
                Log.e(TAG, "Success creating participant")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Success joining event",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            } else {
                Log.e(TAG, "Failure creating participant")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Failure joining event, please contact an admin if problem persists",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }

    // callback for remove participant
    val leaveEventCallback = object : Callback<StatusResponseEntity<Event>> {
        override fun onFailure(
            call: Call<StatusResponseEntity<Event>>,
            t: Throwable
        ) {
            Log.e(TAG, "Failure connecting to remove participant service")
            val toast = Toast.makeText(
                this@ViewEventActivity,
                "Failure removing event, service currently unavailable.",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<Event>>,
            response: Response<StatusResponseEntity<Event>>
        ) {

            if (response.isSuccessful){
                event = response.body()?.entity!!
                setupForUser()
                Log.e(TAG, "Success removing participant")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Success leaving event",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            } else {
                Log.e(TAG, "Failure removing participant")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Failure removing event, please contact an admin if problem persists",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }

    // callback for update event
    val updateEventStatusCallback = object : Callback<StatusResponseEntity<Event>> {
        override fun onFailure(
            call: Call<StatusResponseEntity<Event>>,
            t: Throwable
        ) {
            Log.e(TAG, "Failure connecting to update event service")
            val toast = Toast.makeText(
                this@ViewEventActivity,
                "Failure updating event, service currently unavailable.",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<Event>>,
            response: Response<StatusResponseEntity<Event>>
        ) {
            if (response.isSuccessful){
                event = response.body()?.entity!!
                setupForOrganiser()
                Log.e(TAG, "Success updating event")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Event Status updated",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            } else {
                Log.e(TAG, "Failure removing participant")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Event Status cannot be updated, if problem persists - please contact admin",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }

    // callback for remove participant
    val deleteEventCallback = object : Callback<StatusResponseEntity<Boolean>> {
        override fun onFailure(
            call: Call<StatusResponseEntity<Boolean>>,
            t: Throwable
        ) {
            Log.e(TAG, "Failure connecting to delete event service")
            val toast = Toast.makeText(
                this@ViewEventActivity,
                "Failure removing event, service currently unavailable.",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<Boolean>>,
            response: Response<StatusResponseEntity<Boolean>>
        ) {
            if (response.isSuccessful || response.equals(409)){
                Log.e(TAG, "Success removing event")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Event Deleted",
                    Toast.LENGTH_SHORT
                )
                toast.show()
                val intent = Intent(this@ViewEventActivity, HomeActivity::class.java).apply { }
                startActivity(intent)
            }   else {
                Log.e(TAG, "Failure removing event")
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Event cannot be deleted, if problem persists - please contact admin",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }


    /**
     * initialise buttons and text variables, collect intent extra
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        createButtons()
        createImages()
        intialiseTextView()

        this.eventIdPassed = intent.getSerializableExtra("EVENT_ID") as Int
        getEvent()
    }

    /**
     * function to create buttons from view and add on click listeners
     */
    private fun createButtons() {
        buttonDelete = findViewById(R.id.button_delete_event_view_event)
        buttonAction = findViewById(R.id.button_action_view_event)
        buttonDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                deleteEvent()
            }
        })
        buttonAction.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                eventAction()
            }
        })
    }

    private fun createImages(){
        eventImageImageView = findViewById(R.id.imageview_event_photo_view_event)
    }

    /**
     * initialise text view variables
     */
    private fun intialiseTextView() {
        eventNameTextView = findViewById(R.id.textView_event_name_view_event)
        eventNoteTextView = findViewById(R.id.textView_event_note_view_event)
        eventDateTextView = findViewById(R.id.textView_event_date_event_view)
        eventTimeTextView = findViewById(R.id.textView_event_time_event_view)
    }

    /**
     * function to call for update to event participants list, user joins event
     */
    private fun joinEvent() {
        eventIdPassed?.let { participantController.createParticipant(it, App.sharedPreferences.getLong(App.SharedPreferencesUserId,0), createParticipantCallback) }
    }

    /**
     * function to call for update to event participants list, user leaves event
     */
    private fun leaveEvent(){
        eventIdPassed?.let { participantController.removeParticipant(it,App.sharedPreferences.getLong(App.SharedPreferencesUserId,0),leaveEventCallback) }
    }

    /**
     * function to call for update to event status
     */
    private fun updateEvent(){
        eventIdPassed?.let { eventController.updateStatus(it,updateEventStatusCallback) }
    }

    /**
     * function to call for delete of event
     */
    private fun deleteEvent(){
        eventIdPassed?.let{eventController.deleteEvent(it,deleteEventCallback)}
    }

    /**
     * get event from intent extra event id
     */
    fun getEvent() {
        if (intent.extras != null) {
            eventIdPassed?.let { eventController.retreiveByID(it, getEventCallback) }
        }
    }

    /**
     * fill event information on screen from event retrieved for getEvent
     */
    fun fillEventInformation() {
        setupImage()

        eventNameTextView.text = event.eventName
        eventNoteTextView.text = event.eventNote

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

        val dateformatted = sdf.parse(event.eventDate)

        val dateFormatter = SimpleDateFormat("dd-MM-YYYY")
        val timeFormatter = SimpleDateFormat("HH:mm")

        val date: String = dateFormatter.format(dateformatted)

        val time: String = timeFormatter.format(dateformatted)

        eventDateTextView.text = date
        eventTimeTextView.text = time
    }

    /**
     * Setup event image
     */
    private fun setupImage(){
        val options : RequestOptions  = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)

        for (photo in event.eventPhotographs){
            if (photo.active!!){
                Glide.with(this)
                    .asBitmap()
                    .load(photo.photoPath)
                    .apply(options)
                    .into(eventImageImageView)
            }
        }
    }


    /**
     * Handles Action button depending on user/organiser involvement
     *
     */
    fun eventAction() {
        when (buttonAction.tag) {
            1 -> {
                updateEvent()
            }
            2 -> {
                updateEvent()
            }
            3 -> {
                val intentResults = Intent(this@ViewEventActivity, ResultsListActivity::class.java).apply { }
                intentResults.putExtra("EVENT_ID", event.eventId)
                startActivity(intentResults)
            }
            4 -> {
                joinEvent()
            }
            5 -> {
                leaveEvent()
            }
            6 ->{
                startEvent()
            }
            else -> {
                val toast = Toast.makeText(
                    this@ViewEventActivity,
                    "Problem with Handrail, please contact an admin",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }

    /**
     * Start Event Participation
     */
    fun startEvent(){
        val intent = Intent(this@ViewEventActivity, CourseParticipationActivity::class.java).apply {}
        intent.putExtra("EVENT_ID", event.eventId)
        startActivity(intent)
    }



    /**
     * Setup view buttons for organiser to manage event status
     *
     */
    fun setupForOrganiser() {
        when (event.eventStatus) {
            Integer(1) -> {
                buttonDelete.visibility = View.VISIBLE
                buttonAction.tag = 1
                buttonAction.text = "Start Event"
            }
            Integer(2) -> {
                buttonDelete.visibility = View.INVISIBLE
                buttonAction.tag = 2
                buttonAction.text = "End Event"
            }
            Integer(3) -> {
                buttonDelete.visibility = View.INVISIBLE
                buttonAction.tag = 3
                buttonAction.text = "View Results"
            }
            else -> {
                buttonDelete.visibility = View.INVISIBLE
                buttonAction.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Setup view buttons for event user
     *
     */
    fun setupForUser() {
        var userIsParticipant : Boolean = false
        if (!event.participants.isEmpty()) {
            for (participant in event.participants) {
                if (participant.participantUser.userId?.equals(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0))!! && participant.participantControlPerformances.isEmpty()) {
                    userIsParticipant = true
                }
            }
        }
        if (event.eventStatus == Integer(1) && !userIsParticipant) {
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.tag = 4
            buttonAction.text = "Join Event"
        } else if (event.eventStatus == Integer(1)) {
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.tag = 5
            buttonAction.text = "Leave Event"
        } else if (event.eventStatus == Integer(2) && userIsParticipant){
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.tag = 6
            buttonAction.text = "Start Participation"
        } else if (event.eventStatus == Integer(2)){
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.visibility = View.INVISIBLE
            buttonAction.tag = 7
        } else if (event.eventStatus==Integer(3)){
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.tag = 3
            buttonAction.text = "View Results"
        }
    }
}
