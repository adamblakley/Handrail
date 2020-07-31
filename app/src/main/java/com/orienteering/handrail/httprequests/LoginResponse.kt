package com.orienteering.handrail.httprequests

class LoginResponse(accessToken:String,tokenType:String,userId:Long) {

    var accessToken:String
    var tokenType:String
    var userId:Long

    init{
        this.accessToken=accessToken
        this.tokenType=tokenType
        this.userId=userId
    }
    override fun toString(): String {
        return "LoginResponse(accessToken='$accessToken', tokenType='$tokenType', userId=$userId)"
    }
}