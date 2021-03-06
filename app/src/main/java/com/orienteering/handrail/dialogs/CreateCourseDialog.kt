package com.orienteering.handrail.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import com.orienteering.handrail.R
import java.lang.ClassCastException

class CreateCourseDialog : AppCompatDialogFragment() {

    lateinit var edittextCourseName : EditText
    lateinit var edittextCourseNote : EditText
    lateinit var listener: EventDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogueBuilder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val layoutInflater : LayoutInflater = activity!!.layoutInflater
        val view : View = layoutInflater.inflate(R.layout.layout_event_creation_dialog,null)



        alertDialogueBuilder.setView(view).setTitle("Create Course").setNegativeButton("Cancel",{ dialogInterface: DialogInterface, i: Int -> })
            .setPositiveButton("Create",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->

                    Log.e("dialog","Create")

                    val courseName : String = edittextCourseName.text.toString()
                    val courseNote : String = edittextCourseNote.text.toString()
                    listener.applyEventText(courseName,courseNote)

            })

        edittextCourseName = view.findViewById<EditText>(R.id.edittext_event_name)
        edittextCourseNote = view.findViewById<EditText>(R.id.edittext_event_note)

        return alertDialogueBuilder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as EventDialogListener
        } catch (e: ClassCastException) {
            throw  ClassCastException(context.toString() + "must implement ExampleDialogListener")
        }
    }



}