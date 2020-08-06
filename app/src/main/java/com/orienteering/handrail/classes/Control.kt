package com.orienteering.handrail.classes

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Control(controlName : String, controlNote : String, controlLatitude : Double, controlLongitude : Double, controlAltitude : Double)  : Serializable {

    var controlId : Int? = null
    var controlPosition : Int? = null
    var controlName: String
    var controlNote : String
    var controlTime : Long? = null
    var controlLatitude : Double? = null
    var controlLongitude : Double? = null
    var controlLatLng: LatLng
    var controlAltitude: Double? = null

    init {
        this.controlLatitude = controlLatitude
        this.controlLongitude = controlLongitude
        this.controlName = controlName
        this.controlNote = controlNote
        this.controlLatLng = LatLng(controlLatitude,controlLongitude)
        this.controlAltitude=controlAltitude
        this.controlAltitude = controlAltitude
    }

    fun createLatLng(){
        this.controlLatLng = controlLongitude?.let { controlLatitude?.let { it1 -> LatLng(it1, it) } }!!
    }

    override fun toString(): String {
        return "Control(controlId=$controlId, controlName='$controlName', controlNote='$controlNote', controlTime=$controlTime, controlLatitude=$controlLatitude, controlLongitude=$controlLongitude, controlLatLng=$controlLatLng, controlAltitude=$controlAltitude)"
    }


}