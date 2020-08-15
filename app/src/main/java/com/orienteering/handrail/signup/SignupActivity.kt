package com.orienteering.handrail.signup

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.orienteering.handrail.R
import com.orienteering.handrail.httprequests.SignupRequest
import com.orienteering.handrail.interactors.SignupInteractor
import com.orienteering.handrail.login.LoginActivity
import java.util.*

class SignupActivity : AppCompatActivity(), ISignupContract.ISignupView{

    // Tag for class log
    val TAG : String = "SignupActivity"

    lateinit var signupPerformer : SignupPerformer

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        createButtons()
        createEditText()

        signupPerformer = SignupPerformer(this, SignupInteractor())
    }

    override fun createEditText() {
        editTextFirstName = findViewById(R.id.editText_signup_firstname)
        editTextLastName = findViewById(R.id.editText_signup_lastname)
        editTextEmail = findViewById(R.id.editText_signup_email)
        textViewDob = findViewById(R.id.textView_signup_dob)
        editTextBio = findViewById(R.id.editText_signup_bio)
        editTextPassword = findViewById(R.id.editText_signup_password)
        editTextConfirmPassword = findViewById(R.id.editText_signup_confirm_password)
    }

    override fun createButtons() {
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
                if (validateFields()){
                    val signupRequest = SignupRequest(editTextFirstName.text.toString(),editTextLastName.text.toString(),editTextEmail.text.toString(),editTextPassword.text.toString(),textViewDob.text.toString(),editTextBio.text.toString())
                    if (signupRequest!=null){
                        signupPerformer.postDataToServer(signupRequest)
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

    override fun validateFields(): Boolean {
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
        makeToast("Service unavailable. Please try again later.")
    }

    override fun onResponseError() {
        makeToast("Service unavailable. Please try again later.")
    }

    override fun emailInUse(){
        makeToast("Email already in use.")
        editTextEmail.setError("Email already in use")
    }

    override fun startLoginActivity() {
        val intent = Intent(this@SignupActivity, LoginActivity::class.java).apply {}
        startActivity(intent)
    }
}
