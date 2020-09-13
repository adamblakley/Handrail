package com.orienteering.handrail.create_event

import android.net.Uri
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.CourseInteractor
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Course
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.image_utilities.ImageSelect
import com.orienteering.handrail.image_utilities.MultipartBodyFactory
import com.orienteering.handrail.permissions.PermissionManager
import retrofit2.Response

/**
 * Create Course presenter, obtains data from backend for courses and allows upload of event to backend
 *
 * @constructor
 *
 * @param createEventView
 * @param imageSelect
 * @param eventInteractor
 * @param courseInteractor
 */
class CreateEventPresenter(createEventView : ICreateEventContract.ICreateEventView, imageSelect : ImageSelect, eventInteractor: EventInteractor, courseInteractor: CourseInteractor) : ICreateEventContract.ICreateEventPresenter{

    var createEventView : ICreateEventContract.ICreateEventView?
    // image select, initiates gallery or camera intent - binds uri to imageUri
    var imageSelect : ImageSelect
    var eventInteractor : EventInteractor
    var courseInteractor : CourseInteractor
    var multipartBodyFactory : MultipartBodyFactory
    var getCoursesForEventOnFinishedListener : GetCoursesForEventOnFinishedListener
    var postEventOnFinishedListener : PostEventOnFinishedListener
    var imageUri : Uri? = null

    /**
     * initialise variables such as view, iamgeselect, interactors and multipartbodyfactory
     */
    init{
        this.createEventView=createEventView
        this.imageSelect=imageSelect
        this.eventInteractor=eventInteractor
        this.courseInteractor=courseInteractor
        this.multipartBodyFactory =
            MultipartBodyFactory(
                imageSelect
            )
        this.getCoursesForEventOnFinishedListener = GetCoursesForEventOnFinishedListener(this,createEventView)
        this.postEventOnFinishedListener = PostEventOnFinishedListener(this,createEventView)
    }

    override fun checkImage(): Boolean {
        return imageUri != null
    }

    override fun onDestroy() {
        if(createEventView!=null){
            createEventView = null
        }
    }

    /**
     * Interact with interactor to retrieve courses, utilize shared preferences user id
     *
     */
    override fun getDataFromServer() {
        courseInteractor.retrieveAllByUser(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getCoursesForEventOnFinishedListener)
    }

    /**
     * Interact with interactor, pushing event to backend
     *
     * @param event
     */
    override fun postDataOnServer(event: Event) {
        if (event!=null) {
            if (imageUri != null) {
                val imageMultipartBodyPart = imageUri?.let {multipartBodyFactory.createImageMultipartBody(imageSelect.activity, it,event.eventName) }
                if (imageMultipartBodyPart != null) {
                    eventInteractor.createEvent(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0), event, imageMultipartBodyPart, postEventOnFinishedListener)
                }
            } else {
                createEventView?.onImageResponseError()
            }
        }
    }

    override fun selectImage() {
        if (PermissionManager.checkPermission(imageSelect.activity, imageSelect.context, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PermissionManager.MULTIPLE_REQUEST_CODES)) {
            imageUri=imageSelect.selectImage()
        }
    }

    override fun setImage(imageUri: Uri) {
        this.imageUri=imageUri
    }
}


/**
 * Listener handles interactor responses for getting courses from backend
 *
 * @constructor
 *
 * @param createEventPresenter
 * @param createEventView
 */
class GetCoursesForEventOnFinishedListener(createEventPresenter : ICreateEventContract.ICreateEventPresenter, createEventView : ICreateEventContract.ICreateEventView) :
    IOnFinishedListener<List<Course>> {
    // Events view
    private var createEventView : ICreateEventContract.ICreateEventView
    // Events presenter
    private var createEventPresenter : ICreateEventContract.ICreateEventPresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.createEventPresenter = createEventPresenter
        this.createEventView = createEventView
    }

    override fun onFinished(response: Response<StatusResponseEntity<List<Course>>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                response.body()!!.entity?.let { createEventView.onGetResponseSuccess(it) }
            } else {
                createEventView.onResponseError()
            }
        } else {
            createEventView.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (createEventView!=null){
            createEventView.onResponseFailure()
        }
    }
}

/**
 * handles response from callback of event creation
 *
 * @constructor
 *
 * @param createEventPresenter
 * @param createEventView
 */
class PostEventOnFinishedListener(createEventPresenter : ICreateEventContract.ICreateEventPresenter, createEventView : ICreateEventContract.ICreateEventView) :
    IOnFinishedListener<Event> {
    // Events view
    private var createEventView : ICreateEventContract.ICreateEventView
    // Events presenter
    private var createEventPresenter : ICreateEventContract.ICreateEventPresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.createEventPresenter = createEventPresenter
        this.createEventView = createEventView
    }


    override fun onFinished(response: Response<StatusResponseEntity<Event>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                response.body()!!.entity?.eventId?.let { createEventView.onPostResponseSuccess(it) }
            } else {
                createEventView.onResponseError()
            }
        } else {
            createEventView.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (createEventView!=null){
            createEventView.onResponseFailure()
        }
    }
}