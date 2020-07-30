package com.orienteering.handrail.classes

import java.io.Serializable

class User(userEmail : String, userPassword : String, userFirstName : String, userLastName : String, userDob : String, userBio : String)  : Serializable {

    var userId : Long? = null

    lateinit var userEmail : String

    lateinit var userPassword : String

    lateinit var userFirstName : String

    lateinit var userLastName : String

    lateinit var userDob : String

    lateinit var userBio : String

    var userType : Int? = null

    init{
        this.userEmail=userEmail
        this.userFirstName = userFirstName
        this.userLastName=userLastName
        this.userDob = userDob
        this.userBio = userBio
        this.userPassword=userPassword

    }

    override fun toString(): String {
        return "User(userId=$userId, userEmail='$userEmail', userPassword='$userPassword', userFirstName='$userFirstName', userLastName='$userLastName', userDOB='$userDob', userBio='$userBio')"
    }


}