package com.orienteering.handrail.performance

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.ParticipantInteractor
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.performance_utilities.GeofencePerformanceCalculator
import com.orienteering.handrail.map_utilities.MapUtilities
import retrofit2.Response

/**
 * Class handles all data retrieval and manipulation of view performance use case
 *
 * @constructor
 *
 * @param performanceView
 * @param participantInteractor
 */
class PerformancePresenter(performanceView : IPerformanceContract.IPerformanceView, participantInteractor: ParticipantInteractor) : IPerformanceContract.IPerformancePresenter {

    // participant value for dissemination of performance values
    lateinit var participant : Participant

    // view, interactor for data retrieval and onfinished listener to handle retrieval response
    private var performanceView : IPerformanceContract.IPerformanceView?
    private var participantInteractor : ParticipantInteractor
    private var getParticipantOnFinishedListener : GetParticipantOnFinishedListener

    /**
     * initialise view, interactor and onfinished listener
     */
    init{
        this.performanceView = performanceView
        this.participantInteractor = participantInteractor
        this.getParticipantOnFinishedListener = GetParticipantOnFinishedListener(this,performanceView)
    }

    /**
     * utilise interactor to request performance information from source
     *
     * @param eventId
     */
    override fun requestDataFromServer(eventId: Int) {
        participantInteractor.getParticipant(eventId,(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0)),getParticipantOnFinishedListener)
    }

    override fun getControls() {
        val controlNameLatLng = mutableMapOf<String, LatLng>()
        //  activate controls latlng and add controls to view view addControls method
        for (performance in participant.participantControlPerformances){
            performance.pcpControl.createLatLng()
            controlNameLatLng.put(performance.pcpControl.controlName,performance.pcpControl.controlLatLng)
        }
        performanceView?.addControls(controlNameLatLng)
    }

    override fun getRoute() {
        // use map utilities class to retrieve all route points from a participant
        val mapUtilities = MapUtilities()
        val routePointsLatLng = mapUtilities.getAllParticipantRoutePoints(participant)
        // determine latlng bounds value using map utilities
        val bounds : LatLngBounds = mapUtilities.determineNESW(routePointsLatLng)
        // determine total distance using map utilities
        val totalDistance = mapUtilities.calculateTotalDistance(routePointsLatLng)
        // determine averagepace over all controls
        val pace = mapUtilities.calculatePace(participant.participantControlPerformances[participant.participantControlPerformances.size-1].controlTime,totalDistance)
        // show route in view bu providing route points, bounds, total distance and average pace
        performanceView?.showRoute(routePointsLatLng,bounds,totalDistance,pace)
    }

    override fun getPerformanceInformation(){
        val geofencePerformanceCalculator = GeofencePerformanceCalculator()
        // control names
        val controlNames = mutableListOf<String>()
        // control positions
        val controlPositions = mutableListOf<Int>()
        // image urls for controls
        val imageUrls = mutableListOf<String>()
        // participant time
        val times = mutableListOf<String>()
        // distance ran between controls
        val altitudes = mutableListOf<Double>()
        // for performance add active photo to image urls else add dummy text
        for (performance in participant.participantControlPerformances){
            for (photo in performance.pcpControl.controlPhotographs){
                if(photo.active!!){
                    imageUrls.add(photo.photoPath)
                } else{
                    imageUrls.add("")
                }
            }
            // convert time values to string for visualisation on ui
            // split performance values into seperate lists to be used individually or not at all by view
            times.add(geofencePerformanceCalculator.convertMilliToMinutes(performance.controlTime))
            controlPositions.add(performance.pcpControl.controlPosition!!)
            controlNames.add(performance.pcpControl.controlName)
            performance.pcpControl.controlAltitude?.let { altitudes.add(it) }
        }
        // display information on user interface via view class
        performanceView?.fillInformation(imageUrls,controlPositions,controlNames,times,altitudes)
    }

    override fun setPresenterParticipant(participant: Participant) {
        this.participant=participant
    }

    override fun onDestroy(){
        performanceView = null
    }
}

/**
 * Listener handles interactor responses
 *
 * @param performancePerformer
 * @param performanceView
 */
class GetParticipantOnFinishedListener(performancePerformer : IPerformanceContract.IPerformancePresenter, performanceView : IPerformanceContract.IPerformanceView) :
    IOnFinishedListener<Participant> {
    // Events view
    private var performancePerformer : IPerformanceContract.IPerformancePresenter = performancePerformer
    // Events presenter
    private var performanceView : IPerformanceContract.IPerformanceView = performanceView

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<Participant>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                performancePerformer.setPresenterParticipant(response.body()?.entity!!)
                performancePerformer.getPerformanceInformation()
                performancePerformer.getControls()
                performancePerformer.getRoute()
            } else {
                performanceView.onResponseError()
            }
        } else {
            performanceView.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (performanceView!=null){
            performanceView.onResponseFailure(t)
        }
    }
}