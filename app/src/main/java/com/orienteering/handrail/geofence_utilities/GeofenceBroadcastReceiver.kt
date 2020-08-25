package com.orienteering.handrail.geofence_utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.orienteering.handrail.course_participation.CourseParticipationActivity

/**
 * Broadcast receivever listens for geofence messages in intents
 *
 */
class GeofenceBroadcastReceiver() : BroadcastReceiver() {

    /**
     * overridden onReceive method, applies methods on successful or failure geofence interaction
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context, intent: Intent) {

        // Geofencing event received from intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)


        if (geofencingEvent.hasError()){
            // display message if geofence error occurs
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage+"geofence transition not working")
            return
        }
        // check each triggered geofence and respond with message notifying of success
        for (triggeringGeofence in geofencingEvent.triggeringGeofences) {
            Toast.makeText(context,"Control Recorded", Toast.LENGTH_SHORT).show()
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        // trigger geofence method on geofence entry
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            CourseParticipationActivity.getInstance().triggerGeofence()
        } else {
            // handle failures
            Toast.makeText(context,"Error: Geofence transition failure", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "error in transition")
        }


    }

}



private const val TAG = "GeofenceReceiver"