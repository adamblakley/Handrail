package com.orienteering.handrail.events

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.EventInteractor
import retrofit2.Response

/**
 * Handles EventsActivity logic and access event Interactor for service requests
 *
 * @constructor
 *
 * @param eventsView
 * @param eventInteractor
 */
class EventsPresenter(eventsView: IEventsContract.IEventsView, eventInteractor: EventInteractor) : IEventsContract.IEventsPresenter{

    // Events view
    private var eventsView : IEventsContract.IEventsView?
    // Events interactor for service requests
    private var eventInteractor: EventInteractor
    // Listener to handles interactor responses
    private var getEventsOnFinishedListener : IOnFinishedListener<List<Event>>

    /**
     * Initialises view, interactor, listener
     */
    init{
        this.eventsView = eventsView
        this.eventInteractor = eventInteractor
        this.getEventsOnFinishedListener = GetEventsOnFinishedListener(this,this.eventsView!!)
    }

    override fun onDestroy() {
        eventsView = null
    }

    override fun requestDataFromServer() {
        eventInteractor.getAllEvents(getEventsOnFinishedListener)
    }
}

/**
 * Listener handles interactor responses
 *
 * @constructor
 * TODO
 *
 * @param eventsEventsPresenter
 * @param eventsView
 */
class GetEventsOnFinishedListener(eventsEventsPresenter : IEventsContract.IEventsPresenter, eventsView : IEventsContract.IEventsView) : IOnFinishedListener<List<Event>> {
    // Events view
    private var eventsView : IEventsContract.IEventsView
    // Events presenter
    private var eventsEventsPresenter : IEventsContract.IEventsPresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.eventsView = eventsView
        this.eventsEventsPresenter = eventsEventsPresenter
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
                eventsView?.fillInformation(response.body()!!.entity as ArrayList<Event>)
            } else {
                eventsView?.onResponseError()
            }
        } else {
            eventsView?.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (eventsView!=null){
            eventsView?.onResponseFailure(t)
        }
    }

}