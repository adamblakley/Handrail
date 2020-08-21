package com.orienteering.handrail.edit_event

import android.net.Uri
import android.util.Log
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.image_utilities.ImageSelect
import com.orienteering.handrail.image_utilities.MultipartBodyFactory
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.permissions.PermissionManager
import retrofit2.Response
import java.text.SimpleDateFormat

class EditEventPresenter(eventId: Int, editEventView: IEditEventContract.IEditEventView, imageSelect: ImageSelect, eventInteractor: EventInteractor) : IEditEventContract.IEditEventPresenter {

    var eventId: Int
    var editEventView: IEditEventContract.IEditEventView?
    var imageSelect: ImageSelect
    var multipartBodyFactory: MultipartBodyFactory
    var eventInteractor: EventInteractor

    private var event: Event? = null
    var imageUri: Uri? = null

    private var getEditEventOnFinishedListener: GetEditEventOnFinishedListener
    private var postEditEventOnFinishedListener: PostEditEventOnFinishedListener

    init {
        this.eventId = eventId
        this.editEventView = editEventView
        this.imageSelect = imageSelect
        this.multipartBodyFactory = MultipartBodyFactory(imageSelect)
        this.eventInteractor = eventInteractor
        this.getEditEventOnFinishedListener = GetEditEventOnFinishedListener(this, editEventView)
        this.postEditEventOnFinishedListener = PostEditEventOnFinishedListener(this, editEventView)
    }

    override fun onDestroy() {
        editEventView = null
    }


    override fun putDataOnServer(eventName: String, eventDescription: String, eventDate: String?) {
        this.event?.eventName = eventName
        this.event?.eventNote = eventDescription
        if (eventDate!=null){
            this.event?.eventDate = eventDate
        }
        if (imageUri != null) {

            val imageMultipartBodyPart = imageUri?.let { event?.eventName?.let { it1 -> multipartBodyFactory.createImageMultipartBody(imageSelect.activity, it, it1) } }
            this.event?.eventId?.let { eventInteractor.update(it, event!!, imageMultipartBodyPart, postEditEventOnFinishedListener) }
        } else {
            this.event?.eventId?.let { eventInteractor.update(it, event!!, postEditEventOnFinishedListener) }
        }

    }

    override fun getDataFromServer() {
        eventId?.let { eventInteractor.retreiveByID(it, getEditEventOnFinishedListener) }
    }


    override fun selectImage() {
        if (PermissionManager.checkPermission(
                imageSelect.activity, imageSelect.context,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PermissionManager.MULTIPLE_REQUEST_CODES
            )
        ) {
            imageUri = imageSelect.selectImage()
        }
    }

    override fun setEvent(event: Event) {
        this.event = event
    }

    override fun setImage(imageUri: Uri) {
        this.imageUri = imageUri
    }
}

class GetEditEventOnFinishedListener(
    editEventPresenter: IEditEventContract.IEditEventPresenter,
    editEventView: IEditEventContract.IEditEventView
) :
    IOnFinishedListener<Event> {

    lateinit var event: Event
    private var editEventView: IEditEventContract.IEditEventView
    private var editEventPresenter: IEditEventContract.IEditEventPresenter

    init {
        this.editEventView = editEventView
        this.editEventPresenter = editEventPresenter
    }

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {

        lateinit var eventDate: String
        lateinit var eventTime: String

        if (response.isSuccessful) {
            if (response.body() != null) {
                event = response.body()!!.entity!!

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

                val dateformatted = sdf.parse(event.eventDate)

                val dateFormatter = SimpleDateFormat("YYYY-MM-dd")
                val timeFormatter = SimpleDateFormat("HH:mm")
                eventDate = dateFormatter.format(dateformatted)
                eventTime = timeFormatter.format(dateformatted)

                editEventPresenter.setEvent(event)
                response.body()!!.entity?.let {
                    editEventView?.fillInformation(
                        event.eventName,
                        event.eventNote,
                        eventTime,
                        eventDate,
                        event.eventCourse.courseName
                    )
                }
                for (photo in response.body()!!.entity?.eventPhotographs!!) {
                    if (photo.active!!) {
                        editEventView.setupImage(photo.photoPath)
                        break
                    }
                }
            } else {
                editEventView?.onGetResponseError()
            }
        } else {
            editEventView?.onGetResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (editEventView != null) {
            editEventView?.onGetResponseFailure(t)
        }
    }
}


class PostEditEventOnFinishedListener(
    editEventPresenter: IEditEventContract.IEditEventPresenter,
    editEventView: IEditEventContract.IEditEventView
) : IOnFinishedListener<Event> {

    lateinit var event: Event
    private var editEventView: IEditEventContract.IEditEventView
    private var editEventPresenter: IEditEventContract.IEditEventPresenter

    init {
        this.editEventView = editEventView
        this.editEventPresenter = editEventPresenter
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if (response.isSuccessful) {
            if (response.body()?.entity != null) {
                response.body()!!.entity?.eventId?.let { editEventView.onUpdateResponseSuccess(it) }
            } else {
                editEventView.onUpdateResponseError()
            }
        } else if (response.code() == 206) {
            response.body()!!.entity?.eventId?.let { editEventView?.onUpdatePartialResponseError(it) }
        } else {
            editEventView?.onUpdateResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (editEventView != null) {
            editEventView.onUpdateResponseFailure(t)
        }
    }
}