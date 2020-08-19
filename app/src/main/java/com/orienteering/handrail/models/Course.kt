package com.orienteering.handrail.models

import java.io.Serializable

class Course(courseControls: List<Control>, courseName : String, courseNote : String )  : Serializable {

    val courseId : Int? = null
    var courseName : String
    var courseNote : String? = null
    lateinit var courseDate : String
    var courseControls = mutableListOf<Control>()

    init{
        this.courseName = courseName
        this.courseNote=courseNote
        this.courseControls = courseControls as MutableList<Control>
    }

    override fun toString(): String {
        return "Course(courseId=$courseId, courseControls=$courseControls)"
    }


}