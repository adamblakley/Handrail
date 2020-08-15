package com.orienteering.handrail.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.orienteering.handrail.R
import com.orienteering.handrail.home_menu.HomeActivity
import com.orienteering.handrail.interactors.LoginInteractor
import com.orienteering.handrail.login.LoginActivity
import com.orienteering.handrail.signup.SignupActivity

class WelcomeActivity: AppCompatActivity(), IWelcomeContract.IWelcomeView {

    private  val TAG = "WelcomeActivity"
    lateinit var welcomePerformer : IWelcomeContract.IWelcomePerformer

    lateinit var btnLogin : Button
    lateinit var btnSignup : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        createButtons()
        welcomePerformer = WelcomePerformer(this,LoginInteractor())
        welcomePerformer.checkLogin()
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

    override fun onResponseSuccess() {
            val intent = Intent(this@WelcomeActivity, HomeActivity::class.java).apply {}
            startActivity(intent)
    }

    override fun onResponseFailure(throwable: Throwable) {
        Toast.makeText(this@WelcomeActivity, "Error: Service currently unavailable", Toast.LENGTH_SHORT).show()
    }

    override fun onResponseError() {
        Toast.makeText(this@WelcomeActivity, "Please login or sign up for an account", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy(){
        super.onDestroy()
        welcomePerformer.onDestroy()
    }
}