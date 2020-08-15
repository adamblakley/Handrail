package com.orienteering.handrail.events_history

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.utilities.App
import retrofit2.Response

class EventsHistoryPerformer(eventsHistoryView : IEventsHistoryContract.IEventsHistoryView, eventInteractor: EventInteractor) : IEventsHistoryContract.IEventsHistoryPresenter {

    // EventsHistory view
    private var eventsView : IEventsHistoryContract.IEventsHistoryView?
    // Events interactor for service requests
    private var eventInteractor: EventInteractor
    // Listener to handles interactor responses
    private var getEventsOnFinishedListener : IOnFinishedListener<List<Event>>

    init{
        this.eventsView = eventsHistoryView
        this.eventInteractor = eventInteractor
        this.getEventsOnFinishedListener = GetEventsHistoryOnFinishedListener(this,this.eventsView!!)
    }

    override fun requestDataFromServer() {
        eventInteractor.retreiveByUserHistory(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getEventsOnFinishedListener)
    }

    override fun onDestroy() {
            eventsView = null
    }
}

/**
 * Listener handles interactor responses
 *
 * @constructor
 * TODO
 *
 * @param eventsPresenter
 * @param eventsView
 */
class GetEventsHistoryOnFinishedListener(eventsHistoryPerformer : IEventsHistoryContract.IEventsHistoryPresenter ,eventsView : IEventsHistoryContract.IEventsHistoryView) :
    IOnFinishedListener<List<Event>> {
    // Events view
    private var eventsHistoryView :  IEventsHistoryContract.IEventsHistoryView
    // Events presenter
    private var eventsHistoryperformer : IEventsHistoryContract.IEventsHistoryPresenter

    /**
     * Initialises view, presenter
     */
    init{
        this.eventsHistoryView = eventsView
        this.eventsHistoryperformer = eventsHistoryPerformer
    }

    /**
     * On successful response, ask view to fill recycler view with events history information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<List<Event>>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                eventsHistoryView?.fillRecyclerView(response.body()!!.entity as ArrayList<Event>)
            } else {
                eventsHistoryView?.onResponseError()
            }
        } else {
            eventsHistoryView?.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (eventsHistoryView!=null){
            eventsHistoryView?.onResponseFailure(t)
        }
    }

}