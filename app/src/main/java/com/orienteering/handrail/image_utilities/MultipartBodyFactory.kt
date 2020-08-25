package com.orienteering.handrail.image_utilities

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
     * @param activity
     * @param fileUri
     * @param fileName
     * @return
     */
    fun createImageMultipartBody(activity : Activity, fileUri : Uri, fileName:String) : MultipartBody.Part{
        // check if file is content uri, if so request a cached file written by output stream
        // this is as a content uri does not necessarily point to a specific file, a content file may be a promise to hold a file unknown
        if (fileUri.toString()[0].equals('c')) {
            Log.e(TAG,"I START WITH C")
            // open inputstream on passed file uri
            var inputStream: InputStream? = activity.contentResolver.openInputStream(fileUri)
            try {
                // create new temp file in cachedir with unique date name
                val timeStamp : String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
                var cachedFile: File = File(activity.cacheDir, "JPEG_${timeStamp}.jpg")
                try {
                    // attempt write to file via output stream, catch errors
                    var outputStream: OutputStream = FileOutputStream(cachedFile)
                    IOUtils.copyStream(inputStream, outputStream)
                } catch (f: FileNotFoundException) {
                    Log.e(TAG, f.printStackTrace().toString())
                } catch (i: IOException) {
                    Log.e(TAG, i.printStackTrace().toString())
                }
                // create a request body with file type associated and cached file added as body
                val requestBody: RequestBody = RequestBody.create(activity.contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() }, cachedFile)
                // create multipart.body.part to pass file to service along with additional json objects
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", fileName, requestBody)
                return body
            } catch (i: IOException) {
                Log.e(TAG, i.printStackTrace().toString())
            }
        }
        // else request file from image uri
        var file = File(imageSelect.getImagePath(fileUri))
        // create a request body with file type associated and cached file added as body
        val requestBody : RequestBody = RequestBody.create(activity.contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() },file)
        // create multipart.body.part to pass file to service along with additional json objects
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file",fileName,requestBody)
        return body
    }

}