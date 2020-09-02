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
    private fun checkExternalStoragePermission() : Boolean{
        val state : String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    /**
     * Build a GPX file according to list of control objects
     * Use www.topograpfix.com schema
     * @param controls
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun buildGPX(controls :MutableList<Control>){
        // check file saving permissions
        val permission = checkExternalStoragePermission()
        val timeStamp : String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        Log.e("FileWriter","Permission check = $permission")
        // begin file writing, determine file name
        val fileName = "$timeStamp.gpx"
        // declare file header
        val gpxHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"Handrail\" version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
        // declare gpx file name
        val gpxName = "<name>" + "My Controls $timeStamp" + "</name><trkseg>\n";
        // declare control segments and initiate
        var controlSegments = ""
        // for each passed control, add latitude and longitude values and date value
        for (control in controls){
            val formattedDate : String = control.controlTime.substring(0,23)+'Z'
            controlSegments += "<trkpt lat=\"" + control.controlLatitude + "\" lon=\"" + control.controlLongitude + "\">" +
                    "<time>" + formattedDate + "</time>" +
                    "</trkpt>\n"
        }
        // add gpx footer
        val gpxfooter = "</trkseg></trk></gpx>"
        // call savegpx method to save to file
        saveGPX(fileName,gpxHeader,gpxName,controlSegments,gpxfooter)
    }

    /**
     * Save GPX File to system storage, inform user of succcess
     *
     * @param fileName
     * @param gpxHeader
     * @param gpxName
     * @param controlSegments
     * @param gpxFooter
     */
    private fun saveGPX(fileName : String, gpxHeader : String, gpxName : String, controlSegments : String, gpxFooter : String){
        // try to create a new file
        try{
            val folder : File? = context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            var file : File = File(folder, fileName)

            if (!file.exists()){
                file.createNewFile()
                Log.e("FileWriter", "file doesn't exist")
            } else {
                Log.e("FileWriter", "file exists")
            }
            // create an output stream to write fields to bytearray in created file
            val fileOutputSteam = FileOutputStream(file)

            if (fileOutputSteam != null) {

                fileOutputSteam.write(gpxHeader.toByteArray())
                fileOutputSteam.write(gpxName.toByteArray())
                fileOutputSteam.write(controlSegments.toByteArray())
                fileOutputSteam.write(gpxFooter.toByteArray())

                Log.e("FileWriter","Saved $fileName")
                // tell user file has been saved
                Toast.makeText(context,"File Saved as $fileName",Toast.LENGTH_SHORT).show()
            } else {
                Log.e("FileWriter", "Problem with writing content")
                Toast.makeText(context,"Error: Problem creating file",Toast.LENGTH_SHORT).show()
            }

            Log.e("FileWriter","Reading text = ${file.readText()}")
            // catch io exception, inform user and log stacktrace
        } catch (e : IOException){
            Log.e("FileWriter","Problem writing file")
            e.printStackTrace()
            Toast.makeText(context,"Error: Problem creating file",Toast.LENGTH_SHORT).show()
        }
    }
}