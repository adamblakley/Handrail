package com.orienteering.handrail.httprequests

/**
 * Wraps response objects as entity value
 * contains boolean of web-service success, and message of error or success
 *
 * @param T
 * @property status
 * @property message
 * @property entity
 */
class StatusResponseEntity<T> (val status: Boolean, val message : String, val entity : T?) {

}