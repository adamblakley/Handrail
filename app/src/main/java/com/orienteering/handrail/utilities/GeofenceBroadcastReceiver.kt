package com.orienteering.handrail.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.orienteering.handrail.activities.CourseParticipationActivity

class GeofenceBroadcastReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.e(TAG, "I've noticed a transition")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)



        if (geofencingEvent.hasError()){
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage+"geofence transition not working")
            return
        }

        for (triggeringGeofence in geofencingEvent.triggeringGeofences) {
            Toast.makeText(context,"Control Recorded", Toast.LENGTH_SHORT).show()
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            CourseParticipationActivity.getInstance().triggerGeofence()
        } else {
            Toast.makeText(context,"Error in transition", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "error in transition")
        }


    }

}



private const val TAG = "GeofenceReceiver"