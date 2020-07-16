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
import java.lang.ClassCastException

class PickCourseDialog : AppCompatDialogFragment() {

    lateinit var listener: ExampleDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogueBuilder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val layoutInflater : LayoutInflater = activity!!.layoutInflater
        val view : View = layoutInflater.inflate(com.orienteering.handrail.R.layout.layout_course_item,null)

        alertDialogueBuilder.setView(view).setTitle("Select Course").setNegativeButton("Cancel",{ dialogInterface: DialogInterface, i: Int -> })

        return alertDialogueBuilder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ExampleDialogListener
        } catch (e: ClassCastException) {
            throw  ClassCastException(context.toString() + "must implement ExampleDialogListener")
        }
    }

}