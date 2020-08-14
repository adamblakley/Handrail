package com.orienteering.handrail.models

/**
 *
 * Model for password update request
 * @constructor
 *
 *
 * @param currentPassword
 * @param newPassword
 */
class PasswordUpdateRequest(currentPassword : String, newPassword: String) {

    // current user password
    var currentPassword: String
    // new user password for replacement
    var newPassword: String

    init{
        this.currentPassword=currentPassword
        this.newPassword=newPassword
    }

    override fun toString(): String {
        return "PasswordUpdateRequest(currentPassword='$currentPassword', newPassword='$newPassword')"
    }

}