package com.orienteering.handrail.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.orienteering.handrail.R
import com.orienteering.handrail.classes.PasswordUpdateRequest
import com.orienteering.handrail.classes.User
import com.orienteering.handrail.controllers.UserController
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.utilities.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity for password uddate of user
 *
 */
class PasswordActivity : AppCompatActivity() {

    // Tag for log
    val TAG = "PasswordActivity"
    // new password input
    lateinit var editTextNewPassword : EditText
    // confirm new password input
    lateinit var editTextConfirmPassword : EditText
    // current password input
    lateinit var editTextCurrentPassword : EditText
    // button for submit password change request
    lateinit var buttonSubmitPassword : Button
    // user controller controls user service
    val userController = UserController()

    /**
     * callback handle password update
     */
    val updatePasswordCallback = object : Callback<StatusResponseEntity<User>?> {
        override fun onFailure(call: Call<StatusResponseEntity<User>?>, t: Throwable) {
            Log.e(TAG, "Failure connecting to service")
            Log.e(TAG,"Error: Service Currently Unavailable")
            val toast : Toast = Toast.makeText(this@PasswordActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
            toast.show()
        }
        override fun onResponse(
            call: Call<StatusResponseEntity<User>?>,
            response: Response<StatusResponseEntity<User>?>
        ) {
            if (response.isSuccessful){
                Log.e(TAG,"Success Updating Password")
                val toast : Toast = Toast.makeText(this@PasswordActivity,"Successfully Updated.",Toast.LENGTH_SHORT)
                val intent : Intent = Intent(this@PasswordActivity,HomeActivity::class.java)
                startActivity(intent)
                toast.show()
            } else  if (response.code()==401){
                Log.e(TAG, "Incorrect Password.")
                val toast : Toast = Toast.makeText(this@PasswordActivity,"Incorrect Password.",Toast.LENGTH_SHORT)
                editTextCurrentPassword.setError("Enter your current password")
                toast.show()
            } else {
                Log.e(TAG, "Service Error")
                val toast : Toast = Toast.makeText(this@PasswordActivity,"Error: Service unavailable. Please try again later.",Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }


    /**
     * Initialise view
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

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
                    updatePassword()
                } else {
                    val toast = Toast.makeText(this@PasswordActivity, "Please check all fields",Toast.LENGTH_SHORT)
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
        if (editTextNewPassword.text.toString().trim().length < 8 || editTextNewPassword.text.toString().trim().length > 16 || editTextNewPassword.text.toString()!=editTextNewPassword.text.toString()) {
            editTextConfirmPassword.setError("Password doesn't match")
            inputsOk = false
        }
        if (editTextCurrentPassword.text.toString().trim().length <= 8) {
            editTextCurrentPassword.setError("Enter your current password, must be a 8-16 characters long")
            inputsOk = false
        }

        return  inputsOk
    }

    fun updatePassword(){
        val passwordUpdateRequest : PasswordUpdateRequest = PasswordUpdateRequest(editTextCurrentPassword.text.toString(),editTextNewPassword.text.toString())
        userController.updatePassword(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),passwordUpdateRequest,updatePasswordCallback)
    }
}
