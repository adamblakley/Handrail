package com.orienteering.handrail.httprequests

import android.provider.ContactsContract.CommonDataKinds.Email




class SignupRequest(firstName : String, lastName : String, email : String, password : String, userDob : String, userBio :String) {

    var firstName: String

    var lastName: String

    var email: String

    var password: String

    var userDob: String

    var userBio: String

    init{
        this.firstName=firstName
        this.lastName=lastName
        this.email=email
        this.password=password
        this.userDob=userDob
        this.userBio=userBio
    }

    override fun toString(): String {
        return "SignupRequest(firstName='$firstName', lastName='$lastName', email='$email', password='$password', userDob='$userDob', userBio='$userBio')"
    }

}