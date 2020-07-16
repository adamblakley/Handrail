package com.orienteering.handrail.classes

import com.google.android.gms.maps.model.LatLng

class RoutePoint(routePointPosition: Int, routePointLatitude : Double, routePointLongitude: Double) {

    private var routePointId :Int? = null
    private var routePointPosition: Int
    private var routePointLatitude : Double
    private var routePointLongitude : Double
    private var latlng : LatLng

    init{
        this.routePointPosition=routePointPosition
        this.routePointLatitude=routePointLatitude
        this.routePointLongitude=routePointLongitude
        this.latlng = LatLng(this.routePointLongitude,this.routePointLatitude)
    }

    override fun toString(): String {
        return "RoutePoint(id=$routePointId, position=$routePointPosition, latitude=$routePointLatitude, longitude=$routePointLongitude)"
    }
}