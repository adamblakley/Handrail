package com.orienteering.handrail.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.orienteering.handrail.*
import com.orienteering.handrail.classes.Control
import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.classes.User
import com.orienteering.handrail.services.*
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.ImageSelect
import com.orienteering.handrail.utilities.PermissionManager
import kotlinx.android.synthetic.main.activity_home_menu.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HomeActivity : AppCompatActivity() {

    var button1: Button? = null
    var button2: Button? = null
    var button3: Button? = null

    var button7: Button? = null
    var button8: Button? = null
    var button9: Button? = null

    lateinit var imageview1: ImageView



    private val IMAGE_CAPTURE_CODE = 1001
    private val PICK_IMAGE_CODE = 1002
    var image_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_menu)

        button1 = findViewById<Button>(R.id.btn_create_course)
        button2 = findViewById<Button>(R.id.btn_compete_event)
        button3 = findViewById<Button>(R.id.btn_create_event)

        button7 = findViewById<Button>(R.id.btn_take_photo)
        button8 = findViewById<Button>(R.id.btn_upload_photo)

        button9 = findViewById<Button>(R.id.btn_logout)

        imageview1 = findViewById<ImageView>(R.id.imageview_myphoto)

        button9?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {

                    val sharedPreferences = App.sharedPreferences
                    val sharedPreferencesEditor = sharedPreferences.edit()
                    sharedPreferencesEditor.remove(App.SharedPreferencesAuthToken).commit()
                    sharedPreferencesEditor.remove(App.SharedPreferencesAuthToken).commit()
                    val intent = Intent(this@HomeActivity, WelcomeActivity::class.java).apply {}
                    startActivity(intent)
                }

            })

        button1?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val intent = Intent(this@HomeActivity, CreateMapsActivity::class.java).apply {}
                startActivity(intent)
            }

        })

        button2?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val intent = Intent(this@HomeActivity, EventsActivity::class.java).apply {}
                startActivity(intent)
            }
        })

        button3?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@HomeActivity, CreateEventActivity::class.java).apply {}
                startActivity(intent)
            }
        })


        button7?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (PermissionManager.checkPermission(
                        this@HomeActivity,
                        this@HomeActivity,
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        PermissionManager.MULTIPLE_REQUEST_CODES
                    )
                ) {
                    selectImage(this@HomeActivity)
                }
            }
        })

        button8?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Log.e(TAG,"upload pressed")
                Log.e(TAG,"image_uri = "+image_uri)
                image_uri?.let { uploadImage(it) }
            }
        })



    }

