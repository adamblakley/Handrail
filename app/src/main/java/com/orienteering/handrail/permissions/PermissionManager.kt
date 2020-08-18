package com.orienteering.handrail.permissions

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

class PermissionManager  {

    private val TAG = PermissionManager::class.qualifiedName

    companion object{
        const val MULTIPLE_REQUEST_CODES = 0
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_CHECK_SETTINGS = 2
        const val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 3
        const val BACKGROUND_PERMISSION_REQUEST_CODE = 4
        const val CAMERA_PERMISSION_CODE = 5


    fun checkPermission(activity : Activity, context : Context, permissions : Array<String>, requestCode : Int) : Boolean{

        Log.e(TAG, "Checking Permission")
        var permissionsNotGranted  = arrayListOf<String>()

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

        if (permissionsNotGranted.size>=1){
            return grantPermission(
                activity,
                context,
                permissionsNotGranted,
                requestCode
            )
        } else{
            return true
        }

    }

    fun grantPermission(activity : Activity, context: Context, permissions: ArrayList<String>, requestCode: Int) : Boolean{
        Log.e(TAG, "Requesting Permission")
        ActivityCompat.requestPermissions(activity,
            permissions.toTypedArray(), requestCode)

         return handlePermissionResult(
             activity,
             context,
             permissions,
             requestCode
         )
    }



    fun handlePermissionResult(activity : Activity, context: Context, permissions: ArrayList<String>, requestCode: Int) : Boolean{
        Log.e(TAG, "Handling Permission")
        var permissionGranted : Boolean = true

        for (permission in permissions){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ){
                permissionGranted = false
                break
            }
        }

        if (permissionGranted==false){
            //showPermissionDeniedMessage()
        }
        return permissionGranted
    }

    /*
    fun showPermissionDeniedMessage(view : View){
        val snackbar : Snackbar = Snackbar.make(view,"You denied a permission request. Please provide permission access within your Settings to enable App features", Snackbar.LENGTH_SHORT)
    }

     */
    }
}