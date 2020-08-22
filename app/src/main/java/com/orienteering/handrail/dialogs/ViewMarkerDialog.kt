package com.orienteering.handrail.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R
import java.lang.ClassCastException

/**
 * Display control information via dialog on selection of individual control
 *
 * @constructor
 * TODO
 *
 * @param nameOfMarker
 * @param noteOfMarker
 * @param positionOfMarker
 * @param imagePath
 */
class ViewMarkerDialog(nameOfMarker:String? = "Control", noteOfMarker: String? = "Example Text", positionOfMarker: Int? = 0, imageUriOfMarker: Uri? = null ) : AppCompatDialogFragment()  {

    val TAG : String = "ViewMarkerDialog"
    lateinit var listener: StandardDialogListener

    var nameOfMarker : String?
    var positionOfMarker : Int? = null
    var imageUriOfMarker : Uri? = null
    var noteOfMarker : String?

    lateinit var textViewMarkerName : TextView
    lateinit var textViewMarkerNote : TextView
    lateinit var textViewMarkerPosition : TextView
    lateinit var imageViewMarkerImage : ImageView

    init{
        this.nameOfMarker = nameOfMarker
        this.noteOfMarker = noteOfMarker
        this.positionOfMarker = positionOfMarker
        this.imageUriOfMarker = imageUriOfMarker
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogueBuilder : AlertDialog.Builder = AlertDialog.Builder(activity)


        val layoutInflater : LayoutInflater = activity!!.layoutInflater
        val view : View = layoutInflater.inflate(com.orienteering.handrail.R.layout.layout_view_marker,null)

        textViewMarkerName = view.findViewById(R.id.marker_name)
        textViewMarkerNote = view.findViewById(R.id.marker_note)
        textViewMarkerPosition = view.findViewById(R.id.marker_position)
        imageViewMarkerImage = view.findViewById(R.id.marker_image)

        alertDialogueBuilder.setView(view).setTitle(nameOfMarker).setNegativeButton("Cancel",{ dialogInterface: DialogInterface, i: Int -> })

        setTextandImage()


        return alertDialogueBuilder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as StandardDialogListener
        } catch (e: ClassCastException) {
            throw  ClassCastException(context.toString() + "must implement ExampleDialogListener")
        }
    }

    /**
     * Set image of control and apply name, note and position text
     *
     */
    private fun setTextandImage(){
        textViewMarkerName.text=nameOfMarker
        textViewMarkerNote.text=noteOfMarker
        textViewMarkerPosition.text=positionOfMarker.toString()

        if (imageUriOfMarker!=null){
            Log.e(TAG,imageUriOfMarker.toString())
            val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
            context?.let {
                Glide.with(it)
                    .asBitmap()
                    .load(imageUriOfMarker)
                    .apply(options)
                    .into(imageViewMarkerImage)
            }
        }
    }
}