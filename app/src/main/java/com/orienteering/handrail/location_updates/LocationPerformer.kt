package com.orienteering.handrail.location_updates

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.orienteering.handrail.permissions.PermissionManager

class LocationPerformer(responder : ILocationResponder, context : Context, activity : Activity) {
    private val REQUEST_CHECK_SETTINGS = 2
    var responder : ILocationResponder
    var context : Context
    var activity : Activity
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var locationCallback : LocationCallback
    // Fused Location Prover Client Variable
    private var fusedLocationClient: FusedLocationProviderClient

    init{
        this.responder=responder
        this.context=context
        this.activity=activity
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                responder.locationCallback(p0.lastLocation)
            }
        }
    }

    fun updateLocationUpdateState(){
        locationUpdateState = true
        startLocationUpdates()
    }


    /**
     * Update location
     */
    fun createLocationRequest() {
        // create instance of locationRequest, add to instance of locationSettingsRequest.Builder and get and deal with any changes to be made based on current state of location settings
        locationRequest = LocationRequest()
        // rate at which we will get updates
        locationRequest.interval = 10000
        //fastestInterval provides the fastest rate we can handle updates. It places a limit on how often updates will be sent.
        locationRequest.fastestInterval = 5000
        // high accuracy more likely to use GPS than wifi and cell
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // adds one request to builder to get location
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        //check whether current location settings are satisfied
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        //when task completes, app can check location settings by looking at status code from LocationSettingsResponse object
        //update locationUpdateState and startLocationUpdates()
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        // on failure (location settings) eg locaton settings being turned off, try a fix
        task.addOnFailureListener { e ->

            if (e is ResolvableApiException) {
                // show a user dialogue by calling startResolutionForResult()
                // check result in onActivityResult()
                try { e.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore
                }
            }
        }
    }

    /**
     * Start Location Updates
     */
    private fun startLocationUpdates() {
        if (PermissionManager.checkPermission(
                activity,
                context,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionManager.LOCATION_PERMISSION_REQUEST_CODE
            )
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            responder.startLocationUpdatesFailure()
        }
    }
}