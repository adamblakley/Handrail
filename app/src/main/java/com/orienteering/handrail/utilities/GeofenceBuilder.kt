package com.orienteering.handrail.utilities

import android.graphics.Color
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.orienteering.handrail.utilities.GeofencingConstants.GEOFENCE_LOITERING_DELAY_IN_MS
import com.orienteering.handrail.utilities.GeofencingConstants.GEOFENCE_RADIUS_IN_METERS

class GeofenceBuilder {

    var geofenceID : Int = 0

    /**
     * Method to create Geofence
     * Returns GeofencingRequest
     */
    fun addGeofence(latLng: LatLng) : GeofencingRequest{

        val geofence = Geofence.Builder()
            .setRequestId(geofenceID.toString())
            .setCircularRegion(latLng.latitude,latLng.longitude, GEOFENCE_RADIUS_IN_METERS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(GEOFENCE_LOITERING_DELAY_IN_MS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val geofencingRequest = GeofencingRequest.Builder().apply{
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()

        geofenceID++
        return geofencingRequest
    }

    /**
     * Method to draw geofence on map
     * Returns CircleOptions
     */
    fun drawGeofence(latLng: LatLng) : CircleOptions{
        Log.e(TAG,"drawing geofence")
        var circleOptions : CircleOptions = CircleOptions()
            .center(latLng)
            .strokeColor(Color.argb(50,70,70,70))
            .fillColor(Color.argb(100,150,150,150))
            .radius(20.0)

        return circleOptions
    }
}

private const val TAG = "GeofenceBuilder"