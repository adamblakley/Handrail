package com.orienteering.handrail.classes

class PerformanceResponse() {

    var controls: List<Control> = mutableListOf<Control>()
    var performances: List<ParticipantControlPerformance> = mutableListOf<ParticipantControlPerformance>()
    var routePoints: List<RoutePoint> = mutableListOf<RoutePoint>()

}