package com.orienteering.handrail.models

class Participant(participantUser : User){

    var participantId : Int? = null
    var startTime: Long? = null
    var participantUser : User
    var participantControlPerformances = mutableListOf<ParticipantControlPerformance>()
    var routePoints = mutableListOf<RoutePoint>()
    var position : Int? = null

    init{
        this.participantUser=participantUser
    }

    override fun toString(): String {
        return "Participant(participantId=$participantId, participantUser=$participantUser, participantControlPerformances=$participantControlPerformances, routePoints=$routePoints)"
    }


}