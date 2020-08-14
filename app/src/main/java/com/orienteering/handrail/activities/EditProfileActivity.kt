package com.orienteering.handrail.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.util.IOUtils
import com.orienteering.handrail.R
import com.orienteering.handrail.models.User
import com.orienteering.handrail.controllers.UserController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.ImageSelect
import com.orienteering.handrail.utilities.PermissionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity View and Edit Profile
 *
 */
class EditProfileActivity : AppCompatActivity() {

    // Tag for class log
    val TAG : String = "EditProfileActivity"

    /**
     * edit texts and text views for user creation fields
     */
    lateinit var profileImageView: ImageView
    lateinit var editTextFirstName : EditText
    lateinit var editTextLastName : EditText
    lateinit var editTextEmail : EditText
    lateinit var textViewDob : TextView
    lateinit var editTextBio : EditText

    /**
     * buttons for user input
     */
    lateinit var buttonSelectImage : Button
    lateinit var buttonSelectDOB : Button
    lateinit var buttonUpdateInfo : Button
    lateinit var buttonChangePassword: Button

    /**
     * user returned from service call
     */
    lateinit var user : User

    /**
     * Image Uri for select image
     */
    var imageUri: Uri? = null


    /**
     * User Controller controls user service
     */
    val userController = UserController()

    /**
     * Image select for selecting image from camera intent or gallery and associated methods
     */
    val imageSelect : ImageSelect = ImageSelect(this,this)

    /**
     * user dob value
     */
    lateinit var userDob : String

