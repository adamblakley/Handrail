package com.orienteering.handrail.utilities

import android.app.Application
import android.content.Context

class App : Application() {

    companion object AppCompanion{
        var context : Context? = null
    }


    override fun onCreate() {
        super.onCreate()
        context=getApplicationContext()
    }
}