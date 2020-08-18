package com.orienteering.handrail.location_updates

import android.location.Location

interface ILocationResponder {

    fun locationCallback(location: Location)
    fun startLocationUpdatesFailure()

}