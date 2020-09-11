package com.orienteering.handrail.httprequests

import android.content.Intent
import com.orienteering.handrail.utilities.App

/**
 * handles unauthorized access
 *
 */
class UnauthorizedHandler() {

    /**
     * redirect to applicable activity
     *
     */
    fun redirect(){
        val intent : Intent = Intent(App.context,com.orienteering.handrail.welcome.WelcomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("origin",false)
        App.context?.startActivity(intent)
    }
}