    /**
     * calendar for date and time values to correctly display and modify for creation
     */
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)


    /**
     * callback handle get user info
     */
    val getUserCallback = object : Callback<StatusResponseEntity<User>?> {
        override fun onFailure(call: Call<StatusResponseEntity<User>?>, t: Throwable) {
            Log.e(TAG, "Failure connecting to service")
            Log.e(TAG,"Error: Service Currently Unavailable")
            val toast : Toast = Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<User>?>,
            response: Response<StatusResponseEntity<User>?>
        ) {
            if (response.isSuccessful){
                Log.e(TAG,"Success getting User")

                if (response.body()!=null){
                    user = response.body()!!.entity!!
                    userDob = user.userDob
                    fillUserInformation()
                    initBitMap()

                } else {
                    Log.e(TAG, "Error user empty")
                    val toast : Toast = Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
                    toast.show()
                }

            } else {
                Log.e(TAG, "Error getting user")
                val toast : Toast = Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    /**
     * callback handle user update
     */
    val updateUserCallback = object : Callback<StatusResponseEntity<User>?> {
        override fun onFailure(call: Call<StatusResponseEntity<User>?>, t: Throwable) {
            Log.e(TAG, "Failure connecting to service")
            Log.e(TAG,"Error: Service Currently Unavailable")
            val toast : Toast = Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<User>?>,
            response: Response<StatusResponseEntity<User>?>
        ) {
            if (response.isSuccessful){
                Log.e(TAG,"Success Updating User")
                val toast : Toast = Toast.makeText(this@EditProfileActivity,"Successfully Updated.",Toast.LENGTH_SHORT)
                val intent : Intent = Intent(this@EditProfileActivity,HomeActivity::class.java)
                startActivity(intent)
                toast.show()
            } else  if (response.code()==206){
                Log.e(TAG, "Partial Success Updating User.")
                val toast : Toast = Toast.makeText(this@EditProfileActivity,"Partially updated. Image update unavailable.",Toast.LENGTH_SHORT)
                toast.show()
            } else {
                Log.e(TAG, "Error Updating user")
                val toast : Toast = Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    /**
     * Overides onCreate setup content view initialise buttons and events, calls user service
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        createButtons()
        createText()
        createImage()
        getUser()
    }

    /**
     * Initialised Buttons and Listeners
     */
    fun createButtons(){
        buttonSelectImage = findViewById(R.id.btn_editprofile_update_image)
        buttonSelectDOB = findViewById(R.id.btn_editprofile_dob)
        buttonUpdateInfo = findViewById(R.id.btn_editprofile_update_account)
        buttonChangePassword = findViewById(R.id.btn_editprofile_change_password)

        buttonSelectImage?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (PermissionManager.checkPermission(
                        this@EditProfileActivity,
                        this@EditProfileActivity,
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        PermissionManager.MULTIPLE_REQUEST_CODES
                    )
                ) {
                    imageUri=imageSelect.selectImage()
                }
            }
        })

        buttonSelectDOB.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val datePickerDialog = DatePickerDialog(this@EditProfileActivity,
                    DatePickerDialog.OnDateSetListener{ view: DatePicker?, Tyear: Int, Tmonth: Int, TdayOfMonth: Int ->

                        var yearString : String = Tyear.toString()
                        var monthString : String = Tmonth.toString()
                        var dayString : String = TdayOfMonth.toString()

                        if (monthString.length==1){
                            monthString="0"+monthString
                        }
                        if (dayString.length==1){
                            dayString="0"+dayString
                        }
                        userDob = "$yearString-$monthString-$dayString"
                        textViewDob.text=userDob
                    }, year, month, day)
                datePickerDialog.datePicker.maxDate=System.currentTimeMillis()
                datePickerDialog.show()
            }
        })

        buttonUpdateInfo.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (checkUserFields()){
                    user.userFirstName= editTextFirstName.text.toString()
                    user.userLastName = editTextLastName.text.toString()
                    user.userEmail = editTextEmail.text.toString()
                    user.userBio = editTextBio.text.toString()
                    user.userDob = userDob

                    if (user!=null){
                        if (imageUri!=null){
                            val imageMultipartBodyPart : MultipartBody.Part? = imageUri?.let { createImageMultipartBody(it) }
                            userController.update(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),user,imageMultipartBodyPart,updateUserCallback)
                        } else {
                            userController.update(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),user,updateUserCallback)
                        }
                    }
                    else {
                        val toast = Toast.makeText(this@EditProfileActivity,"Error: Problem updating your account. Please check all fields.",Toast.LENGTH_SHORT)
                        toast.show()
                    }
                } else {
                    val toast = Toast.makeText(this@EditProfileActivity,"Error: Please check all fields.",Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        })

        buttonChangePassword?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent : Intent = Intent(this@EditProfileActivity,PasswordActivity::class.java)
                startActivity(intent)
            }
        })
    }

    /**
     * Create image upload Request and return
     *
     */
    fun createImageMultipartBody(fileUri : Uri) : MultipartBody.Part{
        if (fileUri.toString()[0].equals('c')) {
            Log.e(TAG,"I START WITH C")
            var inputStream: InputStream? = contentResolver.openInputStream(fileUri)
            var file = File(fileUri.toString())
            try {
                val timeStamp : String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
                var cachedFile: File = File(cacheDir, "JPEG_${timeStamp}.jpg")
                try {
                    var outputStream: OutputStream = FileOutputStream(cachedFile)
                    IOUtils.copyStream(inputStream, outputStream)
                } catch (f: FileNotFoundException) {
                    Log.e(TAG, f.printStackTrace().toString())
                } catch (i: IOException) {
                    Log.e(TAG, i.printStackTrace().toString())
                }
                val requestBody: RequestBody = RequestBody.create(contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() }, cachedFile)
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", cachedFile.name, requestBody)
                return body
            } catch (i: IOException) {
                Log.e(TAG, i.printStackTrace().toString())
            }
        }
        var file = File(imageSelect.getImagePath(fileUri))
        val requestBody : RequestBody = RequestBody.create(contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() },file)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("file",file.name,requestBody)
        return body
    }

    /**
     * Initialised Text and Listeners
     */
    fun createText(){
        editTextFirstName = findViewById(R.id.editText_editprofile_firstname)
        editTextLastName = findViewById(R.id.editText_editprofile_lastname)
        editTextEmail = findViewById(R.id.editText_editprofile_email)
        textViewDob = findViewById(R.id.textView_editprofile_dob)
        editTextBio = findViewById(R.id.editText_editprofile_bio)
    }

    /**
     * Initialise Image
     */
    fun createImage(){
        profileImageView = findViewById(R.id.imageCircle_editprofile_image)
    }

    /**
     * Get User from User Controller
     *
     */
    fun getUser(){
        userController.read(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getUserCallback)
    }

    /**
     * fill information returned from get user
     *
     */
    fun fillUserInformation(){
        editTextFirstName.setText(user.userFirstName)
        editTextLastName.setText(user.userLastName)
        editTextEmail.setText(user.userEmail)
        editTextBio.setText(user.userBio)

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val dateformatted = sdf.parse(user.userDob)

        val dateFormatter = SimpleDateFormat("dd-MM-YYYY")
        val date: String = dateFormatter.format(dateformatted)

        textViewDob.setText(date)
    }

    /**
     * Edit bitmap of user image
     *
     */
    fun initBitMap(){
        val options : RequestOptions = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        if (user.isUserPhotographInitialised()){
            if (user.userPhotographs.isNotEmpty()){
                for (photo in user.userPhotographs){
                    if (photo.active!!){
                        Glide.with(this)
                            .asBitmap()
                            .load(photo.photoPath)
                            .apply(options)
                            .into(profileImageView)
                    }
                }
            }
        }
    }

    fun updateBitMap() {
        val options: RequestOptions =
            RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .apply(options)
                .into(profileImageView)
    }
        /**
         * Check input fields
         */
        fun checkUserFields(): Boolean {
            var inputsOk: Boolean = true
            if (editTextFirstName.text.toString().trim().length <= 0) {
                editTextFirstName.setError("Enter your first name")
                inputsOk = false
            }
            if (editTextFirstName.text.toString().trim().length <= 0) {
                editTextLastName.setError("Enter your last name")
                inputsOk = false
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString())
                    .matches()
            ) {
                editTextEmail.setError("Invalid Email Address")
                inputsOk = false
            }
            if (textViewDob.text.toString().trim().length <= 0) {
                textViewDob.setError("Enter a valid date of birth")
                inputsOk = false
            }
            if (editTextBio.text.toString().trim().length <= 0) {
                editTextBio.setError("Enter a Bio")
                inputsOk = false
            }
            return inputsOk
        }

        /**
         * On Activity Result for image select
         * Displays image view of event
         * @param requestCode
         * @param resultCode
         * @param data
         */
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            Log.e(TAG, "Displaying Photo")
            if (requestCode != Activity.RESULT_CANCELED) {
                when (requestCode) {
                    1001 -> {
                        Log.e(TAG, "Request 1001")
                        if (resultCode == Activity.RESULT_OK) {

                            imageUri = imageSelect.tempImageUri

                            updateBitMap()

                        } else {
                            Log.e(TAG, "Result: $resultCode  Data: $data")
                        }
                    }
                    1002 -> {
                        val permission = imageSelect.checkExternalStoragePermission()
                        Log.e("FileWriter", "Permission check = $permission")

                        Log.e(TAG, "Request 1002")
                        if (resultCode == Activity.RESULT_OK && data != null) {

                            imageUri = data.data

                            updateBitMap()
                        }
                    }
                }
            } else {
                Log.e(TAG, "Request cancelled...")
            }
        }

}
