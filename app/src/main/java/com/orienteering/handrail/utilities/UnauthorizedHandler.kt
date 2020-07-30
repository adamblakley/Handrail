package com.orienteering.handrail.utilities

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.orienteering.handrail.activities.WelcomeActivity

/**
 * handles unauthorized access
 *
 * @constructor
 * TODO
 *
 * @param context
 */
class UnauthorizedHandler(context: Context) {

    /**
     * context of activity
     */
    var context: Context

    init{
        this.context=context
    }

    /**
     * redirect to applicable activity
     *
     */
    fun redirect(){
        val toast : Toast = Toast.makeText(context,"Unauthorized Access. Please login",Toast.LENGTH_SHORT)
        toast.show()
        val intent : Intent = Intent(context,WelcomeActivity::class.java)
        context.startActivity(intent)
    }

}