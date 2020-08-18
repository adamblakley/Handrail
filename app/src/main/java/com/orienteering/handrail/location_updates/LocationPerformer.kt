package com.orienteering.handrail.location_updates

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.orienteering.handrail.permissions.PermissionManager

// Log tag
private val TAG: String = LocationPerformer::class.java.getName()

/**
 * Class performs the aquisition of user location by creating location requests and initiating location updates at a regular cycle.
 *
 * @constructor
 * @param locationResponder
 * @param context
 * @param activity
 */
class LocationPerformer(locationResponder : ILocationResponder, context : Context, activity : Activity) {
    // Request code for location permission
    private val REQUEST_SETTINGS_CHECK_CODE = 2
    // Responder class implementing ILocationResponder to receive location update responses
    var locationResponder : ILocationResponder
    // Activity context from view
    var context : Context
    // Activity for view
    var activity : Activity
    // Data object containing service parameters for requests to the location provider
    private lateinit var locationRequest: LocationRequest
    // Boolean value identifies the success or failure of location updates
    private var locationUpdateState = false
    // Receives notications from the location provider
    private var locationCallback : LocationCallback
    // Fused Location Provider Client Variable
    private var fusedLocationClient: FusedLocationProviderClient

    /**
     * Initialisation block
     */
    init{
        this.locationResponder=locationResponder
        this.context=context
        this.activity=activity
        // Create a new fused location client, combining the location providers accessible on the device. LocationServices API provides
        // the necessary functionality to request a service connection, wait for the connection to proceed and return to callback
        // Fused location client provider provides the best location data available from all sources available within a device, handling switching between sources
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        // handles callback of location results and sends location found to the responder for further dissemination
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                locationResponder.locationCallback(p0.lastLocation)
            }
        }
    }

    /**
     * Changes locationupdate state to true and requests start of location updates
     */
    fun updateLocationUpdateState(){
        locationUpdateState = true
        startLocationUpdates()
    }


    /**
     *  Defines parameters for the locationRequest, including interval of location requests and accuracy.
     */
    fun createLocationRequest() {
        // instantiate locationRequest, add to a new instance of locationSettingsRequest.Builder and get and deal with any changes to be manage state changes based on success or failure of location request state
        locationRequest = LocationRequest()
        // rate at which location updates are provided
        locationRequest.interval = 2000
        // fastest rate at which location requests will be sent
        locationRequest.fastestInterval = 1000
        // higher accuracy location requests, reduction of battery life as a tradeoff
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // adds the instantiated request to builder and gets location
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        // checks the location settings are correct, calls task.addOnSucccess/FailureListener on completion
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        //update locationUpdateState and initiate startLocationUpdates() on success of settings check
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        // on failure (location settings) eg locaton settings being turned off, try a fix
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // request location, on error create log
                try { e.startResolutionForResult(activity, REQUEST_SETTINGS_CHECK_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e(TAG, "Error in location settings creation, check permissions of client")
                }
            }
        }
    }

    /**
     * Start Location Updates
     * Checks permissions, and requests updates from fused location client
     */
    private fun startLocationUpdates() {
        // If permission check successful, request updates, else call responder failure to handle failure of location permissions grant.
        if (PermissionManager.checkPermission(activity, context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PermissionManager.LOCATION_PERMISSION_REQUEST_CODE)
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            locationResponder.startLocationUpdatesFailure()
        }
    }
}