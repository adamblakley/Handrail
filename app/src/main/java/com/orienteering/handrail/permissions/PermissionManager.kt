package com.orienteering.handrail.permissions

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

// tag for logs
private val TAG = PermissionManager::class.qualifiedName

/**
 * Class responsible for requesting permissions
 *
 */
class PermissionManager  {

    /**
     * Hold all static methods and values
     */
    companion object{
        const val MULTIPLE_REQUEST_CODES = 0
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_CHECK_SETTINGS = 2
        const val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 3
        const val BACKGROUND_PERMISSION_REQUEST_CODE = 4
        const val CAMERA_PERMISSION_CODE = 5

        /**
         * Check permission is granted, if not handle via grantPermission method
         * return boolean value on permission request success or failure
         * @param activity
         * @param context
         * @param permissions
         * @param requestCode
         * @return
         */
    fun checkPermission(activity : Activity, context : Context, permissions : Array<String>, requestCode : Int) : Boolean{

        Log.e(TAG, "Checking Permission")
        var permissionsNotGranted  = arrayListOf<String>()
            // check package manager that permission has been granted, add to list if not granted
        for (permission in permissions){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG,"Permission not granted $permission")
                permissionsNotGranted.add(permission)
            }
        }
        // iterate through list of ungranted permissions, response of granted permission is false
        if (permissionsNotGranted.size>=1){
            return grantPermission(activity, context, permissionsNotGranted, requestCode)
        } else{
            // all permissions granted return true
            return true
        }

    }

        /**
         * use activitycompat to request permissions, return result
         *
         * @param activity
         * @param context
         * @param permissions
         * @param requestCode
         * @return
         */
        fun grantPermission(activity : Activity, context: Context, permissions: ArrayList<String>, requestCode: Int) : Boolean{
        Log.e(TAG, "Requesting Permission")
            // request each permission inside arraylist of permissions
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestCode)
            // handle permission, return boolean value
         return handlePermissionResult(context, permissions)
    }


        /**
         * Handles all permission request responses
         *
         * @param activity
         * @param context
         * @param permissions
         * @param requestCode
         * @return
         */
        fun handlePermissionResult(context: Context, permissions: ArrayList<String>) : Boolean{
        Log.e(TAG, "Handling Permission")
        var permissionGranted : Boolean = true
        // check if permission has been granted for each permission
        for (permission in permissions){
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                // if false, permission granted = false, break and inform user
                permissionGranted = false
                break
            }
        }
        if (permissionGranted==false){
            Toast.makeText(context,"You may be unable to user certain app features. Please grant permissions in system settings",Toast.LENGTH_SHORT).show()
        }
            // if true, return to calling method to return
        return permissionGranted
    }
    }
}