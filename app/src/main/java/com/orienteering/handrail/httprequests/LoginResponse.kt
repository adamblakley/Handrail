package com.orienteering.handrail.httprequests

class LoginResponse(accessToken:String,tokenType:String) {

    var accessToken:String
    var tokenType:String

    init{
        this.accessToken=accessToken
        this.tokenType=tokenType
    }

    override fun toString(): String {
        return "LoginResponse(accessToken='$accessToken', tokenType='$tokenType')"
    }


}