/*
    fun createUser() {

        val date: Date = Date()
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val newdate = sdf.format(date)
        val user = User(2,newdate, "b@b", "b", "b", "bb", "2019-06-19T00:16:43.375+00:00", "mybio", 1)
        ServiceFactory.makeService(UserService::class.java).create(user)
            .enqueue(object : Callback<StatusResponseEntity<User>?> {
                override fun onFailure(call: Call<StatusResponseEntity<User>?>, t: Throwable) {
                    Log.e(TAG, "Failure adding user")
                }

                override fun onResponse(
                    call: Call<StatusResponseEntity<User>?>,
                    response: Response<StatusResponseEntity<User>?>
                ) {
                    Log.e(TAG, "Success adding user")
                }
            })
    }

 */

    fun getUsers() {
        ServiceFactory.makeService(UserService::class.java).readAll()
            .enqueue(object : retrofit2.Callback<List<User>?> {
                override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                    Log.e(TAG, "Failure getting users")
                }

                override fun onResponse(call: Call<List<User>?>, response: Response<List<User>?>) {
                    Log.e(TAG, "Success getting users")
                    val usersgot: List<User>? = response.body()
                    if (usersgot != null) {
                        for (user in usersgot) {
                            Log.e(TAG, user.toString())
                        }
                    }
                }
            })
    }

    fun getCourses() {
        ServiceFactory.makeService(CourseService::class.java).readAll()
            .enqueue(object : retrofit2.Callback<List<Course>?> {
                override fun onFailure(call: Call<List<Course>?>, t: Throwable) {
                    Log.e(TAG, "Failure getting courses")
                }

                override fun onResponse(
                    call: Call<List<Course>?>,
                    response: Response<List<Course>?>
                ) {
                    Log.e(TAG, "Success getting courses")
                    val coursegot: List<Course>? = response.body()
                    if (coursegot != null) {
                        for (course in coursegot) {
                            Log.e(TAG, course.toString())
                        }
                    }
                }
            })
    }

    fun getControls() {
        ServiceFactory.makeService(ControlService::class.java).readAll()
            .enqueue(object : retrofit2.Callback<List<Control>?> {
                override fun onFailure(call: Call<List<Control>?>, t: Throwable) {
                    Log.e(TAG, "Failure getting controls")
                }

                override fun onResponse(
                    call: Call<List<Control>?>,
                    response: Response<List<Control>?>
                ) {
                    Log.e(TAG, "Success getting controls")
                    val controlsgot: List<Control>? = response.body()
                    if (controlsgot != null) {
                        for (control in controlsgot) {
                            Log.e(TAG, control.toString())
                        }
                    }
                }
            })
    }

    fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.TITLE, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Control Photo")
        builder.setItems(
            options,
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, item: Int ->

                if (options[item].equals("Take Photo")) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, "New Picture")
                    values.put(MediaStore.Images.Media.TITLE, "From the Camera")
                    image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
                } else if (options[item].equals("Choose from Gallery")) {
                    val pickPhotoIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhotoIntent, PICK_IMAGE_CODE)
                } else {
                    dialogInterface.dismiss()
                }

            })
        builder.show()
    }

    fun uploadImage(fileUri : Uri){

        Log.e(TAG,"upload function running = " + fileUri)
        val file = File(getImagePath(fileUri))

        val requestBody : RequestBody = RequestBody.create(contentResolver.getType(fileUri)?.let {
            it
                .toMediaTypeOrNull()
        },file)

        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file",file.name,requestBody)

        Log.e(TAG,"$body")
    }

    fun checkExternalStoragePermission() : Boolean{

        val state : String = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true
        } else {
            return false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "Displaying Photo")
        if (requestCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1001 -> {
                    Log.e(TAG, "Request 1001")
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Log.e(TAG, "Result ok, data not null")
                        val selectedImage: Bitmap = data.extras?.get("data") as Bitmap
                        imageview_myphoto.setImageBitmap(selectedImage)
                    } else {
                        Log.e(TAG,"Result: $resultCode  Data: $data")
                    }
                }
                1002 -> {

                    val permission = checkExternalStoragePermission()
                    Log.e("FileWriter","Permission check = $permission")

                    Log.e(TAG, "Request 1002")
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Log.e(TAG,"result ok and data doesn't equal null")
                        val selectedImage: Uri? = data.data
                        image_uri = data.data
                        var filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)
                        if (selectedImage != null) {
                            val cursor: Cursor? = contentResolver.query(
                                selectedImage,
                                filePathColumn,
                                null,
                                null,
                                null
                            )
                            if (cursor != null) {
                                cursor.moveToFirst()

                                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                                val picturePath: String = cursor.getString(columnIndex)
                                imageview_myphoto.setImageBitmap(
                                    BitmapFactory.decodeFile(
                                        picturePath
                                    )
                                )
                                cursor.close()
                            }
                        }
                    }
                }

            }
        } else {
            Log.e(TAG, "Request cancelled...")
        }
    }

    fun getImagePath(contentUri: Uri) : String?{
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this,contentUri,proj,null,null,null)
        val cursor : Cursor? = loader.loadInBackground()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = columnIndex?.let { cursor?.getString(it) }
        cursor?.close()
        Log.e(TAG,"path = $result")
        return result
    }


}

private const val TAG = "HomeActivity"

