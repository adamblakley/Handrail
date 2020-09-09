package com.orienteering.handrail.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();

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
        progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.setCancelable(false)
        loginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (checkFields()){
                    progressDialog.setMessage("Logging In...")
                    progressDialog.show()
                    loginPresenter.requestDataFromServer(userEmail,userPassword)
                }
            }
        })
    }

    fun checkFields(): Boolean {
        var inputsOk : Boolean = true
        if (emailEditText.text.toString().trim().length<=0){
            emailEditText.setError("Enter an email address")
            inputsOk=false
        }
        if (passwordEditText.text.toString().trim().length<=0){
            passwordEditText.setError("Enter a password")
            inputsOk=false
        }
        if (!inputsOk){
            Toast.makeText(this@LoginActivity,"Please check login fields",Toast.LENGTH_SHORT).show()
        }
        return inputsOk
    }

    override fun makeToast(message: String) {
        val toast : Toast = Toast.makeText(this@LoginActivity,message, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun startHomeMenuActivity() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {}
        startActivity(intent)
        finish()
    }

    override fun onResponseIncorrect() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast : Toast = Toast.makeText(this@LoginActivity,"Error: Email or Password not found, please check your input and try again",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseFailure(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast : Toast = Toast.makeText(this@LoginActivity,"Error: Connection unavailable, please try again later",Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
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