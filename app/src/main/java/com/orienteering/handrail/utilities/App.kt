package com.orienteering.handrail.utilities

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

/**
 * Class holds app context and global variables
 *
 */
class App : Application() {

    /**
     * Static variables for application
     */
    companion object AppCompanion{
        // context of app
        var context : Context? = null
        // shared preferences instance for storing information
        lateinit var sharedPreferences : SharedPreferences
        // editor for shared preferences for inputting or removing information
        lateinit var sharedPreferencesEditor : SharedPreferences.Editor
        // auth token title string
        const val SharedPreferencesAuthToken : String = "AUTH_TOKEN"
        // token type title string
        const val SharedPreferencesTokenType : String = "TOKEN_TYPE"
        // user id title string
        const val SharedPreferencesUserId : String = "USER_ID"
        // radius of globe in km
        const val GLOBAL_RADIUS = 6371
    }


    /**
     * Initialise context and shared preference variables
     *
     */
    override fun onCreate() {
        super.onCreate()
        context=getApplicationContext()
        sharedPreferences  = this.getSharedPreferences("SHARED_PREFERENCES", AppCompatActivity.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
    }

}