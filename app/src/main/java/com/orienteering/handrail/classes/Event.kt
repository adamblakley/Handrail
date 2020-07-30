package com.orienteering.handrail.classes

import java.io.Serializable
import java.text.SimpleDateFormat

class Event(eventName: String, eventCourse: Course, eventDate: String, eventCreated : String, eventNote: String) : Serializable{

    val eventId : Int? = null
    var eventName : String
    lateinit var eventCreated : String
    lateinit var eventOrganiser : User
    var eventCourse : Course
    var eventDate : String
    var eventNote : String
    lateinit var participants : List<Participant>
    lateinit var eventStatus : Integer
    lateinit var eventPhotograph: Photograph

    init{
        this.eventName = eventName
        this.eventCourse = eventCourse
        val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val receivedDate = sdf1.parse(eventDate)
        this.eventDate = sdf2.format(receivedDate)
        val receivedCreatedDate = sdf1.parse(eventCreated)
        this.eventCreated = sdf2.format(receivedCreatedDate)
        this.eventNote = eventNote
    }

    override fun toString(): String {
        return "Event(eventId=$eventId, eventName='$eventName', eventCreated='$eventCreated', eventOrganiser=$eventOrganiser, eventCourse=$eventCourse, eventDate='$eventDate', eventNote='$eventNote', participants=$participants, eventStatus=$eventStatus, eventPhotograph=$eventPhotograph)"
    }


}