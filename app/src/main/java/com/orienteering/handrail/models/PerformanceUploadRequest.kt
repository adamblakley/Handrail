package com.orienteering.handrail.models

/**
 * Holds Route Point and Performances for upload to service
 *
 * @constructor
 * TODO
 *
 * @param startTime
 * @param performances
 * @param routePoints
 */
class PerformanceUploadRequest(startTime : Long, performances : List<ParticipantControlPerformance>, routePoints : List<RoutePoint>) {

    var startTime : Long
    var performances : List<ParticipantControlPerformance>
    var routePoints : List<RoutePoint>

    init{
        this.startTime=startTime
        this.performances=performances
        this.routePoints=routePoints
    }
}