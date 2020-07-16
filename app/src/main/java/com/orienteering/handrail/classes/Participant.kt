package com.orienteering.handrail.classes

class Participant(participantUser : User){

    var participantId : Int? = null
    var startTime: Long? = null
    var participantUser : User
    var participantControlPerformances = mutableListOf<ParticipantControlPerformance>()
    var routePoints = mutableListOf<RoutePoint>()

    init{
        this.participantUser=participantUser
        this.participantControlPerformances= participantControlPerformances
        this.routePoints= routePoints
    }

    override fun toString(): String {
        return "Participant(participantId=$participantId, participantUser=$participantUser, participantControlPerformances=$participantControlPerformances, routePoints=$routePoints)"
    }


}