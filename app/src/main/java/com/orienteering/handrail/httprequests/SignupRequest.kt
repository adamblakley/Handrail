package com.orienteering.handrail.httprequests

/**
 * Signup request wrapper, contains user information
 * User to initiate a signup request on web layer
 *
 * @constructor
 *
 * @param firstName
 * @param lastName
 * @param email
 * @param password
 * @param userDob
 * @param userBio
 */
class SignupRequest(firstName : String, lastName : String, email : String, password : String, userDob : String, userBio :String) {

    // user first name
    var firstName: String
    //user last name
    var lastName: String
    // user email address
    var email: String
    // user password
    var password: String
    // user dob
    var userDob: String
    // user bio, a description of user
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