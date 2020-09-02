package com.orienteering.handrail.models

import java.io.Serializable

class Course(courseControls: List<Control>? = null, courseName: String, courseNote: String )  : Serializable {

    var courseId : Int? = null
    var courseName : String
    var courseNote : String
    lateinit var courseDate : String
    var courseControls = mutableListOf<Control>()

    init{
        this.courseName = courseName
        this.courseNote=courseNote
        if (courseControls!=null){
            this.courseControls = courseControls as MutableList<Control>
        }
    }

    override fun toString(): String {
        return "Course(courseId=$courseId, courseControls=$courseControls)"
    }


}