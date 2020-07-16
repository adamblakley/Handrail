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
import com.orienteering.handrail.classes.ParticipantControlPerformance
import com.orienteering.handrail.httprequests.EventService
import com.orienteering.handrail.httprequests.ParticipantService
import com.orienteering.handrail.httprequests.ServiceFactory
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.GeofencingConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class ViewEventActivity : AppCompatActivity() {

    val TAG : String = "ViewEventActivity"

    lateinit var eventNameTextView : TextView
    lateinit var eventNoteTextView : TextView
    lateinit var eventDateTextView: TextView
    lateinit var eventTimeTextView: TextView
    lateinit var joinEventButton: Button
    lateinit var startEventCourse : Button

    lateinit var event : Event
    var eventIdPassed : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        eventNameTextView = findViewById(R.id.textView_event_name_view_event)
        eventNoteTextView = findViewById(R.id.textView_event_note_view_event)
        eventDateTextView = findViewById(R.id.textView_event_date_event_view)
        eventTimeTextView = findViewById(R.id.textView_event_time_event_view)

        joinEventButton = findViewById(R.id.button_join_event_event_view)
        startEventCourse = findViewById(R.id.button_start_course_view_event)

        startEventCourse.visibility =View.INVISIBLE

        this.eventIdPassed =  intent.getSerializableExtra("EVENT_ID") as Int
        getEvent()



        startEventCourse.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@ViewEventActivity, CourseParticipationActivity::class.java).apply {}
                intent.putExtra("EVENT_ID", event.eventId)
                startActivity(intent)
            }
        })

        joinEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                joinEvent()
            }
        })
    }

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


    fun getEvent() {
        if (intent.extras!=null){
            ServiceFactory.makeService(EventService::class.java).read(eventIdPassed)
                .enqueue(object : Callback<Event> {
                    override fun onFailure(call: Call<Event>, t: Throwable) {
                        Log.e(TAG, "Failure getting event")
                    }
                    override fun onResponse(
                        call: Call<Event>,
                        response: Response<Event>
                    ) {
                        Log.e(TAG, "Success getting event")
                        val eventgot: Event? = response.body()
                        if (eventgot != null) {
                            event = eventgot
                            fillEventInformation()
                            for (participant in event.participants){
                                Log.e(TAG,"${participant.toString()}")
                                if (participant.participantUser.userId?.equals(3)!!){
                                    joinEventButton.visibility = View.INVISIBLE
                                    startEventCourse.visibility = View.VISIBLE
                                    break
                                }
                            }
                        }

                    }
                })
        }
    }

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
