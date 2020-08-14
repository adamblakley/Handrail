package com.orienteering.handrail.utilities

import android.content.Context
import android.location.Location
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.maps.model.LatLng
import kotlin.collections.ArrayList


/**
 * Returns the error string for a geofencing error code.
 */
fun errorMessage(context: Context, errorCode: Int): String {
    val resources = context.resources
    return when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "Geofence not available"
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Too many geofences"
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Too many pending intents"
        else -> "Unknown Error"
    }
}

data class ControlDataObject(val id: Int, val note: String, val name: String, var time: Long?, val latLong: LatLng, var altitude : Double?, var completed : Boolean)

data class ParticipantPerformance (val id: Int, var controldata : Array<ControlDataObject>)

internal object GeofencingConstants {

    val locationControls = ArrayList<Location>()

    const val GEOFENCE_RADIUS_IN_METERS = 20f
    const val GEOFENCE_LOITERING_DELAY_IN_MS = 100
    const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
}