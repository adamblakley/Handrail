package com.orienteering.handrail.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.orienteering.handrail.R

class SignupActivity : AppCompatActivity(), ISignupContract.ISignupView{

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        createButtons()
        createEditText()
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
    }

    override fun makeToast(message: String) {
        TODO("Not yet implemented")
    }

    override fun onResponseFailure(throwable: Throwable) {
        TODO("Not yet implemented")
    }

    override fun onResponseError() {
        TODO("Not yet implemented")
    }

    override fun startHomeMenuActivity() {
        TODO("Not yet implemented")
    }
}
