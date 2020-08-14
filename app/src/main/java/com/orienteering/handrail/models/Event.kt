package com.orienteering.handrail.models

import java.io.Serializable
import java.text.SimpleDateFormat

class Event(eventName: String, eventCourse: Course, eventDate: String, eventNote: String) : Serializable{

    val eventId : Int? = null
    var eventName : String
    lateinit var eventCreated : String
    lateinit var eventOrganiser : User
    var eventCourse : Course
    var eventDate : String
    var eventNote : String
    lateinit var participants : List<Participant>
    lateinit var eventStatus : Integer
    lateinit var eventPhotographs: List<Photograph>

    init{
        this.eventName = eventName
        this.eventCourse = eventCourse
        val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val receivedDate = sdf1.parse(eventDate)
        this.eventDate = sdf2.format(receivedDate)
        this.eventNote = eventNote
    }

    override fun toString(): String {
        return "Event(eventId=$eventId, eventName='$eventName', eventCreated='$eventCreated', eventOrganiser=$eventOrganiser, eventCourse=$eventCourse, eventDate='$eventDate', eventNote='$eventNote', participants=$participants, eventStatus=$eventStatus, eventPhotograph=$eventPhotographs)"
    }


}