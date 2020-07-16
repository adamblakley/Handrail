package com.orienteering.handrail.classes

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Course(courseControls: List<Control>, courseName : String )  : Serializable {

    val courseId : Int? = null
    var courseName : String
    var courseDate : String
    var courseControls = mutableListOf<Control>()

    init{
        this.courseName = courseName

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val formattedDate = sdf.format(Date())
        this.courseDate = formattedDate

        this.courseControls = courseControls as MutableList<Control>
    }

    override fun toString(): String {
        return "Course(courseId=$courseId, courseControls=$courseControls)"
    }


}