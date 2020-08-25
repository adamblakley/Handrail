package com.orienteering.handrail.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.orienteering.handrail.R
import com.orienteering.handrail.home_menu.HomeActivity
import com.orienteering.handrail.interactors.LoginInteractor

// TAG for Logs
private val TAG: String = LoginActivity::class.java.name

/**
 * Class responsibile for displaying login user interface and binding interactions to events
 *
 */
class LoginActivity : AppCompatActivity(), ILoginContract.ILoginView {

    // presenter handles login activities
    lateinit var loginPresenter : LoginPresenter

    /**
     * edit text and button attributes
     */
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button

    /**
     * Strings for login
     */
    var userEmail : String = ""
    var userPassword : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        createEditText()
        createButtons()

        loginPresenter = LoginPresenter(this, LoginInteractor())
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
                loginPresenter.requestDataFromServer(userEmail,userPassword)
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
        finish()
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
        loginPresenter.onDestroy()
    }
}