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


class ImageSelect(activity: Activity, context: Context){

    private val IMAGE_CAPTURE_CODE = 1001
    private val PICK_IMAGE_CODE = 1002
    lateinit var currentPhotoPath: String
    lateinit var tempImageUri : Uri
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
        builder.setTitle("Choose Control Photo")
        builder.setItems(
            options,
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, item: Int ->
                if (options[item].equals("Take Photo")) {
                    openCamera()
                } else if (options[item].equals("Choose from Gallery")) {
                    val pickPhotoIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    activity.startActivityForResult(pickPhotoIntent, PICK_IMAGE_CODE)
                } else {
                    dialogInterface.dismiss()
                }
            })
        builder.show()
        return image_uri
    }

    fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            takePictureIntent -> takePictureIntent.resolveActivity(context.packageManager)?.also {

                val photoFile: File? = try {
                    createPhotoFile()
                } catch (ex: IOException) {
                    Log.e("ImageSelect",ex.printStackTrace().toString())
                    null
                }

                photoFile.also {
                    val photoUri : Uri? = it?.let { it ->
                        FileProvider.getUriForFile(context,"com.orienteering.handrail.fileprovider",
                            it
                        )

                    }
                    if (photoUri != null) {
                        tempImageUri=photoUri
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
                    activity.startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
                }

            }
        }

/*
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.TITLE, "From the Camera")
        val image_uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        activity.startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)

 */
    }

    /**
     * get path of selected image for file
     * @param contentUri
     * @return
     */
    fun getImagePath(contentUri: Uri) : String?{
        var result : String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)

        val loader = CursorLoader(context,contentUri,proj,null,null,null)

        val cursor : Cursor? = loader.loadInBackground()
        if (cursor!=null){
            val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            result = columnIndex?.let { cursor?.getString(it) }
            cursor?.close()
        }
        return result
    }

    fun createPhotoFile() : File{
        val timeStamp : String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        val storageDir : File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if (!storageDir.exists()){
            try{
                storageDir.mkdirs()
            } catch(ex : Exception){
                Log.e("ImageSelect",ex.printStackTrace().toString())
            }
        }
        return File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir).apply { currentPhotoPath=absolutePath }
    }
}