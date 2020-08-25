package com.orienteering.handrail.password_update

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.orienteering.handrail.R
import com.orienteering.handrail.home_menu.HomeActivity
import com.orienteering.handrail.interactors.UserInteractor
import com.orienteering.handrail.models.PasswordUpdateRequest

// TAG for Logs
private val TAG: String = PasswordUpdateActivity::class.java.name


/**
 * Handles all user interface elements and initiation of events in password update use case
 *
 */
class PasswordUpdateActivity : AppCompatActivity(), IPasswordUpdateContract.IPasswordUpdateView {

    lateinit var passwordUpdatePresenter : IPasswordUpdateContract.IPasswordUpdatePresenter

    // new password input
    lateinit var editTextNewPassword : EditText
    // confirm new password input
    private lateinit var editTextConfirmPassword : EditText
    // current password input
    lateinit var editTextCurrentPassword : EditText
    // button for submit password change request
    private lateinit var buttonSubmitPassword : Button

    /**
     * Initialise view
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        passwordUpdatePresenter = PasswordUpdatePresenter(this, UserInteractor())

        createText()
        createButtons()
    }

    /**
     * Initialises text input
     *
     */
    private fun createText(){
        editTextNewPassword=findViewById(R.id.editText_new_password_password)
        editTextConfirmPassword=findViewById(R.id.editText_password_confirm_password)
        editTextCurrentPassword=findViewById(R.id.editText_password_current_password)
    }

    /**
     * Initialises buttons
     *
     */
    fun createButtons(){
        buttonSubmitPassword=findViewById(R.id.button_password_submit)

        buttonSubmitPassword.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (checkFields()){
                    val passwordUpdateRequest = PasswordUpdateRequest(editTextCurrentPassword.text.toString(),editTextNewPassword.text.toString())
                    passwordUpdatePresenter.putDataToServer(passwordUpdateRequest)
                } else {
                    val toast = Toast.makeText(this@PasswordUpdateActivity, "Please check all fields",
                        Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        })
    }

    /**
     * Check validity of all user input fields, provide response
     *
     * @return
     */
    fun checkFields() : Boolean{
        var inputsOk = true

        if (editTextNewPassword.text.toString().trim().length < 8 || editTextNewPassword.text.toString().trim().length > 16) {
            editTextNewPassword.error = "Enter a new password, must be a 8-16 characters long"
            inputsOk = false
        }
        if (editTextConfirmPassword.text.toString().trim().length < 8 || editTextConfirmPassword.text.toString().trim().length > 16 && editTextNewPassword.text.toString()!=editTextConfirmPassword.text.toString()) {
            editTextConfirmPassword.error = "Password doesn't match"
            inputsOk = false
        }
        if (editTextCurrentPassword.text.toString().trim().length < 8) {
            editTextCurrentPassword.error = "Enter your current password, must be a 8-16 characters long"
            inputsOk = false
        }
        return  inputsOk
    }

    override fun onResponseFailure(throwable: Throwable) {
        Log.e(TAG, "Error: Service Failure")
        Toast.makeText(this@PasswordUpdateActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT).show()
    }

    override fun onResponseError() {
        Log.e(TAG, "Error: Service Error")
        Toast.makeText(this@PasswordUpdateActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT).show()
    }

    override fun onResponsePasswordError() {
        Log.e(TAG, "Error: Incorrect Password")
        Toast.makeText(this@PasswordUpdateActivity,"Error: Incorrect Password.",Toast.LENGTH_SHORT).show()
        editTextCurrentPassword.error = "Enter your current password"
    }

    override fun onResponseSuccess() {
        Log.i(TAG, "Success")
        Toast.makeText(this@PasswordUpdateActivity,"Successfully Updated.",Toast.LENGTH_SHORT).show()
        val intent = Intent(this@PasswordUpdateActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}