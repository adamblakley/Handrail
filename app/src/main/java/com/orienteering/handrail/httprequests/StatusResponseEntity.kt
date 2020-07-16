package com.orienteering.handrail.httprequests

class StatusResponseEntity<T> (val status: Boolean, val message : String, val entity : T?) {

}