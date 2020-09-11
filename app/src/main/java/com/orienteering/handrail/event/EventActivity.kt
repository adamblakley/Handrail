package com.orienteering.handrail.event

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import com.orienteering.handrail.course_participation.CourseParticipationActivity
import com.orienteering.handrail.events.EventsActivity
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.interactors.ParticipantInteractor
import com.orienteering.handrail.manage_events.ManageEventsActivity
import com.orienteering.handrail.results.ResultsActivity

/**
 * Event Activity handles view for Event.
 * Initiaties event information and controls onclick of each button
 */
class EventActivity : AppCompatActivity(), IEventContract.IEventView {

    // image view for event image
    private lateinit var eventImageImageView : ImageView
    // textview for event name
    private lateinit var eventNameTextView: TextView
    // text view for event note
    private lateinit var eventNoteTextView: TextView
    // text view for event date
    private lateinit var eventDateTextView: TextView
    // text view for event time
    private lateinit var eventTimeTextView: TextView
    // button to action event
    private lateinit var buttonAction: Button
    // button to delete event
    private lateinit var buttonDelete: Button
    // presenter contains logic for view event activity
    private lateinit var eventPresenter : IEventContract.IEventPresenter
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();

    // Id passed via intent
    override var eventId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        if (intent.extras!=null){
            this.eventId = intent.getSerializableExtra("EVENT_ID") as Int
        } else {
            startViewAllEventsActivity()
        }

        this.eventPresenter = EventPresenter(this.eventId,this, EventInteractor(), ParticipantInteractor())

        intialiseTextView()
        createButtons()
        createImages()
        progressDialog.setMessage("Loading Content...")
        progressDialog.show()
        // request event information from presenter
        eventPresenter.requestDataFromServer()
    }

    /**
     * function to create buttons from view and add on click listeners
     */
    fun createButtons() {
        buttonDelete = findViewById(R.id.button_delete_event_view_event)
        buttonAction = findViewById(R.id.button_action_view_event)
        progressDialog = ProgressDialog(this@EventActivity)
        progressDialog.setCancelable(false)
        buttonDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                progressDialog.setMessage("Deleting Event...")
                progressDialog.show()
                eventPresenter.deleteEvent()
            }
        })
        buttonAction.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                eventAction()
            }
        })
    }

    /**
     * initialise image view
     *
     */
    private fun createImages(){
        eventImageImageView = findViewById(R.id.imageview_event_photo_view_event)
    }

    /**
     * initialise text view variables
     */
    fun intialiseTextView() {
        eventNameTextView = findViewById(R.id.textView_event_name_view_event)
        eventNoteTextView = findViewById(R.id.textView_event_note_view_event)
        eventDateTextView = findViewById(R.id.textView_event_date_event_view)
        eventTimeTextView = findViewById(R.id.textView_event_time_event_view)
    }

    /**
     * Setup view buttons for event user and add tags to determine what action they will incur upon press
     * @param eventStatus
     * @param userIsParticipant
     */
    @SuppressLint("UseValueOf", "SetTextI18n")
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
     * Setup view buttons for organiser to manage event status, determine button action based on tag
     *
     */
    @SuppressLint("UseValueOf", "SetTextI18n")
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

    /**
     * fill textview information from event
     *
     * @param name
     * @param note
     * @param date
     * @param time
     */
    override fun fillInformation(name : String, note : String, date : String, time : String) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
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

    /**
     * when button tag is value, initiate action by presenter
     *
     */
    fun eventAction() {
        when (buttonAction.tag) {
            1 , 2 -> {
                eventPresenter.updateEventStatus()
            }
            3 -> {
                eventPresenter.showResults()
            }
            4 -> {
                eventPresenter.joinEvent()
            }
            5 -> {
                eventPresenter.leaveEvent()
            }
            6 ->{
                eventPresenter.startEvent()
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
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast = Toast.makeText(this@EventActivity,"Error: Connectivity Error, unable to process request ", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast = Toast.makeText(this@EventActivity,"Error: Please contact an administrator",Toast.LENGTH_SHORT)
        toast.show()
    }

    /**
     * start courseparticipationactivity and pass event id as intent extra
     *
     * @param eventId
     */
    override fun startCourseParticipationActivity(eventId : Int) {
        val intent = Intent(this@EventActivity, CourseParticipationActivity::class.java).apply {}
        intent.putExtra("EVENT_ID", eventId)
        startActivity(intent)
    }

    /**
     * start view events activity
     *
     */
    override fun startViewEventsActivity(){
        val intent = Intent(this@EventActivity, ManageEventsActivity::class.java).apply {}
        startActivity(intent)
    }

    /**
     * start view events activity
     *
     */
    fun startViewAllEventsActivity(){
        val intent = Intent(this@EventActivity, EventsActivity::class.java).apply {}
        startActivity(intent)
    }

    /**
     * start events results activity
     *
     * @param eventId
     */
    override fun startEventResultsActivity(eventId : Int){
        val intent = Intent(this@EventActivity, ResultsActivity::class.java).apply {}
        intent.putExtra("EVENT_ID", eventId)
        startActivity(intent)
    }

    /**
     * call presenter on destroy
     *
     */
    override fun onDestroy() {
        eventPresenter.onDestroy()
        super.onDestroy()
    }

    override fun finish(){
        super.finish()
    }

}
