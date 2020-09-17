package com.orienteering.handrail.image_utilities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Class responsible for accessing device images via camera or gallery
 * responsible for retrieving image uri and creating temporary photo file before transfer to web-layer
 * @constructor
 * TODO
 *
 * @param activity
 * @param context
 */
class ImageSelect(activity: Activity, context: Context){

    // capture codes for camera or gallery
    private val IMAGE_CAMERA_CODE = 1001
    private val IMAGE_GALLERY_CODE = 1002
    // used to hold image uri or selected photo and photopath of temporary files
    lateinit var currentPhotoPath: String
    lateinit var tempImageUri : Uri
    // activity and context of calling class
    var activity : Activity
    var context : Context

    init{
        this.activity = activity
        this.context= context
    }

    /**
     * Check external storage permission for media
     *
     * @return
     */
    fun checkExternalStoragePermission() : Boolean{
        val state : String = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true
        } else {
            return false
        }
    }

    /**
     * Get image uri from device
     *
     */
    fun selectImage() : Uri? {
        var image_uri : Uri? = null;
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Photo")
        builder.setItems(
            options,
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, item: Int ->
                if (options[item].equals("Take Photo")) {
                    // open device camera
                    openCamera()
                } else if (options[item].equals("Choose from Gallery")) {
                    // open device gallery, pass intent extra for use in on result method
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activity.startActivityForResult(pickPhotoIntent, IMAGE_GALLERY_CODE)
                } else {
                    dialogInterface.dismiss()
                }
            })
        builder.show()
        // return image uri to calling class
        return image_uri
    }

    /**
     * Open device camera, selected image returned as URI
     *
     */
    fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            takePictureIntent -> takePictureIntent.resolveActivity(context.packageManager)?.also {
                // create new photo file and catch error if presented
                val photoFile: File? = try {
                    createPhotoFile()
                } catch (ex: IOException) {
                    Log.e("ImageSelect",ex.printStackTrace().toString())
                    null
                }
                // capture uri from temp new file
                photoFile.also {
                    val photoUri : Uri? = it?.let { it -> FileProvider.getUriForFile(context,"com.orienteering.handrail.fileprovider", it) }
                    if (photoUri != null) {
                        tempImageUri=photoUri
                    }
                    // open camera activity, provide capture code as intent extra to be passed to context for onresult method
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
                    activity.startActivityForResult(takePictureIntent, IMAGE_CAMERA_CODE)
                }

            }
        }
    }

    /**
     * get path of selected image for file
     * @param contentUri
     * @return
     */
    fun getImagePath(contentUri: Uri) : String?{
        // final pathway
        var result : String? = null
        // required information to interact with content providers
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        // read data from content provider
        val loader = CursorLoader(context,contentUri,proj,null,null,null)

        val cursor : Cursor? = loader.loadInBackground()
        if (cursor!=null){
            // return correct column index of file pathname
            val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
            // point to the first value of the returned result
            cursor?.moveToFirst()
            // get filepathname from selected index
            result = columnIndex?.let { cursor?.getString(it) }
            cursor?.close()
        }
        //return pathway
        return result
    }

    /**
     * Create a temporary file before transfer to web-service layer
     *
     * @return
     */
    fun createPhotoFile() : File{
        // create timestamp name for unique naming system
        val timeStamp : String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        // determine sotrage directory
        val storageDir : File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        // check if storage directory exists, or make new storage directory
        if (!storageDir.exists()){
            try{
                storageDir.mkdirs()
            } catch(ex : Exception){
                Log.e("ImageSelect",ex.printStackTrace().toString())
            }
        }
        // create a temp file within the cosen sotrage directory, return file to calling class
        return File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir).apply { currentPhotoPath=absolutePath }
    }
}