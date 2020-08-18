package com.orienteering.handrail.gpx_utilities

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.orienteering.handrail.models.Control
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * GPX File Writer to handle file writing of GPX File
 *
 * @constructor
 *
 * @param context
 */
class GPXBuilder(context : Context)  {

    // Context used in creation of file directory
    var context : Context = context

    /**
     * Check external storage permissions
     * @return
     */
    fun checkExternalStoragePermission() : Boolean{
        val state : String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED.equals(state)
    }

    /**
     * Build a GPX file according to list of control objects
     * Use www.topograpfix.com schema
     * @param controls
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun buildGPX(controls :MutableList<Control>){

        val permission = checkExternalStoragePermission()
        val timeStamp : String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        Log.e("FileWriter","Permission check = $permission")
        val fileName = "$timeStamp.gpx"
        val gpxHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"Handrail\" version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
        val gpxName = "<name>" + "My Controls $timeStamp" + "</name><trkseg>\n";
        var controlSegments = ""

        for (control in controls){
            val formattedDate : String = control.controlTime.substring(0,23)+'Z'
            controlSegments += "<trkpt lat=\"" + control.controlLatitude + "\" lon=\"" + control.controlLongitude + "\">" +
                    "<time>" + formattedDate + "</time>" +
                    "</trkpt>\n"
        }

        val gpxfooter = "</trkseg></trk></gpx>"

        saveGPX(fileName,gpxHeader,gpxName,controlSegments,gpxfooter)
    }

    /**
     * Save GPX File
     *
     * @param fileName
     * @param gpxHeader
     * @param gpxName
     * @param controlSegments
     * @param gpxFooter
     */
    fun saveGPX(fileName : String, gpxHeader : String, gpxName : String, controlSegments : String, gpxFooter : String){
        try{
            val folder : File? = context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            var file : File = File(folder, fileName)

            if (!file.exists()){
                file.createNewFile()
                Log.e("filewriter", "file doesn't exist")
            } else {
                Log.e("filewriter", "file exists")
            }

            val fileOutputSteam = FileOutputStream(file)

            if (fileOutputSteam != null) {

                fileOutputSteam.write(gpxHeader.toByteArray())
                fileOutputSteam.write(gpxName.toByteArray())
                fileOutputSteam.write(controlSegments.toByteArray())
                fileOutputSteam.write(gpxFooter.toByteArray())

                Log.e("FileWriter","Saved $fileName")
                val toast = Toast.makeText(context,"File Saved as $fileName",Toast.LENGTH_SHORT)
                toast.show()
            } else {
                Log.e("FileWriter", "Problem with writing content")
                val toast = Toast.makeText(context,"Error: Problem creating file",Toast.LENGTH_SHORT)
                toast.show()
            }

            Log.e("FileWriter","Reading text = ${file.readText()}")

        } catch (e : IOException){
            Log.e("FileWriter","Problem writing file")
            e.printStackTrace()
            val toast = Toast.makeText(context,"Error: Problem creating file",Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}