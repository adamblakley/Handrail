package com.orienteering.handrail.classes

class Photograph {

    var photoID : Integer? = null
    lateinit var photoName : String
    lateinit var photoPath : String

    override fun toString(): String {
        return "Photograph(photoID=$photoID, photoName='$photoName', photoPath='$photoPath')"
    }
}