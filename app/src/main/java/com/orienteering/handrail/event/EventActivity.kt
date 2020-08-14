package com.orienteering.handrail.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.activities.CourseParticipationActivity
import com.orienteering.handrail.events.EventsActivity
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.interactors.ParticipantInteractor

/**
 * Event Activity handles view for Event.
 * Initiaties event information and controls onclick of each button
 */
class EventActivity : AppCompatActivity(), IEventContract.IEventView {

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

    /**
     * Logic presenter
     */
    private lateinit var eventPerformer : IEventContract.IEventPerformer

    // Id passed via intent
    override var eventId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        if (intent.extras!=null){
            this.eventId = intent.getSerializableExtra("EVENT_ID") as Int
        } else {
            startViewEventsActivity()
        }

        this.eventPerformer = EventPerformer(this.eventId,this, EventInteractor(), ParticipantInteractor())

        intialiseTextView()
        createButtons()
        createImages()

        eventPerformer.requestDataFromServer()
    }

    /**
     * function to create buttons from view and add on click listeners
     */
    override fun createButtons() {
        buttonDelete = findViewById(R.id.button_delete_event_view_event)
        buttonAction = findViewById(R.id.button_action_view_event)
        buttonDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                eventPerformer.deleteEvent()
            }
        })
        buttonAction.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                eventAction()
            }
        })
    }

    override fun createImages(){
        eventImageImageView = findViewById(R.id.imageview_event_photo_view_event)
    }

    /**
     * initialise text view variables
     */
    override fun intialiseTextView() {
        eventNameTextView = findViewById(R.id.textView_event_name_view_event)
        eventNoteTextView = findViewById(R.id.textView_event_note_view_event)
        eventDateTextView = findViewById(R.id.textView_event_date_event_view)
        eventTimeTextView = findViewById(R.id.textView_event_time_event_view)
    }

    /**
     * Setup view buttons for event user
     *
     */
    override fun setupForUser(eventStatus : Integer, userIsParticipant : Boolean) {

        if (eventStatus == Integer(1) && !userIsParticipant) {
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.tag = 4
            buttonAction.text = "Join Event"
        } else if (eventStatus == Integer(1)) {
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.tag = 5
            buttonAction.text = "Leave Event"
        } else if (eventStatus == Integer(2) && userIsParticipant){
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.tag = 6
            buttonAction.text = "Start Participation"
        } else if (eventStatus == Integer(2)){
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.visibility = View.INVISIBLE
            buttonAction.tag = 7
        } else if (eventStatus==Integer(3)){
            buttonDelete.visibility = View.INVISIBLE
            buttonAction.tag = 3
            buttonAction.text = "View Results"
        }
    }

    /**
     * Setup view buttons for organiser to manage event status
     *
     */
    override fun setupForOrganizer(eventStatus : Integer) {
        when (eventStatus) {
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

    override fun fillInformation(name : String, note : String, date : String, time : String) {
        eventNameTextView.text = name
        eventNoteTextView.text = note
        eventDateTextView.text = date
        eventTimeTextView.text = time
    }

    /**
     * Setup event image
     */
    override fun setupImage(imageUrl : String){
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .apply(options)
            .into(eventImageImageView)
    }

    override fun eventAction() {
        when (buttonAction.tag) {
            1 , 2 -> {
                eventPerformer.updateEventStatus()
            }
            3 -> {
                eventPerformer.showResults()
            }
            4 -> {
                eventPerformer.joinEvent()
            }
            5 -> {
                eventPerformer.leaveEvent()
            }
            6 ->{
                eventPerformer.startEvent()
            }
            else -> {
                val toast = Toast.makeText(this@EventActivity, "Error: Problem with Handrail, please contact an admin", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    override fun makeToast(message: String) {
        val toast : Toast = Toast.makeText(this@EventActivity,message,Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseFailure(throwable: Throwable) {
        val toast = Toast.makeText(this@EventActivity,"Error: Connectivity Error, unable to process request ", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseError() {
        val toast = Toast.makeText(this@EventActivity,"Error: Please contact an administrator",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun startCourseParticipationActivity(eventId : Int) {
        val intent = Intent(this@EventActivity, CourseParticipationActivity::class.java).apply {}
        intent.putExtra("EVENT_ID", eventId)
        startActivity(intent)
    }

    override fun startViewEventsActivity(){
        val intent = Intent(this@EventActivity, EventsActivity::class.java).apply {}
        startActivity(intent)
    }

    override fun startEventResultsActivity(eventId : Int){
        val intent = Intent(this@EventActivity, CourseParticipationActivity::class.java).apply {}
        intent.putExtra("EVENT_ID", eventId)
        startActivity(intent)
    }

    override fun onDestroy() {
        eventPerformer.onDestroy()
        super.onDestroy()
    }

}
