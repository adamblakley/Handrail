package com.orienteering.handrail.create_event

import android.net.Uri
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.models.Event

interface ICreateEventContract {

    interface ICreateEventView{
        fun onPostResponseSuccess(eventId : Int)
        fun onResponseError()
        fun onResponseFailure()
        fun onImageResponseError()
        fun onGetResponseSuccess(courses : List<Course>)
        fun setupImage(imageUri : Uri)
    }

    interface ICreateEventPerformer{
        fun onDestroy()
        fun postDataOnServer(event : Event)
        fun getDataFromServer()
        fun selectImage()
        fun setImage(imageUri : Uri)
    }

}