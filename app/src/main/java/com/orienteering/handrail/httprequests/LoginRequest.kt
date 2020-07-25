package com.orienteering.handrail.httprequests

class LoginRequest(userEmail: String,password: String) {

    var userEmail : String
    var password : String

    init{
        this.userEmail = userEmail
        this.password = password
    }


}