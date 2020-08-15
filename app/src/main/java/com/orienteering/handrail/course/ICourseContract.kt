package com.orienteering.handrail.course

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Course

interface ICourseContract {

    interface ICourseView{
        fun fillRecyclerView(controls: List<Control>)
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
        fun onDeleteResponseSuccess()
        fun onGetResponseSuccess(controls: List<Control>, courseName : String)
        fun showInformation(courseName: String?=null, courseNote : String?=null, courseAltitudes : MutableList<Double>? = null, courseDistance :Double)
        fun showControlInformation(nameOfControl:String? = "No Name Available", noteOfControl: String? = "No Note Available", positionOfControl: Int? = 0, imagePath: String? = null)
        fun showMessage(message : String)
        fun showRoute(routePoints : List<LatLng>)
        fun addControls(controlsNameLatLng : Map<String,LatLng>)
    }

    interface ICoursePresenter{
        fun requestDataFromServer()
        fun removeDataFromServer()
        fun courseInformation()
        fun controlInformation(markerTitle : String)
        fun provideBounds()
        fun generateFile(context : Context)
        fun setPresenterCourse(course : Course)
        fun getRoute(controls : List<Control>)
        fun getControls()
    }

}