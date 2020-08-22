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
import com.orienteering.handrail.image_utilities.ImageSelect
import java.util.*

// TAG for Logs
private val TAG: String = EditProfileActivity::class.java.getName()

/**
 * View for edit profile use case - binds user input and sends to presenter class
 * Displays all retrieved user data
 */
class EditProfileActivity : AppCompatActivity(), IEditProfileContract.IEditProfileView {

    lateinit var editProfilePresenter : IEditProfileContract.IEditProfilePresenter
    lateinit var imageSelect : ImageSelect

    /**
     * edit texts and text views for user creation fields
     */
    private lateinit var profileImageView: ImageView
    lateinit var editTextFirstName : EditText
    lateinit var editTextLastName : EditText
    lateinit var editTextEmail : EditText
    lateinit var textViewDob : TextView
    lateinit var editTextBio : EditText

    /**
     * buttons for user input
     */
    private lateinit var buttonSelectImage : Button
    private lateinit var buttonSelectDOB : Button
    private lateinit var buttonUpdateInfo : Button
    private lateinit var buttonChangePassword: Button

    /**
     * user dob value
     */
    var userDob : String = "dummy"

    /**
     * calendar for date and time values to correctly display and modify for creation
     */
    val calendar: Calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    /**
     * Initialises view, buttons, text, images, image select and presenter
     * Requests presenter retrieves data from backend
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        createButtons()
        createText()
        createImage()
        this.imageSelect = ImageSelect(
            this,
            this@EditProfileActivity
        )
        editProfilePresenter = EditProfilePresenter(this,UserInteractor(),imageSelect)
        editProfilePresenter.getDataFromServer()
    }

    /**
     * Initialised Buttons and Listeners
     */
    fun createButtons(){
        buttonSelectImage = findViewById(R.id.btn_editprofile_update_image)
        buttonSelectDOB = findViewById(R.id.btn_editprofile_dob)
        buttonUpdateInfo = findViewById(R.id.btn_editprofile_update_account)
        buttonChangePassword = findViewById(R.id.btn_editprofile_change_password)
        // starts gallery or camera intent from dialog selection
        buttonSelectImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                editProfilePresenter.selectImage()
            }
        })
        //provides date dialog and converts selected date to string for use in display and upload
        buttonSelectDOB.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val datePickerDialog = DatePickerDialog(this@EditProfileActivity,
                    DatePickerDialog.OnDateSetListener{ view: DatePicker?, Tyear: Int, Tmonth: Int, TdayOfMonth: Int ->

                        val yearString : String = Tyear.toString()
                        var monthString : String = Tmonth.toString()
                        var dayString : String = TdayOfMonth.toString()
                        // add leading 0 if date is before october
                        if (monthString.length==1){
                            monthString="0"+monthString
                        }
                        // add leading 0 if date is before 10th
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
        // check user fields on update button press, if successful call presenter to place user on server
        buttonUpdateInfo.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (checkUserFields()){
                    val user : User = User(editTextEmail.text.toString(),editTextFirstName.text.toString(),editTextLastName.text.toString(),userDob,editTextBio.text.toString())
                    if (user!=null){
                        editProfilePresenter.putDataOnServer(user)
                    } else {
                        Toast.makeText(this@EditProfileActivity,"Error: Problem updating your account. Please check all fields.",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity,"Error: Please check all fields.", Toast.LENGTH_SHORT).show()
                }
            }
        })
        // start intent of password update activity
        buttonChangePassword.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent : Intent = Intent(this@EditProfileActivity, PasswordUpdateActivity::class.java)
                startActivity(intent)
            }
        })
    }

    /**
     * Initialised Text and Listeners
     */
    private fun createText(){
        editTextFirstName = findViewById(R.id.editText_editprofile_firstname)
        editTextLastName = findViewById(R.id.editText_editprofile_lastname)
        editTextEmail = findViewById(R.id.editText_editprofile_email)
        textViewDob = findViewById(R.id.textView_editprofile_dob)
        editTextBio = findViewById(R.id.editText_editprofile_bio)
    }

    /**
     * Initialise Image
     */
    private fun createImage(){
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

    /**
     * setup image from uri selected by image select, load into view
     *
     * @param imageUri
     */
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
        textViewDob.text = dob
    }

    /**
     * Check input fields, return true if successful
     */
    fun checkUserFields(): Boolean {
        var inputsOk = true
        if (editTextFirstName.text.toString().trim().length <= 0) {
            editTextFirstName.error = "Enter your first name"
            inputsOk = false
        }
        if (editTextFirstName.text.toString().trim().length <= 0) {
            editTextLastName.error = "Enter your last name"
            inputsOk = false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString())
                .matches()
        ) {
            editTextEmail.error = "Invalid Email Address"
            inputsOk = false
        }
        if (textViewDob.text.toString().trim().length <= 0) {
            textViewDob.error = "Enter a valid date of birth"
            inputsOk = false
        }
        if (editTextBio.text.toString().trim().length <= 0) {
            editTextBio.error = "Enter a Bio"
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

    /**
     * Start intent home activity on success
     *
     */
    override fun onUpdateResponseSuccess() {
        Log.i(TAG,"Success Updating User")
        Toast.makeText(this@EditProfileActivity,"Successfully Updated.",Toast.LENGTH_SHORT).show()
        val intent = Intent(this@EditProfileActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
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
                    // camera intent
                    if (resultCode == Activity.RESULT_OK) {

                        editProfilePresenter.setImage(imageSelect.tempImageUri)
                        setupImage(imageSelect.tempImageUri)

                    } else {
                        Log.e(TAG, "Result: $resultCode  Data: $data")
                    }
                }
                1002 -> {
                    //gallery intent
                    val permission = imageSelect.checkExternalStoragePermission()
                    Log.e("FileWriter", "Permission check = $permission")

                    Log.e(TAG, "Request 1002")
                    if (resultCode == Activity.RESULT_OK && data != null) {

                        data.data?.let { editProfilePresenter.setImage(it) }
                        data.data?.let { setupImage(it) }
                    }
                }
            }
        } else {
            Log.e(TAG, "Request cancelled...")
        }
    }

    /**
     * Call presenter onDestroy() to destroy view
     *
     */
    override fun onDestroy(){
        super.onDestroy()
        editProfilePresenter.onDestroy()
    }
}
