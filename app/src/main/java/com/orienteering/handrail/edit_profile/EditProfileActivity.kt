package com.orienteering.handrail.edit_profile

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
import com.orienteering.handrail.R
import com.orienteering.handrail.home_menu.HomeActivity
import com.orienteering.handrail.interactors.UserInteractor
import com.orienteering.handrail.models.User
import com.orienteering.handrail.password_update.PasswordUpdateActivity
import com.orienteering.handrail.utilities.ImageSelect
import java.util.*

class EditProfileActivity : AppCompatActivity(), IEditProfileContract.IEditProfileView {

    // Tag for class log
    val TAG : String = "EditProfileActivity"

    lateinit var editProfilePerformer : IEditProfileContract.IEditProfilePerformer
    lateinit var imageSelect : ImageSelect

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
     * user dob value
     */
    var userDob : String = "dummy"

    /**
     * calendar for date and time values to correctly display and modify for creation
     */
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        createButtons()
        createText()
        createImage()
        this.imageSelect = ImageSelect(this,this@EditProfileActivity)
        editProfilePerformer = EditProfilePerformer(this,UserInteractor(),imageSelect)
        editProfilePerformer.getDataFromServer()
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
                editProfilePerformer.selectImage()
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
                    var user : User = User(editTextEmail.text.toString(),editTextFirstName.text.toString(),editTextLastName.text.toString(),userDob,editTextBio.text.toString())
                    if (user!=null){
                        editProfilePerformer.putDataOnServer(user)
                    } else {
                        Toast.makeText(this@EditProfileActivity,"Error: Problem updating your account. Please check all fields.",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity,"Error: Please check all fields.", Toast.LENGTH_SHORT).show()
                }
            }
        })

        buttonChangePassword?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent : Intent = Intent(this@EditProfileActivity, PasswordUpdateActivity::class.java)
                startActivity(intent)
            }
        })
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

    override fun setupImage(imageUrl : String){
        val options: RequestOptions =
            RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .apply(options)
            .into(profileImageView)
    }

    fun setupImage(imageUri : Uri) {
        val options: RequestOptions =
            RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round)
        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .apply(options)
            .into(profileImageView)
    }

    /**
     * fill information returned from get user
     *
     */
    override fun fillInformation(firstName : String, lastName : String, email : String, bio : String, dob : String){
        editTextFirstName.setText(firstName)
        editTextLastName.setText(lastName)
        editTextEmail.setText(email)
        editTextBio.setText(bio)
        textViewDob.setText(dob)
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

    override fun onGetResponseFailure(throwable: Throwable) {
        Log.e(TAG, "Failure connecting to service")
        Log.e(TAG,"Error: Service Currently Unavailable")
        val toast : Toast = Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onGetResponseError() {
        Log.e(TAG, "Error getting user")
        val toast : Toast = Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onUpdateResponseFailure(throwable: Throwable) {
        Log.e(TAG, "Failure connecting to service")
        Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateResponseError() {
        Log.e(TAG, "Error user empty")
        Toast.makeText(this@EditProfileActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT).show()
    }

    override fun onUpdatePartialResponseError() {
        Log.e(TAG, "Error Image upload failure")
        Toast.makeText(this@EditProfileActivity,"Error: Image update unavailable.",Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateResponseSuccess() {
        Log.i(TAG,"Success Updating User")
        Toast.makeText(this@EditProfileActivity,"Successfully Updated.",Toast.LENGTH_SHORT).show()
        val intent : Intent = Intent(this@EditProfileActivity, HomeActivity::class.java)
        startActivity(intent)
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

                        editProfilePerformer.setImage(imageSelect.tempImageUri)
                        setupImage(imageSelect.tempImageUri)

                    } else {
                        Log.e(TAG, "Result: $resultCode  Data: $data")
                    }
                }
                1002 -> {
                    val permission = imageSelect.checkExternalStoragePermission()
                    Log.e("FileWriter", "Permission check = $permission")

                    Log.e(TAG, "Request 1002")
                    if (resultCode == Activity.RESULT_OK && data != null) {

                        data.data?.let { editProfilePerformer.setImage(it) }
                        data.data?.let { setupImage(it) }
                    }
                }
            }
        } else {
            Log.e(TAG, "Request cancelled...")
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        editProfilePerformer.onDestroy()
    }
}
