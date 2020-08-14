package com.orienteering.handrail.models

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Control(controlName : String, controlNote : String, controlLatitude : Double, controlLongitude : Double, controlAltitude : Double, controlTime : Date)  : Serializable {

    var controlId : Int? = null
    var controlPosition : Int? = null
    var controlName: String
    var controlNote : String
    var controlTime : String
    var controlLatitude : Double? = null
    var controlLongitude : Double? = null
    var controlLatLng: LatLng
    var controlAltitude: Double? = null
    lateinit var controlPhotographs: List<Photograph>

    init {
        this.controlLatitude = controlLatitude
        this.controlLongitude = controlLongitude
        this.controlName = controlName
        this.controlNote = controlNote
        this.controlLatLng = LatLng(controlLatitude,controlLongitude)
        this.controlAltitude = controlAltitude
        val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        this.controlTime = sdf2.format(controlTime)
    }

    fun isControlPhotographInitialised() = ::controlPhotographs.isInitialized

    fun createLatLng(){
        this.controlLatLng = controlLongitude?.let { controlLatitude?.let { it1 -> LatLng(it1, it) } }!!
    }

    override fun toString(): String {
        return "Control(controlId=$controlId, controlName='$controlName', controlNote='$controlNote', controlTime=$controlTime, controlLatitude=$controlLatitude, controlLongitude=$controlLongitude, controlLatLng=$controlLatLng, controlAltitude=$controlAltitude)"
    }


}