package com.orienteering.handrail.httprequests

/**
 * Wrapper of login response, contains access token, token type and user id
 *
 * @constructor
 * TODO
 *
 * @param accessToken
 * @param tokenType
 * @param userId
 */
class LoginResponse(accessToken:String,tokenType:String,userId:Long) {
    /**
     * JWT Auth token value, type and individual userid from correct login
     */
    var accessToken:String
    var tokenType:String
    var userId:Long

    /**
     * Initialise variables
     */
    init{
        this.accessToken=accessToken
        this.tokenType=tokenType
        this.userId=userId
    }

    override fun toString(): String {
        return "LoginResponse(accessToken='$accessToken', tokenType='$tokenType', userId=$userId)"
    }
}