package com.orienteering.handrail.utilities

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import com.orienteering.handrail.utilities.App.AppCompanion.context
import com.orienteering.handrail.classes.Control
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class GPXBuilder(context : Context, controls :MutableList<Control>)  {

    val controls : MutableList<Control> = controls

    fun checkExternalStoragePermission() : Boolean{

        val state : String = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true
        } else {
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun buildGPX(){

        val permission = checkExternalStoragePermission()

        Log.e("FileWriter","Permission check = $permission")

        val header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
        val name = "<name>" + "MyControls" + "</name><trkseg>\n";
        var segments = ""
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

        for (control in controls){
            segments += "<trkpt lat=\"" + control.controlLatitude + "\" lon=\"" + control.controlLongitude + "\">" +
                    //"<time>" + df.format(control.time) + "</time>" +
                    "</trkpt>\n"
        }

        val footer = "</trkseg></trk></gpx>"

        try{
            val folder : File? = context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            var file : File = File(folder, "example.gpx")

            if (!file.exists()){
                file.createNewFile()
                Log.e("filewriter", "file doesn't exist")
            } else {
                Log.e("filewriter", "file exists")
            }

            val fileWriter : FileWriter = FileWriter(file)
            val bufferedWriter : BufferedWriter = BufferedWriter(fileWriter)

            val fileOutputSteam = FileOutputStream(file)



            if (fileOutputSteam != null) {

                fileOutputSteam.write(header.toByteArray())
                fileOutputSteam.write(name.toByteArray())
                fileOutputSteam.write(segments.toByteArray())
                fileOutputSteam.write(footer.toByteArray())

                Log.e("FileWriter","Saved example.gpx")
            } else {
                Log.e("FileWriter", "Problem with writing content")
            }

            Log.e("Filereader","Reading text = ${file.readText()}")

        } catch (e : IOException){
            Log.e("Filewriter","Problem writing file")
            e.printStackTrace()
        }

    }
}