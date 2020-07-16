package com.orienteering.handrail.classes

class ParticipantControlPerformance(controlTime : Long, control : Control) {

    var pcpId : Int? = null
    var controlTime: Long = 0
    var pcpControl : Control

    init{
        this.controlTime= controlTime
        this.pcpControl = control

    }


    override fun toString(): String {
        return "ParticipantControlPerformance(participantControlId=$pcpId, controlTime=$controlTime, control=$pcpControl)"
    }


}