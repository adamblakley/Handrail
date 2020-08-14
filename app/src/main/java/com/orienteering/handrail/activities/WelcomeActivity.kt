package com.orienteering.handrail.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.orienteering.handrail.R
import com.orienteering.handrail.controllers.LoginController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.login.LoginActivity
import com.orienteering.handrail.signup.SignupActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeActivity : AppCompatActivity() {

    private  val TAG = "WelcomeActivity"

    lateinit var btnLogin : Button
    lateinit var btnSignup : Button

    val loginController = LoginController()

    /**
     * callback response login
     */
    private val callbackLogin = object :
        Callback<StatusResponseEntity<Boolean>> {
        override fun onFailure(call: Call<StatusResponseEntity<Boolean>>, t: Throwable) {
            Log.e(TAG, "Failure checking user login")
        }

        override fun onResponse(
            call: Call<StatusResponseEntity<Boolean>>,
            response: Response<StatusResponseEntity<Boolean>>
        ) {
            if (response.isSuccessful){
                val loginResponse = response.body()?.entity
                if (loginResponse!!){
                    Log.e(TAG, "User already logged in")
                    val intent = Intent(this@WelcomeActivity, HomeActivity::class.java).apply {}
                    startActivity(intent)
                }
            } else {
                Log.e(TAG, "Unsuccessful login check response")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        checkLogin()
        createButtons()
    }

    fun checkLogin(){
        loginController.checkLogin(callbackLogin)
    }

    fun createButtons(){
        btnLogin = findViewById(R.id.btn_launch_login)
        btnSignup = findViewById(R.id.btn_launch_signup)

        btnLogin.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java).apply {}
                startActivity(intent)
            }
        })
        btnSignup.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@WelcomeActivity, SignupActivity::class.java).apply {}
                startActivity(intent)
            }
        })
    }
}
