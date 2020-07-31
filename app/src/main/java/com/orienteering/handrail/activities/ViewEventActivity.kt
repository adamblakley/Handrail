package com.orienteering.handrail.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.classes.Participant
import com.orienteering.handrail.controllers.EventController
import com.orienteering.handrail.services.ParticipantService
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.GeofencingConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

private const val TAG: String = "ViewEventActivity"

/**
 * class to view selected event, join and participate in event, view results of event if completed
 */
class ViewEventActivity : AppCompatActivity() {

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

    val eventController: EventController = EventController()

    // handle event callback, success/failure of retrieval of getEvent
    private val getEventCallback = object : Callback<Event> {
        override fun onFailure(call: Call<Event>, t: Throwable) {
            Log.e(TAG, "Failure getting event")
            val toast = Toast.makeText(
                this@ViewEventActivity,
                "Failure getting event, please contact admin.",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }

        override fun onResponse(call: Call<Event>, response: Response<Event>) {
            if (response.isSuccessful) {
                Log.e(TAG, "Success getting event")
                if (response.body() != null) {
                    event = response.body()!!
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

    /**
     * initialise buttons and text variables, collect intent extra
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        createButtons()
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
                val intentParticipate =
                    Intent(this@ViewEventActivity, CourseParticipationActivity::class.java).apply {}
                intentParticipate.putExtra("EVENT_ID", event.eventId)
                startActivity(intentParticipate)
            }
        })

        buttonAction.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                eventAction()
            }
        })

        buttonDelete.visibility = View.INVISIBLE
        buttonAction.visibility = View.VISIBLE
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
        val participant = Participant(GeofencingConstants.userTest)
        participant.participantUser.userId = 3
        ServiceFactory.makeService(ParticipantService::class.java)
            .create(eventIdPassed, participant)
            .enqueue(object : Callback<StatusResponseEntity<Participant>> {
                override fun onFailure(
                    call: Call<StatusResponseEntity<Participant>>,
                    t: Throwable
                ) {
                    Log.e(TAG, "Failure creating participant")
                }

                override fun onResponse(
                    call: Call<StatusResponseEntity<Participant>>,
                    response: Response<StatusResponseEntity<Participant>>
                ) {
                    buttonAction.visibility = View.INVISIBLE
                    buttonDelete.visibility = View.VISIBLE
                    Log.e(TAG, "Success creating participant")
                }

            })
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
        eventNameTextView.text = event.eventName
        eventNoteTextView.text = event.eventNote

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

        val dateformatted = sdf.parse(event.eventDate)
        Log.e(TAG, "$dateformatted")
        val dateFormatter = SimpleDateFormat("dd-MM-YYYY")
        val timeFormatter = SimpleDateFormat("HH:mm")

        val date: String = dateFormatter.format(dateformatted)
        Log.e(TAG, "$date")
        val time: String = timeFormatter.format(dateformatted)

        eventDateTextView.text = date
        eventTimeTextView.text = time
    }


    /**
     * Handles Action button depending on user/organiser involvement
     *
     */
    fun eventAction() {
        when (buttonAction.tag) {
            1 -> {
            }
            2 -> {
            }
            3 -> {
                val intentResults = Intent(this@ViewEventActivity, ResultsListActivity::class.java).apply { }
                intentResults.putExtra("EVENT_ID", event.eventId)
                startActivity(intentResults)
            }
            4 -> {
            }
            5 -> {
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
                if (participant.participantUser.userId?.equals(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0))!!) {
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
