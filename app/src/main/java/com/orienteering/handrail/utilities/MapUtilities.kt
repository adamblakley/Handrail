package com.orienteering.handrail.utilities

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Participant

class MapUtilities {

    fun determineNESW(latlngs : MutableList<LatLng>) : LatLngBounds {
        var latNE : Double? = null
        var latSW : Double? = null
        var lngNE : Double? = null
        var lngSW : Double? = null

        for (latlng in latlngs){
            if (latNE==null||latSW==null||lngNE==null||lngSW==null){
                latSW = latlng.latitude
                latNE  = latlng.latitude
                lngSW = latlng.longitude
                lngNE = latlng.longitude
            } else {
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

    fun calculateTotalDistance(firstLatLng : LatLng, secondLatLng: LatLng) : Double {
        val radius: Int = 6371
        var totalDistance : Double = 0.0

            val latDistance = Math.toRadians(secondLatLng.latitude - firstLatLng.latitude)
            val lngDistance = Math.toRadians(secondLatLng.longitude - firstLatLng.longitude)

            val a: Double = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(Math.toRadians(firstLatLng.latitude)) * Math.cos(
                Math.toRadians(
                    secondLatLng.latitude
                )
            ) *
                    Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)

            val c: Double = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            var calculatedDistance: Double = radius * c * 1000

            calculatedDistance = Math.pow(calculatedDistance, 2.0) + Math.pow(0.0, 2.0)
            totalDistance+=Math.sqrt(calculatedDistance)

        return totalDistance
    }

    fun calculateTotalDistance(latlngs : MutableList<LatLng>) : Double {
        val radius: Int = 6371
        var totalDistance : Double = 0.0

        for (position in 0..latlngs.size-2){

            val latDistance = Math.toRadians(latlngs[position+1].latitude - latlngs[position].latitude)
            val lngDistance = Math.toRadians(latlngs[position+1].longitude - latlngs[position].longitude)

            val a: Double = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(Math.toRadians(latlngs[position].latitude)) * Math.cos(
                Math.toRadians(
                    latlngs[position+1].latitude
                )
            ) *
                    Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)

            val c: Double = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            var calculatedDistance: Double = radius * c * 1000

            calculatedDistance = Math.pow(calculatedDistance, 2.0) + Math.pow(0.0, 2.0)
            totalDistance+=Math.sqrt(calculatedDistance)
        }

        return totalDistance
    }


}