package com.orienteering.handrail.utilities

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.classes.Participant

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
        var allLatLngs : MutableList<LatLng> = mutableListOf()
        for (participant in participants){
            for (routePoint in participant.routePoints){
                allLatLngs.add(routePoint.latlng)
            }
        }
        return allLatLngs
    }

}