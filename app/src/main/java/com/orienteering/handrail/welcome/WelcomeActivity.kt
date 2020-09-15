package com.orienteering.handrail.welcome

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.orienteering.handrail.R
import com.orienteering.handrail.home_menu.HomeActivity
import com.orienteering.handrail.interactors.LoginInteractor
import com.orienteering.handrail.signup.SignupActivity

/**
 * Class handles the user interface and user input for the welcome screen
 *
 */
class WelcomeActivity: AppCompatActivity(), IWelcomeContract.IWelcomeView {

    // presenter checks login of user
    private lateinit var welcomePresenter : IWelcomeContract.IWelcomePresenter
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    private lateinit var btnLogin : Button
    private lateinit var btnSignup : Button
    // progress dialog for web queries
    lateinit var progressDialog : ProgressDialog
    // handler delay web query dialog
    val handler : Handler = Handler();

    /**
     * Strings for login
     */
    var userEmail : String = ""
    var userPassword : String = ""

    /**
     * Initiate buttons and view, check user login
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        if (intent.extras!=null) {
            Toast.makeText(this@WelcomeActivity,"Access Denied: Please login with correct email address and password",Toast.LENGTH_SHORT).show()
        }
        createButtons()
        createEditText()
        welcomePresenter = WelcomePresenter(this,LoginInteractor())
        welcomePresenter.checkLogin()
    }

    /**
     * Initiate buttons, set onclick listener
     *
     */
    fun createButtons(){
        btnLogin = findViewById(R.id.btn_launch_login)
        btnSignup = findViewById(R.id.btn_launch_signup)
        progressDialog = ProgressDialog(this@WelcomeActivity)
        progressDialog.setCancelable(false)
        btnLogin.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if (checkFields()){
                    progressDialog.setMessage("Logging In...")
                    progressDialog.show()
                    welcomePresenter.requestDataFromServer(userEmail,userPassword)
                }
            }
        })
        btnSignup.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@WelcomeActivity, SignupActivity::class.java).apply {}
                startActivity(intent)
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
            Toast.makeText(this@WelcomeActivity,"Please check login fields",Toast.LENGTH_SHORT).show()
        }
        return inputsOk
    }

    fun createEditText() {
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

    /**
     * Initiate homeactivity on successful login
     *
     */
    override fun onResponseSuccess() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val intent = Intent(this@WelcomeActivity, HomeActivity::class.java).apply {}
        startActivity(intent)
    }

    override fun onResponseFailure(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        Toast.makeText(this@WelcomeActivity, "Error: Service currently unavailable", Toast.LENGTH_SHORT).show()
    }

    /**
     * Login failure message
     *
     */
    override fun onResponseError() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
    }

    override fun makeToast(message: String) {
        val toast : Toast = Toast.makeText(this@WelcomeActivity,message, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun startHomeMenuActivity() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val intent = Intent(this@WelcomeActivity, HomeActivity::class.java).apply {}
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResponseIncorrect() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast : Toast = Toast.makeText(this@WelcomeActivity,"Error: Email or Password not found, please check your input and try again",Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    override fun onResponseFailureLogin(throwable: Throwable) {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast : Toast = Toast.makeText(this@WelcomeActivity,"Error: Connection unavailable, please try again later",Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    override fun onResponseErrorLogin() {
        handler.postDelayed(Runnable() { run() { progressDialog.dismiss() } },500);
        val toast : Toast = Toast.makeText(this@WelcomeActivity,"User email or password incorrect", Toast.LENGTH_SHORT)
        toast.show()
        emailEditText.setError("Please check email")
        passwordEditText.setError("Please check password")
        finish()
    }

    override fun onDestroy(){
        super.onDestroy()
        welcomePresenter.onDestroy()
    }
}