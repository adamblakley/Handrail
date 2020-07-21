package com.orienteering.handrail.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.classes.Participant
import com.orienteering.handrail.controllers.EventController
import com.orienteering.handrail.services.ParticipantService
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.GeofencingConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

private const val TAG : String = "ViewEventActivity"

/**
 * class to view selected event, join and participate in event, view results of event if completed
 */
class ViewEventActivity : AppCompatActivity() {

    // textview for event name
    lateinit var eventNameTextView : TextView
    // text view for event note
    lateinit var eventNoteTextView : TextView
    // text view for event date
    lateinit var eventDateTextView: TextView
    // text view for event time
    lateinit var eventTimeTextView: TextView
    // button to join event
    lateinit var joinEventButton: Button
    // button to participate in event
    lateinit var startEventCourse : Button
    // button to view results
    lateinit var viewEventResults : Button

    // event for information display and use in http calls to update participants list
    lateinit var event : Event

    //event id passed from intent
    var eventIdPassed : Int? = null

    val eventController : EventController = EventController()

    // handle event callback, success/failure of retrieval of getEvent
    private val getEventCallback = object : Callback<Event> {
        override fun onFailure(call: Call<Event>, t: Throwable) {
            Log.e(TAG, "Failure getting event")
        }
        override fun onResponse(call: Call<Event>, response: Response<Event>) {
            Log.e(TAG, "Success getting event")
            val eventgot: Event? = response.body()
            if (eventgot != null) {
                event = eventgot
                fillEventInformation()
                if (event.eventStatus.equals(2)){
                    startEventCourse.visibility=View.INVISIBLE
                    joinEventButton.visibility=View.INVISIBLE
                    viewEventResults.visibility=View.VISIBLE
                } else{
                    for (participant in event.participants){
                        Log.e(TAG,"${participant.toString()}")
                        if (participant.participantUser.userId?.equals(3)!!){
                            joinEventButton.visibility = View.INVISIBLE
                            viewEventResults.visibility = View.INVISIBLE
                            startEventCourse.visibility = View.VISIBLE
                            break
                        }
                    }
                }
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

        this.eventIdPassed =  intent.getSerializableExtra("EVENT_ID") as Int
        getEvent()

    }

    /**
     * function to create buttons from view and add on click listeners
     */
    private fun createButtons(){
        joinEventButton = findViewById(R.id.button_join_event_event_view)
        startEventCourse = findViewById(R.id.button_start_course_view_event)
        viewEventResults = findViewById(R.id.button_view_results)

        startEventCourse.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intentParticipate = Intent(this@ViewEventActivity, CourseParticipationActivity::class.java).apply {}
                intentParticipate.putExtra("EVENT_ID", event.eventId)
                startActivity(intentParticipate)
            }
        })

        joinEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                joinEvent()
            }
        })

        viewEventResults.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val intentResults = Intent(this@ViewEventActivity,ResultsListActivity::class.java).apply {  }
                intentResults.putExtra("EVENT_ID",event.eventId)
                startActivity(intentResults)
            }
        })
        viewEventResults.visibility=View.INVISIBLE
        startEventCourse.visibility =View.INVISIBLE
        joinEventButton.visibility=View.INVISIBLE
    }

    /**
     * initialise text view variables
     */
    private fun intialiseTextView(){
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
        participant.participantUser.userId=3
        ServiceFactory.makeService(ParticipantService::class.java).create(eventIdPassed, participant)
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
                    joinEventButton.visibility = View.INVISIBLE
                    startEventCourse.visibility =View.VISIBLE
                    Log.e(TAG, "Success creating participant")
                }

            })
    }

    /**
     * get event from intent extra event id
     */
    fun getEvent(){
        if (intent.extras!=null){
            eventIdPassed?.let { eventController.retreiveByID(it,getEventCallback) }
        }
    }

    /**
     * fill event information on screen from event retrieved for getEvent
     */
    fun fillEventInformation(){
        eventNameTextView.text = event.eventName
        eventNoteTextView.text = event.eventNote

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

        val dateformatted = sdf.parse(event.eventDate)
        Log.e(TAG,"$dateformatted")
        val dateFormatter = SimpleDateFormat("dd-MM-YYYY")
        val timeFormatter = SimpleDateFormat("HH:mm")

        val date : String = dateFormatter.format(dateformatted)
        Log.e(TAG,"$date")
        val time : String = timeFormatter.format(dateformatted)

        eventDateTextView.text = date
        eventTimeTextView.text = time
    }
}
