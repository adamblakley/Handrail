package com.orienteering.handrail.models

import java.io.Serializable

class User(userEmail : String, userFirstName : String, userLastName : String, userDob : String, userBio : String)  : Serializable {

    var userId : Long? = null

    lateinit var userEmail : String

    lateinit var userFirstName : String

    lateinit var userLastName : String

    lateinit var userDob : String

    lateinit var userBio : String

    var userType : Int? = null

    lateinit var userPhotographs: List<Photograph>

    init{
        this.userEmail=userEmail
        this.userFirstName = userFirstName
        this.userLastName=userLastName
        this.userDob = userDob
        this.userBio = userBio
    }

    fun isUserPhotographInitialised() = ::userPhotographs.isInitialized

    override fun toString(): String {
        return "User(userId=$userId, userEmail='$userEmail', userFirstName='$userFirstName', userLastName='$userLastName', userDOB='$userDob', userBio='$userBio')"
    }


}