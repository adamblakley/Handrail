package com.orienteering.handrail.edit_event

import android.net.Uri
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.image_utilities.ImageSelect
import com.orienteering.handrail.image_utilities.MultipartBodyFactory
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.permissions.PermissionManager
import retrofit2.Response
import java.text.SimpleDateFormat

/**
 * Presenter class handles logic of edit event use case
 *
 * @constructor
 *
 * @param eventId
 * @param editEventView
 * @param imageSelect
 * @param eventInteractor
 */
class EditEventPresenter(eventId: Int, editEventView: IEditEventContract.IEditEventView, imageSelect: ImageSelect, eventInteractor: EventInteractor) : IEditEventContract.IEditEventPresenter {

    var eventId: Int
    private var editEventView: IEditEventContract.IEditEventView?
    var imageSelect: ImageSelect
    private var multipartBodyFactory: MultipartBodyFactory
    private var eventInteractor: EventInteractor

    private var event: Event? = null
    var imageUri: Uri? = null

    private var getEditEventOnFinishedListener: GetEditEventOnFinishedListener
    private var postEditEventOnFinishedListener: PostEditEventOnFinishedListener

    /**
     * initialise event id, view, image select, interactor and onfinish listeners
     */
    init {
        this.eventId = eventId
        this.editEventView = editEventView
        this.imageSelect = imageSelect
        this.multipartBodyFactory = MultipartBodyFactory(imageSelect)
        this.eventInteractor = eventInteractor
        this.getEditEventOnFinishedListener = GetEditEventOnFinishedListener(this, editEventView)
        this.postEditEventOnFinishedListener = PostEditEventOnFinishedListener(this, editEventView)
    }

    /**
     * remove view from presenter
     *
     */
    override fun onDestroy() {
        editEventView = null
    }

    /**
     * query interactor to upload data to server, provide onfinished listener to handle response
     *
     * @param eventName
     * @param eventDescription
     * @param eventDate
     */
    override fun putDataOnServer(eventName: String, eventDescription: String, eventDate: String?) {
        this.event?.eventName = eventName
        this.event?.eventNote = eventDescription
        if (eventDate!=null){
            this.event?.eventDate = eventDate
        }
        if (imageUri != null) {
             // if image available, create multipartbody.part and add to update request
            val imageMultipartBodyPart = imageUri?.let { event?.eventName?.let { it1 -> multipartBodyFactory.createImageMultipartBody(imageSelect.activity, it, it1) } }
            this.event?.eventId?.let { eventInteractor.update(it, event!!, imageMultipartBodyPart, postEditEventOnFinishedListener) }
        } else {
            this.event?.eventId?.let { eventInteractor.update(it, event!!, postEditEventOnFinishedListener) }
        }

    }

    /**
     *  retrieve event data from server via interacor, pass event id
     *
     */
    override fun getDataFromServer() {
        eventId.let { eventInteractor.retreiveByID(it, getEditEventOnFinishedListener) }
    }

    /**
     * request camera and storage permissions, bind image uri from selection
     *
     */
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

/**
 * Handles retrieval of events response, updates view and presenter if successful or selects view error if unsuccessful
 *
 * @constructor
 * TODO
 *
 * @param editEventPresenter
 * @param editEventView
 */
class GetEditEventOnFinishedListener(editEventPresenter: IEditEventContract.IEditEventPresenter, editEventView: IEditEventContract.IEditEventView) : IOnFinishedListener<Event> {

    lateinit var event: Event
    private var editEventView: IEditEventContract.IEditEventView = editEventView
    private var editEventPresenter: IEditEventContract.IEditEventPresenter = editEventPresenter

    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {

        lateinit var eventDate: String
        lateinit var eventTime: String

        if (response.isSuccessful) {
            if (response.body() != null) {
                event = response.body()!!.entity!!
                // convert data format to string for display to user
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

                val dateformatted = sdf.parse(event.eventDate)

                val dateFormatter = SimpleDateFormat("YYYY-MM-dd")
                val timeFormatter = SimpleDateFormat("HH:mm")
                eventDate = dateFormatter.format(dateformatted)
                eventTime = timeFormatter.format(dateformatted)
                // set presenter event and update view with fillinformation method
                editEventPresenter.setEvent(event)
                response.body()!!.entity?.let { editEventView.fillInformation(event.eventName, event.eventNote, eventTime, eventDate, event.eventCourse.courseName
                    )
                }
                // determine active photo and set to view
                for (photo in response.body()!!.entity?.eventPhotographs!!) {
                    if (photo.active!!) {
                        editEventView.setupImage(photo.photoPath)
                        break
                    }
                }
            } else {
                editEventView.onGetResponseError()
            }
        } else {
            editEventView.onGetResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (editEventView != null) {
            editEventView.onGetResponseFailure(t)
        }
    }
}

/**
 * Handles response from update event
 * on success, request view update to notify user. on-failure identify error and response to user with explanation
 * @constructor
 * TODO
 *
 * @param editEventPresenter
 * @param editEventView
 */
class PostEditEventOnFinishedListener(editEventPresenter: IEditEventContract.IEditEventPresenter, editEventView: IEditEventContract.IEditEventView) : IOnFinishedListener<Event> {

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
            // if code 206, image upload failure, notify user
        } else if (response.code() == 206) {
            response.body()!!.entity?.eventId?.let { editEventView.onUpdatePartialResponseError(it) }
        } else {
            editEventView.onUpdateResponseError()
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