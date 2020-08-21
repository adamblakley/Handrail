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

class PerformancePerformer(performanceView : IPerformanceContract.IPerformanceView, participantInteractor: ParticipantInteractor) : IPerformanceContract.IPerformancePresenter {

    lateinit var participant : Participant

    var performanceView : IPerformanceContract.IPerformanceView?
    var participantInteractor : ParticipantInteractor
    var getParticipantOnFinishedListener : GetParticipantOnFinishedListener

    init{
        this.performanceView = performanceView
        this.participantInteractor = participantInteractor
        this.getParticipantOnFinishedListener = GetParticipantOnFinishedListener(this,performanceView)
    }

    override fun requestDataFromServer(eventId: Int) {
        participantInteractor.getParticipant(eventId,(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0)),getParticipantOnFinishedListener)
    }

    override fun getControls() {
        var controlNameLatLng = mutableMapOf<String, LatLng>()
        for (performance in participant.participantControlPerformances){
            performance.pcpControl.createLatLng()
            controlNameLatLng.put(performance.pcpControl.controlName,performance.pcpControl.controlLatLng)
        }
        performanceView?.addControls(controlNameLatLng)
    }

    override fun getRoute() {
        val mapUtilities = MapUtilities()
        var routePointsLatLng = mapUtilities.getAllParticipantRoutePoints(participant)
        val bounds : LatLngBounds = mapUtilities.determineNESW(routePointsLatLng)
        val totalDistance = mapUtilities.calculateTotalDistance(routePointsLatLng)
        val pace = mapUtilities.calculatePace(participant.participantControlPerformances[participant.participantControlPerformances.size-1].controlTime,totalDistance)
        performanceView?.showRoute(routePointsLatLng,bounds,totalDistance,pace)
    }

    override fun getPerformanceInformation(){
        val geofencePerformanceCalculator = GeofencePerformanceCalculator()
        // control names
        var controlNames = mutableListOf<String>()
        // control positions
        var controlPositions = mutableListOf<Int>()
        // image urls for controls
        var imageUrls = mutableListOf<String>()
        // participant time
        var times = mutableListOf<String>()
        // distance ran between controls
        var altitudes = mutableListOf<Double>()

        for (performance in participant.participantControlPerformances){
            for (photo in performance.pcpControl.controlPhotographs){
                if(photo.active!!){
                    imageUrls.add(photo.photoPath)
                } else{
                    imageUrls.add("")
                }
            }
            times.add(geofencePerformanceCalculator.convertMilliToMinutes(performance.controlTime))
            controlPositions.add(performance.pcpControl.controlPosition!!)
            controlNames.add(performance.pcpControl.controlName!!)
            performance.pcpControl.controlAltitude?.let { altitudes.add(it) }
        }

        performanceView?.fillRecyclerView(imageUrls,controlPositions,controlNames,times,altitudes)
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
class GetParticipantOnFinishedListener(performancePerformer : IPerformanceContract.IPerformancePresenter, performanceView : IPerformanceContract.IPerformanceView) : IOnFinishedListener<Participant> {
    // Events view
    private var performancePerformer : IPerformanceContract.IPerformancePresenter
    // Events presenter
    private var performanceView : IPerformanceContract.IPerformanceView

    /**
     * Initialises view, presenter
     */
    init{
        this.performanceView = performanceView
        this.performancePerformer = performancePerformer
    }

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