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

class CreateControlDialog : AppCompatDialogFragment() {

    lateinit var edittextControlName : EditText
    lateinit var edittextControlNote : EditText
    lateinit var listener: ExampleDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogueBuilder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val layoutInflater : LayoutInflater = activity!!.layoutInflater
        val view : View = layoutInflater.inflate(com.orienteering.handrail.R.layout.layout_dialog,null)



        alertDialogueBuilder.setView(view).setTitle("Add Control").setNegativeButton("Cancel",{ dialogInterface: DialogInterface, i: Int -> })
            .setPositiveButton("Create",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->

                    Log.e("dialog","Create")

                    val username : String = edittextControlName.text.toString()
                    val note : String = edittextControlNote.text.toString()
                    listener.applyText(username,note)

            })

        edittextControlName = view.findViewById<EditText>(com.orienteering.handrail.R.id.edittext_control_name)
        edittextControlNote = view.findViewById<EditText>(com.orienteering.handrail.R.id.edittext_control_note)

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