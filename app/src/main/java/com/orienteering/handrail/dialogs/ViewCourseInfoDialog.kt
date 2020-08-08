package com.orienteering.handrail.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import java.lang.ClassCastException

class ViewCourseInfoDialog(courseName: String?=null, courseNote : String?=null, courseAltitudes : MutableList<Double>? = null, courseDistance :Double) : AppCompatDialogFragment() {

    var courseName : String? = courseName
    var courseNote : String? = courseNote
    var courseAltitude: Double? = null
    var courseDistance: Double = courseDistance

    lateinit var textViewCourseName : TextView
    lateinit var textViewCourseNote : TextView
    lateinit var textViewCourseAltitude : TextView
    lateinit var textViewCourseDistance : TextView
    lateinit var listener: ExampleDialogListener

    init{
        if (courseAltitudes != null) {
            courseAltitude = courseAltitudes.sum()/courseAltitudes.size
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogueBuilder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val layoutInflater : LayoutInflater = activity!!.layoutInflater
        val view : View = layoutInflater.inflate(com.orienteering.handrail.R.layout.layout_course_information_dialog,null)

        alertDialogueBuilder.setView(view).setTitle("Course Info").setNegativeButton("Cancel",{ dialogInterface: DialogInterface, i: Int -> })

        textViewCourseName = view.findViewById<EditText>(com.orienteering.handrail.R.id.textView_course_info_name)
        textViewCourseNote = view.findViewById<EditText>(com.orienteering.handrail.R.id.textView_course_info_note)
        textViewCourseAltitude = view.findViewById<EditText>(com.orienteering.handrail.R.id.textView_course_info_altitude)
        textViewCourseDistance = view.findViewById<EditText>(com.orienteering.handrail.R.id.textView_course_info_distance)

        fillText()

        return alertDialogueBuilder.create()
    }

    private fun fillText(){
        if(courseName!=null){
            textViewCourseName.text = courseName
        }
        if (courseNote!=null){
            textViewCourseNote.text = courseNote
        }
        if (courseAltitude!=null){
            textViewCourseAltitude.text = "Average Altitude: %.2f".format(courseAltitude)
        }
        if (courseDistance!=null){
            textViewCourseDistance.text = "Total Distance: %.2f".format(courseDistance)
        }

    }

}