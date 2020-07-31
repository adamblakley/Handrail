package com.orienteering.handrail.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.orienteering.handrail.controllers.LoginController
import com.orienteering.handrail.httprequests.LoginRequest
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Acitivity manges login
 *
 */
class LoginActivity : AppCompatActivity() {

    // Tag for class log
    val TAG : String = "LoginActivity"

    /**
     * edit text and button attributes
     */
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button

    /**
     * login controller manages login service
     */
    val loginController : LoginController = LoginController()

    /**
     * user email and user password, input user
     */
    lateinit var userEmail : String
    lateinit var userPassword : String

    /**
     * Shared preferences variable
     */
    lateinit var sharedPreferences: SharedPreferences

    /**
     * callback response login
     */
    private val callbackLogin = object :
        Callback<StatusResponseEntity<LoginResponse>> {
        override fun onFailure(call: Call<StatusResponseEntity<LoginResponse>>, t: Throwable) {
            Log.e(TAG, "Failure connecting to log in service")
            val toast : Toast = Toast.makeText(this@LoginActivity,"Connection unavailable, please try again later",Toast.LENGTH_SHORT)
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<LoginResponse>>,
            response: Response<StatusResponseEntity<LoginResponse>>
        ) {
            if (response.code()== 401){
                Log.e(TAG, "User email or password incorrect")
                val toast : Toast = Toast.makeText(this@LoginActivity,"User email or password incorrect",Toast.LENGTH_SHORT)
                toast.show()
            } else if (response.isSuccessful){
                val loginResponse : LoginResponse? = response.body()?.entity
                if (loginResponse!=null){
                    insertSharedPreferences(loginResponse.accessToken,loginResponse.tokenType,loginResponse.userId)
                    Log.e(TAG, "Success logging in")
                    val toast : Toast = Toast.makeText(this@LoginActivity,"Success logging in",Toast.LENGTH_SHORT)
                    toast.show()
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {}
                    startActivity(intent)
                }
            } else {
                Log.e(TAG, "Error logging in")
                val toast : Toast = Toast.makeText(this@LoginActivity,"Service unavailable, please try again later",Toast.LENGTH_SHORT)
                toast.show()
            }
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

        sharedPreferences = App.sharedPreferences
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

    /**
     * get authentication token from SharedPreferences
     *
     * @return
     */
    fun getSharedPreferences() : String? {
        return sharedPreferences.getString(App.SharedPreferencesAuthToken,"")
    }

    /**
     * Insert authentication token into SharedPreferences
     *
     * @param authToken
     * @param tokenType
     */
    fun insertSharedPreferences(authToken : String, tokenType : String,userId : Long){
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(App.SharedPreferencesAuthToken,authToken).commit()
        sharedPreferencesEditor.putString(App.SharedPreferencesTokenType,tokenType).commit()
        sharedPreferencesEditor.putLong(App.SharedPreferencesUserId,userId).commit()
    }
}
