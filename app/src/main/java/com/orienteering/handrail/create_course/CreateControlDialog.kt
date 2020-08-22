package com.orienteering.handrail.create_course

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.orienteering.handrail.dialogs.StandardDialogListener
import java.lang.ClassCastException

/**
 * Dialog handle input for the creation of a single control item
 *
 */
class CreateControlDialog : AppCompatDialogFragment() {

    private lateinit var edittextControlName : EditText
    private lateinit var edittextControlNote : EditText
    lateinit var listener: StandardDialogListener

    /**
     * Create a control by passing information via applytext method to view and presenter
     *
     * @param savedInstanceState
     * @return
     */
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogueBuilder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val layoutInflater : LayoutInflater = activity!!.layoutInflater
        val view : View = layoutInflater.inflate(com.orienteering.handrail.R.layout.layout_dialog,null)

        alertDialogueBuilder.setView(view).setTitle("Add Control").setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
            .setPositiveButton("Create") { _: DialogInterface, _: Int ->
                val username : String = edittextControlName.text.toString()
                val note : String = edittextControlNote.text.toString()
                // when not empty pass information to listener via applytext method
                when {
                    username.trim().isEmpty() -> {
                        edittextControlName.error="Please enter a control name"
                        Toast.makeText(context,"Please enter a name and note",Toast.LENGTH_SHORT).show()
                    }
                    note.trim().isEmpty() -> {
                        edittextControlName.error="Please enter a control note"
                        Toast.makeText(context,"Please enter a name and note",Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        listener.applyText(username,note)
                    }
                }
            }

        edittextControlName = view.findViewById(com.orienteering.handrail.R.id.edittext_control_name)
        edittextControlNote = view.findViewById(com.orienteering.handrail.R.id.edittext_control_note)

        return alertDialogueBuilder.create()
    }

    /**
     * Create listener via context
     * @param context
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as StandardDialogListener
        } catch (e: ClassCastException) {
            throw  ClassCastException(context.toString() + "listener must use StandardDialogListener")
        }
    }



}