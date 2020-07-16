package com.orienteering.handrail.classes

class Photo {

    var photoID : Integer? = null
    lateinit var photoPathway : String
    lateinit var photoType : String


    override fun toString(): String {
        return "Photo(photoID=$photoID, photoPathway='$photoPathway', photoType='$photoType')"
    }


}