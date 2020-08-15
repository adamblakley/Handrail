package com.orienteering.handrail.utilities

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.google.android.gms.common.util.IOUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Responsible for creation of MultipartBody for images file upload
 *
 * @constructor
 * TODO
 *
 * @param imageSelect
 */
class MultipartBodyFactory(imageSelect: ImageSelect) {

    val TAG = "MultipartBodyFactory"
    var imageSelect : ImageSelect

    init{
        this.imageSelect = imageSelect
    }

    /**
     * Create image upload Request and return
     *
     */
    fun createImageMultipartBody(activity : Activity, fileUri : Uri) : MultipartBody.Part{
        if (fileUri.toString()[0].equals('c')) {
            Log.e(TAG,"I START WITH C")
            var inputStream: InputStream? = activity.contentResolver.openInputStream(fileUri)
            var file = File(fileUri.toString())
            try {
                val timeStamp : String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
                var cachedFile: File = File(activity.cacheDir, "JPEG_${timeStamp}.jpg")
                try {
                    var outputStream: OutputStream = FileOutputStream(cachedFile)
                    IOUtils.copyStream(inputStream, outputStream)
                } catch (f: FileNotFoundException) {
                    Log.e(TAG, f.printStackTrace().toString())
                } catch (i: IOException) {
                    Log.e(TAG, i.printStackTrace().toString())
                }
                val requestBody: RequestBody = RequestBody.create(activity.contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() }, cachedFile)
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", cachedFile.name, requestBody)
                return body
            } catch (i: IOException) {
                Log.e(TAG, i.printStackTrace().toString())
            }
        }
        var file = File(imageSelect.getImagePath(fileUri))
        val requestBody : RequestBody = RequestBody.create(activity.contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() },file)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file",file.name,requestBody)
        return body
    }

}