package com.orienteering.handrail.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.User
import com.orienteering.handrail.controllers.SignupController
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.SignupRequest
import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Acitivity handles user input and calls signup of new account
 *
 */
class SignupActivity : AppCompatActivity() {

    // Tag for class log
    val TAG : String = "SignupActivity"

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
     * Signup controller, handles signup requests
     */
    val signupController=SignupController()

    /**
     * callback handle signup
     */
    val callback = object : Callback<StatusResponseEntity<Boolean>> {
        override fun onFailure(call: Call<StatusResponseEntity<Boolean>>, t: Throwable) {
            Log.e(TAG, "Failure connecting to user signup")
            Log.e(TAG,"Success creating user")
            val toast : Toast = Toast.makeText(this@SignupActivity,"Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<Boolean>>,
            response: Response<StatusResponseEntity<Boolean>>
        ) {
            if (response.isSuccessful){
                Log.e(TAG,"Success creating user")
                val toast : Toast = Toast.makeText(this@SignupActivity,"Successfully Registered.",Toast.LENGTH_SHORT)
                val intent : Intent = Intent(this@SignupActivity,LoginActivity::class.java)
                startActivity(intent)
                toast.show()
            } else  if (response.code()==409){
                Log.e(TAG, "Error registering user. Email in use.")
                val toast : Toast = Toast.makeText(this@SignupActivity,"Email already in use.",Toast.LENGTH_SHORT)
                toast.show()
                editTextEmail.setError("Email already in use")
            } else {
                Log.e(TAG, "Error registering user")
                val toast : Toast = Toast.makeText(this@SignupActivity,"Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    /**
     * on create initalialises view and components
     *
     * @param savedInstanceState    // calendar for date and time values to correctly display and modify for creation
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        createButtons()
        createEditTexts()
    }

    /**
     * Creates buttons and sets onclick listeners
     *
     */
    fun createButtons(){
        btnDob = findViewById(R.id.btn_signup_dob)
        btnCreateAccount = findViewById(R.id.btn_signup_create_account)

        btnDob.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val datePickerDialog = DatePickerDialog(this@SignupActivity,
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

        btnCreateAccount.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (checkUserFields()){
                    val signupRequest : SignupRequest = SignupRequest(editTextFirstName.text.toString(),editTextLastName.text.toString(),editTextEmail.text.toString(),editTextPassword.text.toString(),textViewDob.text.toString(),editTextBio.text.toString())
                    if (signupRequest!=null){
                        Log.e(TAG,signupRequest.toString())
                        postNewSignup(signupRequest)
                    }
                     else {
                        val toast = Toast.makeText(this@SignupActivity,"Problem creating your account. Please check all fields.",Toast.LENGTH_SHORT)
                        toast.show()
                    }
                } else {
                    val toast = Toast.makeText(this@SignupActivity,"Please check all fields.",Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        })

    }

    /**
     * Creates edit text and sets change listeners
     * Creates textview for dob
     */
    fun createEditTexts(){
        editTextFirstName = findViewById(R.id.editText_signup_firstname)
        editTextLastName = findViewById(R.id.editText_signup_lastname)
        editTextEmail = findViewById(R.id.editText_signup_email)
        textViewDob = findViewById(R.id.textView_signup_dob)
        editTextBio = findViewById(R.id.editText_signup_bio)
        editTextPassword = findViewById(R.id.editText_signup_password)
        editTextConfirmPassword = findViewById(R.id.editText_signup_confirm_password)
    }


    fun postNewSignup(signupRequest : SignupRequest){
        signupController.signup(signupRequest,callback)
    }

    /**
     * Check input fields
     */
    fun checkUserFields() : Boolean{
        var inputsOk : Boolean = true
        if (editTextFirstName.text.toString().trim().length<=0){
            editTextFirstName.setError("Enter your first name")
            inputsOk=false
        }
        if (editTextFirstName.text.toString().trim().length<=0){
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
        if (editTextPassword.text.toString().trim().length<=0){
            editTextPassword.setError("Enter a password")
            inputsOk=false
        } else {
            if (!editTextConfirmPassword.text.toString().equals(editTextPassword.text.toString())){
                editTextConfirmPassword.setError("Passwords do not match")
                inputsOk=false
            }
        }
        return inputsOk
    }
}
