package com.orienteering.handrail.utilities

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.widget.Toast
import com.orienteering.handrail.activities.WelcomeActivity

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
        App.context?.startActivity(intent)


    }

}