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

class CreateEventPerformer(createEventView : ICreateEventContract.ICreateEventView, imageSelect : ImageSelect, eventInteractor: EventInteractor, courseInteractor: CourseInteractor) : ICreateEventContract.ICreateEventPerformer{

    var createEventView : ICreateEventContract.ICreateEventView?
    var imageSelect : ImageSelect
    var eventInteractor : EventInteractor
    var courseInteractor : CourseInteractor
    var multipartBodyFactory : MultipartBodyFactory
    var getCoursesForEventOnFinishedListener : GetCoursesForEventOnFinishedListener
    var postEventOnFinishedListener : PostEventOnFinishedListener
    var imageUri : Uri? = null

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

    override fun onDestroy() {
        if(createEventView!=null){
            createEventView = null
        }
    }

    override fun getDataFromServer() {
        courseInteractor.retrieveAllByUser(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getCoursesForEventOnFinishedListener)
    }

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
 * Listener handles interactor responses
 * @param createEventPerformer
 * @param createEventView
 */
class GetCoursesForEventOnFinishedListener(createEventPerformer : ICreateEventContract.ICreateEventPerformer, createEventView : ICreateEventContract.ICreateEventView) : IOnFinishedListener<List<Course>> {
    // Events view
    private var createEventView : ICreateEventContract.ICreateEventView
    // Events presenter
    private var createEventPerformer : ICreateEventContract.ICreateEventPerformer

    /**
     * Initialises view, presenter
     */
    init{
        this.createEventPerformer = createEventPerformer
        this.createEventView = createEventView
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     * @param response
     */
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

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (createEventView!=null){
            createEventView.onResponseFailure()
        }
    }
}
/**
 * Listener handles interactor responses
 * @param createEventPerformer
 * @param createEventView
 */
class PostEventOnFinishedListener(createEventPerformer : ICreateEventContract.ICreateEventPerformer, createEventView : ICreateEventContract.ICreateEventView) : IOnFinishedListener<Event> {
    // Events view
    private var createEventView : ICreateEventContract.ICreateEventView
    // Events presenter
    private var createEventPerformer : ICreateEventContract.ICreateEventPerformer

    /**
     * Initialises view, presenter
     */
    init{
        this.createEventPerformer = createEventPerformer
        this.createEventView = createEventView
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     * @param response
     */
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

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (createEventView!=null){
            createEventView.onResponseFailure()
        }
    }
}