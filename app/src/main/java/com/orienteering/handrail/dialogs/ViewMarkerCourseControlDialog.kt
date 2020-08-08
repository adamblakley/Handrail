package com.orienteering.handrail.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orienteering.handrail.R

class ViewMarkerCourseControlDialog(nameOfMarker:String? = "No Name Available", noteOfMarker: String? = "No Note Available", positionOfMarker: Int? = 0, imagePath: String? = null ) : AppCompatDialogFragment() {
    val TAG : String = "ViewMarkerDialog"


    var nameOfMarker : String?
    var positionOfMarker : Int? = null
    var imagePath : String? = null
    var noteOfMarker : String?

    lateinit var textViewMarkerName : TextView
    lateinit var textViewMarkerNote : TextView
    lateinit var textViewMarkerPosition : TextView
    lateinit var imageViewMarkerImage : ImageView

    init{

        this.nameOfMarker = nameOfMarker
        this.noteOfMarker = noteOfMarker
        this.positionOfMarker = positionOfMarker
        this.imagePath = imagePath
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
    }

    private fun setTextandImage(){
        textViewMarkerName.text=nameOfMarker
        textViewMarkerNote.text=noteOfMarker
        textViewMarkerPosition.text=positionOfMarker.toString()

        if (imagePath!=null){
            val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(
                R.mipmap.ic_launcher_round)
            context?.let {
                Glide.with(it)
                    .asBitmap()
                    .load(imagePath)
                    .apply(options)
                    .into(imageViewMarkerImage)
            }
        }
    }
}