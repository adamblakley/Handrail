package com.orienteering.handrail.manage_events

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.utilities.App
import retrofit2.Response

class ManageEventsPresenter(view : IManageEventsContract.IManageEventsView, eventInteractor: EventInteractor) : IManageEventsContract.IManageEventsPresenter {
    // Coursesview
    private var view : IManageEventsContract.IManageEventsView?
    // Courses interactor for service requests
    private var eventInteractor: EventInteractor
    // Listener to handles interactor responses
    private var getManageEventsOnFinishedListener : IOnFinishedListener<List<Event>>

    init{
        this.view = view
        this.eventInteractor = eventInteractor
        this.getManageEventsOnFinishedListener = GetManageEventsOnFinishedListener(this,view)
    }
    override fun onDestroy() {
        view=null
    }

    override fun requestDataFromServer() {
        eventInteractor.retreiveByOrganiser(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getManageEventsOnFinishedListener)
    }
}

/**
 * Listener handles interactor responses
 *
 * @param eventsPresenter
 * @param eventsView
 */
class GetManageEventsOnFinishedListener(presenter : IManageEventsContract.IManageEventsPresenter, view : IManageEventsContract.IManageEventsView) :
    IOnFinishedListener<List<Event>> {
    // Events view
    private var view : IManageEventsContract.IManageEventsView
    // Events presenter
    private var presenter : IManageEventsContract.IManageEventsPresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.presenter = presenter
        this.view = view
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<List<Event>>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                view.fillInformation(response.body()!!.entity as ArrayList<Event>)
            } else {
                view.onResponseError()
            }
        } else {
            view.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (view!=null){
            view.onResponseFailure(t)
        }
    }

}