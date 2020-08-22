package com.orienteering.handrail.create_course

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.orienteering.handrail.R
import com.orienteering.handrail.dialogs.EventDialogListener
import java.lang.ClassCastException

/**
 * Dialog for creation of a course
 *
 */
class CreateCourseDialog : AppCompatDialogFragment() {

    lateinit var edittextCourseName : EditText
    lateinit var edittextCourseNote : EditText
    // listener determines outcome of button press and value input
    lateinit var listener: EventDialogListener

    /**
     * set view, buttons and inputs. validate input, apply listener
     *
     * @param savedInstanceState
     * @return
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogueBuilder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val layoutInflater : LayoutInflater = activity!!.layoutInflater
        val view : View = layoutInflater.inflate(R.layout.layout_event_creation_dialog,null)

        // set buttons and title
        alertDialogueBuilder.setView(view).setTitle("Create Course").setNegativeButton("Cancel",{ dialogInterface: DialogInterface, i: Int -> })
            .setPositiveButton("Create",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->

                    Log.e("dialog","Create")

                    val courseName : String = edittextCourseName.text.toString()
                    val courseNote : String = edittextCourseNote.text.toString()
                // validiate fields, greater than 0
                if (courseName.trim().length<=0){
                    edittextCourseName.error="Please enter a course name"
                    Toast.makeText(context,"Please enter a course name and note", Toast.LENGTH_SHORT).show()
                } else if(courseNote.trim().length<=0){
                    edittextCourseNote.error="Please enter a course note"
                    Toast.makeText(context,"Please enter a course name and note",Toast.LENGTH_SHORT).show()
                } else {
                    // apply listener on change of input
                    listener.applyEventText(courseName,courseNote)
                }
            })

        edittextCourseName = view.findViewById<EditText>(R.id.edittext_event_name)
        edittextCourseNote = view.findViewById<EditText>(R.id.edittext_event_note)

        return alertDialogueBuilder.create()
    }

    /**
     * Attach to view via context
     * @param context
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as EventDialogListener
        } catch (e: ClassCastException) {
            throw  ClassCastException(context.toString() + "must implement EventDialogListener")
        }
    }



}