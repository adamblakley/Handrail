package com.orienteering.handrail.httprequests

/**
 * Login request wrapper, contains user password and email address
 *
 * @constructor
 *
 * @param userEmail
 * @param password
 */
class LoginRequest(userEmail: String,password: String) {
    // email of user login attempt
    var userEmail : String
    // user password attempt
    var password : String

    /**
     * Initialise values
     */
    init{
        this.userEmail = userEmail
        this.password = password
    }


}