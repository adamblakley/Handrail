package com.orienteering.handrail.utilities

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.orienteering.handrail.classes.Participant
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class GeofencePerformanceCalculator {



    fun calculateTime(participant : Participant) {
        var performanceOriginalTime : Long =participant.participantControlPerformances[0].controlTime

        for (participantControlPerformance in participant.participantControlPerformances) {
            Log.e("Calc","${participantControlPerformance.controlTime}")
            if(participantControlPerformance.pcpControl.controlPosition==0){
                participantControlPerformance.controlTime -= participant.startTime!!
            } else {
                participantControlPerformance.controlTime -= performanceOriginalTime
                performanceOriginalTime += participantControlPerformance.controlTime
            }
        }
    }

    fun convertMilliToMinutes(time : Long) : String{

        val minutes : Long = (time/1000) / 60
        val seconds : Int = ((time/1000) % 60).toInt()

        return String.format("%d:%d",minutes,seconds)
}

    fun calculateDistance(firstLatLng: LatLng, secondLatLng: LatLng): Double {

        val radius: Int = 6371

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

        return Math.sqrt(calculatedDistance)
    }


}