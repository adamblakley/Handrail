package com.orienteering.handrail.classes

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Course(courseControls: List<Control>, courseName : String )  : Serializable {

    val courseId : Int? = null
    var courseName : String
    var courseNote : String? = null
    lateinit var courseDate : String
    var courseControls = mutableListOf<Control>()

    init{
        this.courseName = courseName
        this.courseControls = courseControls as MutableList<Control>
    }

    override fun toString(): String {
        return "Course(courseId=$courseId, courseControls=$courseControls)"
    }


}