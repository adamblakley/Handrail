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

class PasswordUpdateActivity : AppCompatActivity(), IPasswordUpdateContract.IPasswordUpdateView {

    // Tag for log
    val TAG = "PasswordActivity"

    lateinit var passwordUpdatePerformer : IPasswordUpdateContract.IPasswordUpdatePerformer

    // new password input
    lateinit var editTextNewPassword : EditText
    // confirm new password input
    lateinit var editTextConfirmPassword : EditText
    // current password input
    lateinit var editTextCurrentPassword : EditText
    // button for submit password change request
    lateinit var buttonSubmitPassword : Button

    /**
     * Initialise view
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        passwordUpdatePerformer = PasswordUpdatePerformer(this, UserInteractor())

        createText()
        createButtons()
    }

    /**
     * Initialises text input
     *
     */
    fun createText(){
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
                    val passwordUpdateRequest : PasswordUpdateRequest = PasswordUpdateRequest(editTextCurrentPassword.text.toString(),editTextNewPassword.text.toString())
                    passwordUpdatePerformer.putDataToServer(passwordUpdateRequest)
                } else {
                    val toast = Toast.makeText(this@PasswordUpdateActivity, "Please check all fields",
                        Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        })
    }

    fun checkFields() : Boolean{
        var inputsOk : Boolean = true

        if (editTextNewPassword.text.toString().trim().length < 8 || editTextNewPassword.text.toString().trim().length > 16) {
            editTextNewPassword.setError("Enter a new password, must be a 8-16 characters long")
            inputsOk = false
        }
        if (editTextConfirmPassword.text.toString().trim().length < 8 || editTextConfirmPassword.text.toString().trim().length > 16 && editTextNewPassword.text.toString()!=editTextConfirmPassword.text.toString()) {
            editTextConfirmPassword.setError("Password doesn't match")
            inputsOk = false
        }
        if (editTextCurrentPassword.text.toString().trim().length < 8) {
            editTextCurrentPassword.setError("Enter your current password, must be a 8-16 characters long")
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
        editTextCurrentPassword.setError("Enter your current password")
    }

    override fun onResponseSuccess() {
        Log.i(TAG, "Success")
        Toast.makeText(this@PasswordUpdateActivity,"Successfully Updated.",Toast.LENGTH_SHORT).show()
        val intent : Intent = Intent(this@PasswordUpdateActivity, HomeActivity::class.java)
        startActivity(intent)
    }
}