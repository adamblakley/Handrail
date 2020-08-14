package com.orienteering.handrail.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.orienteering.handrail.R
import com.orienteering.handrail.activities.HomeActivity
import com.orienteering.handrail.interactors.LoginInteractor

class LoginActivity : AppCompatActivity(), ILoginContract.ILoginView {
    // Tag for class log
    val TAG : String = "LoginActivity"

    lateinit var loginPerformer : LoginPerformer

    /**
     * edit text and button attributes
     */
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button

    var userEmail : String = ""
    var userPassword : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        createEditText()
        createButtons()

        loginPerformer = LoginPerformer(this, LoginInteractor())
    }

    override fun createEditText() {
        emailEditText = findViewById(R.id.editText_email_login)
        passwordEditText = findViewById(R.id.editText_password_login)

        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { userEmail = emailEditText.text.toString() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { userPassword = passwordEditText.text.toString() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun createButtons() {
        loginButton = findViewById(R.id.button_login)
        loginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                loginPerformer.requestDataFromServer(userEmail,userPassword)
            }
        })
    }

    override fun makeToast(message: String) {
        val toast : Toast = Toast.makeText(this@LoginActivity,message, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun startHomeMenuActivity() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {}
        startActivity(intent)
    }

    override fun onResponseFailure(throwable: Throwable) {
        val toast : Toast = Toast.makeText(this@LoginActivity,"Error: Connection unavailable, please try again later",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseError() {
        val toast : Toast = Toast.makeText(this@LoginActivity,"User email or password incorrect", Toast.LENGTH_SHORT)
        toast.show()
        emailEditText.setError("Please check email")
        passwordEditText.setError("Please check password")
    }

    override fun onDestroy(){
        super.onDestroy()
        loginPerformer.onDestroy()
    }
}