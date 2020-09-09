package com.orienteering.handrail.signup

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import com.orienteering.handrail.R
import com.orienteering.handrail.httprequests.SignupRequest
import com.orienteering.handrail.interactors.SignupInteractor
import com.orienteering.handrail.login.LoginActivity
import java.util.*

// TAG for Logs
private val TAG: String = SignupActivity::class.java.name

/**
 * Class responsible for user interface of sign up use case
 *
 */
class SignupActivity : AppCompatActivity(), ISignupContract.ISignupView{

    // presenter handles signup logic
    lateinit var signupPresenter : SignupPresenter

    /**
     * edit texts and text views for user creation fields
     */
    lateinit var editTextFirstName : EditText
    lateinit var editTextLastName : EditText
    lateinit var editTextEmail : EditText
    lateinit var textViewDob : TextView
    lateinit var editTextBio : EditText
    lateinit var editTextPassword : EditText
    lateinit var editTextConfirmPassword : EditText
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();
    /**
     * buttons for dob entry and create account (calls signup service)
     */
    lateinit var btnDob : Button
    lateinit var btnCreateAccount : Button

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
     * initialise view, buttons, edit text and signup presenter class
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        createButtons()
        createEditText()

        signupPresenter = SignupPresenter(this, SignupInteractor())
    }

    /**
     * initiate edit text for user input
     *
     */
    fun createEditText() {
        editTextFirstName = findViewById(R.id.editText_signup_firstname)
        editTextLastName = findViewById(R.id.editText_signup_lastname)
        editTextEmail = findViewById(R.id.editText_signup_email)
        textViewDob = findViewById(R.id.textView_signup_dob)
        editTextBio = findViewById(R.id.editText_signup_bio)
        editTextPassword = findViewById(R.id.editText_signup_password)
        editTextConfirmPassword = findViewById(R.id.editText_signup_confirm_password)
    }

    /**
     * Initiate buttons and set onclick listeners
     *
     */
    fun createButtons() {
        btnDob = findViewById(R.id.btn_signup_dob)
        btnCreateAccount = findViewById(R.id.btn_signup_create_account)
        progressDialog = ProgressDialog(this@SignupActivity)
        progressDialog.setCancelable(false)
        // create date dialog picker
        btnDob.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val datePickerDialog = DatePickerDialog(this@SignupActivity,
                    DatePickerDialog.OnDateSetListener{ view: DatePicker?, Tyear: Int, Tmonth: Int, TdayOfMonth: Int ->
                        // hold year, month and day information
                        var yearString : String = Tyear.toString()
                        var monthString : String = Tmonth.toString()
                        var dayString : String = TdayOfMonth.toString()
                        // add leading 0s if day and month<10
                        if (monthString.length==1){
                            monthString="0"+monthString
                        }
                        if (dayString.length==1){
                            dayString="0"+dayString
                        }
                        // provide string value for user display
                        userDob = "$yearString-$monthString-$dayString"
                        textViewDob.text=userDob
                    }, year, month, day)
                // max date to prevent future dob values
                datePickerDialog.datePicker.maxDate=System.currentTimeMillis()
                datePickerDialog.show()
            }

        })
        // create a signup request and request presenter send to source
        btnCreateAccount.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (validateFields()){
                    val signupRequest = SignupRequest(editTextFirstName.text.toString(),editTextLastName.text.toString(),editTextEmail.text.toString(),editTextPassword.text.toString(),textViewDob.text.toString(),editTextBio.text.toString())
                    if (signupRequest!=null){
                        progressDialog.setMessage("Creating Account...")
                        progressDialog.show()
                        signupPresenter.postDataToServer(signupRequest)
                    }
                    else {
                        makeToast("Problem creating your account. Please check all fields.")
                    }
                } else {
                    makeToast("Please check all fields.")
                }
            }
        })
    }

    /**
     * Check each user input field, validate and provide error message if invalid
     *
     * @return
     */
    fun validateFields(): Boolean {
        var inputsOk : Boolean = true
        if (editTextFirstName.text.toString().trim().length<=0){
            editTextFirstName.setError("Enter your first name")
            inputsOk=false
        }
        if (editTextLastName.text.toString().trim().length<=0){
            editTextLastName.setError("Enter your last name")
            inputsOk=false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString()).matches()){
            editTextEmail.setError("Invalid Email Address")
            inputsOk=false
        }
        if (textViewDob.text.toString().trim().length<=0){
            textViewDob.setError("Enter a valid date of birth")
            inputsOk=false
        }
        if (editTextBio.text.toString().trim().length<=0){
            editTextBio.setError("Enter a Bio")
            inputsOk=false
        }
        if (editTextPassword.text.toString().trim().length<8||editTextPassword.text.toString().trim().length>16){
            editTextPassword.setError("Enter a password of 8 to 16 characters")
            inputsOk=false
        } else {
            if (!editTextConfirmPassword.text.toString().equals(editTextPassword.text.toString())){
                editTextConfirmPassword.setError("Passwords do not match")
                inputsOk=false
            }
        }
        return inputsOk
    }

    override fun makeToast(message: String) {
        val toast : Toast = Toast.makeText(this@SignupActivity,message, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseFailure(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);

        makeToast("Service unavailable. Please try again later.")
    }

    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);

        makeToast("Service unavailable. Please try again later.")
    }

    override fun emailInUse(){
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        makeToast("Email already in use.")
        editTextEmail.setError("Email already in use")
    }

    /**
     * Initiate login activity on success of signin
     *
     */
    override fun startLoginActivity() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val intent = Intent(this@SignupActivity, LoginActivity::class.java).apply {}
        startActivity(intent)
        finish()
    }
}
