package com.orienteering.handrail.performance_utilities

/**
 * Perform functions to provide performance values
 *
 */
class GeofencePerformanceCalculator {

    /**
     * Provide string value for millisecond performances
     *
     * @param time
     * @return
     */
    fun convertMilliToMinutes(time : Long) : String{
        var minutes : String = ((time/1000) / 60).toString()
        var seconds : String = ((time/1000) % 60).toString()
        if (minutes.length==1){
            minutes="0"+minutes
        }
        if (seconds.length==1){
            seconds="0"+seconds
        }
        return String.format("$minutes:$seconds")
    }

}