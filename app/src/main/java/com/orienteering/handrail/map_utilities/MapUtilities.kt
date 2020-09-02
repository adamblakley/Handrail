package com.orienteering.handrail.map_utilities

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.utilities.App.AppCompanion.GLOBAL_RADIUS
import kotlin.math.*

class MapUtilities {

    /**
     * Calculate most southwesterly latitude and longitude and most northeasterly latitude and longitude
     * @param latlngs
     * @return
     */
    fun determineNESW(latlngs : MutableList<LatLng>) : LatLngBounds {
        var latNE : Double? = null
        var latSW : Double? = null
        var lngNE : Double? = null
        var lngSW : Double? = null

        // fill the first latitude and longitude values from the first latlng provided
        for (latlng in latlngs){
            if (latNE==null||latSW==null||lngNE==null||lngSW==null){
                latSW = latlng.latitude
                latNE  = latlng.latitude
                lngSW = latlng.longitude
                lngNE = latlng.longitude
            } else {
                // if the next latitude and longitude is more northeasterly or southwesterly update latNE, lngNE, latSW and lngSW
                if (latlng.latitude > latNE) latNE = latlng.latitude;
                if (latlng.latitude < latSW) latSW = latlng.latitude;
                if (latlng.longitude > lngNE) lngNE = latlng.longitude;
                if (latlng.longitude < lngSW) lngSW = latlng.longitude;
            }
        }
        return LatLngBounds(LatLng(latSW!!, lngSW!!), LatLng(latNE!!,lngNE!!))
    }

    fun getAllParticipantRoutePoints(participants: List<Participant>) : MutableList<LatLng> {
        var allLatLngs : MutableList<LatLng> = mutableListOf<LatLng>()
        for (participant in participants){
            for (routePoint in participant.routePoints){
                routePoint.createLatLng()
                allLatLngs.add(routePoint.latlng)
            }
        }
        return allLatLngs
    }

    fun getAllParticipantRoutePoints(participant:Participant) : MutableList<LatLng> {
        var allLatLngs : MutableList<LatLng> = mutableListOf<LatLng>()
        for (routePoint in participant.routePoints){
            routePoint.createLatLng()
            allLatLngs.add(routePoint.latlng)
        }
        return allLatLngs
    }

    fun getAllControlPoints(controls : List<Control>) : MutableList<LatLng>{
        var allLatLngs : MutableList<LatLng> = mutableListOf()
        for (control in controls){
            control.createLatLng()
            allLatLngs.add(control.controlLatLng)
        }
        return allLatLngs
    }


    /**
     * Calculate pace of performance by dividing distance by time
     * @param time
     * @param distance
     * @return
     */
    fun calculatePace(time : Long, distance : Double) : Double{
        return distance/(time/60) * 1000
    }

    /**
     * Calculate total distance via the haversine formula
     * @param latlngs
     * @return
     */
    fun calculateTotalDistance(latlngs : MutableList<LatLng>) : Double {
        var totalDistance : Double = 0.0

        if (latlngs.size<=1){
            return 0.0
        }

        // iterate through list of latitude and longitude, stopping at preantepenultimate to prevent index out of bounds error
        for (currentLatLng in 0..latlngs.size-2){

            // calculate lat and long differences, subtract current latitude and longitude values from the next latlng value in the list
            val lngDistance = Math.toRadians(latlngs[currentLatLng+1].longitude - latlngs[currentLatLng].longitude)
            val latDistance = Math.toRadians(latlngs[currentLatLng+1].latitude - latlngs[currentLatLng].latitude)
            // utilize Haversine forumla to calculate total distance between two points on a sphere (globe)
            // Angle a of triangle - find sine value of latitude distance/2 squared
            val a: Double = sin(latDistance / 2) * sin(latDistance / 2) +
            // add to the cosine value of the radian value of the current latitude * the radian value of the next latitude
            // multiplied by the sine value of the lngdistance/2 squared
            // Angle b of triangle
                    cos(Math.toRadians(latlngs[currentLatLng].latitude)) * cos(Math.toRadians(latlngs[currentLatLng+1].latitude))*
                    sin(lngDistance / 2) * sin(lngDistance / 2)

            // determine the value of 2 * the angle of the euclidean plane providing the x and y axis points of the square root of a and the square root of 1-a
            val c: Double = 2 * atan2(sqrt(a), sqrt(1 - a))
            // the calculated distance is now the radius of the globe multiplied by the value of c multiplied by 1000 to determine distance in meters
            var calculatedDistance: Double = GLOBAL_RADIUS * c * 1000
            Log.e("Total Distance","$calculatedDistance")
            // add to total distance
            totalDistance+=calculatedDistance
        }
        // return total added distance
        return totalDistance
    }
}