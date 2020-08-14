package com.orienteering.handrail.models

import retrofit2.http.Multipart

class ControlPhotoUpload {

    lateinit var image : Multipart
    var position : Int? = null

}