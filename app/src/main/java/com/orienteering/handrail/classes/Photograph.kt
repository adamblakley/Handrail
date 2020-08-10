package com.orienteering.handrail.classes

import kotlin.properties.Delegates

class Photograph {

    var photoID : Integer? = null
    lateinit var photoName : String
    lateinit var photoPath : String
    var active : Boolean? = null

    override fun toString(): String {
        return "Photograph(photoID=$photoID, photoName='$photoName', photoPath='$photoPath', isActive=$active)"
    }


}