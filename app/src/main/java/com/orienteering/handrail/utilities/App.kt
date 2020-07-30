package com.orienteering.handrail.utilities

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class App : Application() {

    companion object AppCompanion{
        var context : Context? = null
        lateinit var sharedPreferences : SharedPreferences
        lateinit var sharedPreferencesEditor : SharedPreferences.Editor
        const val SharedPreferencesAuthToken : String = "AUTH_TOKEN"
        const val SharedPreferencesTokenType : String = "TOKEN_TYPE"
    }


    override fun onCreate() {
        super.onCreate()
        context=getApplicationContext()
        sharedPreferences  = this.getSharedPreferences("SHARED_PREFERENCES",
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferencesEditor = sharedPreferences.edit()
    }

}