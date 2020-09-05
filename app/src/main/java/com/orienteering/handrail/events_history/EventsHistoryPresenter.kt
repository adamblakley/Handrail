package com.orienteering.handrail.events_history

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.EventInteractor
import com.orienteering.handrail.models.Event
import com.orienteering.handrail.utilities.App
import retrofit2.Response

/**
 * Presenter handles logic of events history view,including retrieval of events information
 *
 * @constructor
 * TODO
 *
 * @param eventsHistoryView
 * @param eventInteractor
 */
class EventsHistoryPresenter(eventsHistoryView : IEventsHistoryContract.IEventsHistoryView, eventInteractor: EventInteractor) : IEventsHistoryContract.IEventsHistoryPresenter {

    // EventsHistory view
    private var eventsView : IEventsHistoryContract.IEventsHistoryView?
    // Events interactor for service requests
    private var eventInteractor: EventInteractor
    // Listener to handles interactor responses
    private var getEventsOnFinishedListener : IOnFinishedListener<List<Event>>

    init{
        this.eventsView = eventsHistoryView
        this.eventInteractor = eventInteractor
        this.getEventsOnFinishedListener = GetEventsHistoryOnFinishedListener(this.eventsView!!)
    }

    /**
     * request events data from backend by applying user id as check for participation
     *
     */
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
 * @param presenter
 * @param view
 */
class GetEventsHistoryOnFinishedListener(view : IEventsHistoryContract.IEventsHistoryView) : IOnFinishedListener<List<Event>> {
    // Events view
    private var view :  IEventsHistoryContract.IEventsHistoryView = view

    /**
     * On successful response, ask view to fill recycler view with events history information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<List<Event>>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                view?.fillInformation(response.body()!!.entity as ArrayList<Event>)
            } else {
                view?.onResponseError()
            }
        } else {
            view?.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (view!=null){
            view?.onResponseFailure(t)
        }
    }

}