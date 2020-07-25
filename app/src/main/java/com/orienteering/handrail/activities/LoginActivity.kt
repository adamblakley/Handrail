package com.orienteering.handrail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.orienteering.handrail.R
import com.orienteering.handrail.controllers.LoginController
import com.orienteering.handrail.httprequests.LoginRequest
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    // Tag for class log
    val TAG : String = "LoginActivity"

    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button

    val loginController : LoginController = LoginController()

    lateinit var userEmail : String
    lateinit var userPassword : String

    private val callbackLogin = object :
        Callback<StatusResponseEntity<LoginResponse>> {
        override fun onFailure(call: Call<StatusResponseEntity<LoginResponse>?>, t: Throwable) {
            Log.e(TAG, "Failure logging in")
        }

        override fun onResponse(
            call: Call<StatusResponseEntity<LoginResponse>?>,
            response: Response<StatusResponseEntity<LoginResponse>?>
        ) {
            val loginResponse = response.body()
            Log.e(TAG,loginResponse.toString())
            Log.e(TAG, "Success logging in")
        }
    }

    /**
     * called on creation, set's up view and elements
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        createEditText()
        createButtons()
    }

    /**
     * Creates edit text and functionality
     *
     */
    fun createEditText(){
        emailEditText = findViewById(R.id.editText_email_login)
        passwordEditText = findViewById(R.id.editText_password_login)

        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userEmail = emailEditText.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userPassword = passwordEditText.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    /**
     * Creates login button and onclick
     *
     */
    fun createButtons(){
        loginButton = findViewById(R.id.button_login)
        loginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                login()
            }
        })
    }

    /**
     * retrieves login information, request login
     *
     */
    fun login(){
        if (userEmail!=null || userPassword!=null){
            val loginRequest = LoginRequest(userEmail,userPassword)
            loginController.login(loginRequest,callbackLogin)
        } else {
            Log.e(TAG,"Email or Password empty")
        }
    }
}
