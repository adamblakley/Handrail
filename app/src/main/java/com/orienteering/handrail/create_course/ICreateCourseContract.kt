package com.orienteering.handrail.create_course

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.google.android.gms.maps.model.LatLng

interface ICreateCourseContract {

    interface ICreateCoursePerformer{
        fun createCourse(name: String, note :String)
        fun createControl(name : String, note : String)
        fun getControlInformation(nameOfControl : String)
        fun saveLatLng()
        fun setUpMap()
        fun createLocationRequest()
        fun updateLocationUpdateState()
        fun setImage(imageUri : Uri)
        fun getCourseLength() : Boolean
    }

    interface ICreateCourseView{
        fun returnContext() : Context
        fun returnActivity() : Activity
        fun animateMapCamera(currentLatlng : LatLng)
        fun addMapPolyline(routePoints : List<LatLng>)
        fun addMapControl(name: String, note: String, currentLatLng: LatLng)
        fun onResponseError()
        fun onResponseFailure()
        fun onLocationUpdateFailure()
        fun onPostResponseSuccess(courseId : Int)
        fun onControlinformationSucess(nameOfMarker:String? = "Control", noteOfMarker: String? = "Example Text", positionOfMarker: Int? = 0, imageUriOfMarker: Uri? = null)
        fun onSaveLatLngSuccess()
        fun setUpMap()
    }
}