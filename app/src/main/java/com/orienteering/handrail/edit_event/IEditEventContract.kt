package com.orienteering.handrail.edit_event

import android.net.Uri
import com.orienteering.handrail.models.Event

interface IEditEventContract {

    interface IEditEventView{
        fun onGetResponseFailure(throwable : Throwable)
        fun onGetResponseError()
        fun onUpdateResponseFailure(throwable : Throwable)
        fun onUpdateResponseError()
        fun onUpdatePartialResponseError(eventId: Int)
        fun onUpdateResponseSuccess(eventId : Int)
        fun fillInformation(eventName : String, eventNote : String, eventTime : String, eventDate : String, courseName : String)
        fun setupImage(imageUrl : Uri)
        fun setupImage(imagepath : String)
    }

    interface IEditEventPresenter{
        fun onDestroy()
        fun putDataOnServer(eventName: String, eventDescription: String, eventDate: String? = null)
        fun getDataFromServer()
        fun selectImage()
        fun setEvent(event : Event)
        fun setImage(imageUri : Uri)
    }

}