package com.orienteering.handrail.toproutes

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.ParticipantInteractor
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.performance_utilities.GeofencePerformanceCalculator
import com.orienteering.handrail.map_utilities.MapUtilities
import retrofit2.Response

class TopRoutesPerformer(topRoutesView : ITopRoutesContract.ITopRoutesView, participantInteractor: ParticipantInteractor) : ITopRoutesContract.ITopRoutesPerformer {

    var topRoutesView : ITopRoutesContract.ITopRoutesView?
    var participantInteractor : ParticipantInteractor
    var getParticipantsOnFinishedListener : GetTopParticipantsOnFinishedListener

    private lateinit var participants : List<Participant>

    init{
        this.topRoutesView=topRoutesView
        this.participantInteractor=participantInteractor
        this.getParticipantsOnFinishedListener = GetTopParticipantsOnFinishedListener(this,topRoutesView)
    }

    override fun requestDataFromServer(eventId: Int) {
        participantInteractor.getParticipants(eventId,getParticipantsOnFinishedListener)
    }

    override fun processInformation(){
        val geofencePerformanceCalculator =
            GeofencePerformanceCalculator()
        // participant names
        var names = mutableListOf<String>()
        //participant time
        var times = mutableListOf<String>()
        //participant positions
        var positions = mutableListOf<Int>()
        //participant ids
        var ids = mutableListOf<Int?>()
        //participant image urls
        var imageUrls = mutableListOf<String>()
        if (participants != null) {
            for (participant in participants){
                names.add(participant.participantUser.userFirstName)
                times.add(geofencePerformanceCalculator.convertMilliToMinutes(participant.participantControlPerformances[participant.participantControlPerformances.size-1].controlTime))
                positions.add(participants.indexOf(participant))
                ids.add(participant.participantId)
                if (participant.participantUser.userPhotographs?.size!! >=1){
                    for (photo in participant.participantUser.userPhotographs!!){
                        if (photo.active==true){
                            imageUrls.add(photo.photoPath)
                            break
                        }
                    }
                } else {
                    imageUrls.add("dummy")
                }
            }
        }
        topRoutesView?.showRecyclerInformation(names,times,positions,ids,imageUrls)
    }

    override fun getControls() {
        var controlNameLatLng = mutableMapOf<String, LatLng>()
        for (pcp in participants.get(0).participantControlPerformances){
            pcp.pcpControl.createLatLng()
            controlNameLatLng.put(pcp.pcpControl.controlName,pcp.pcpControl.controlLatLng)
        }
        topRoutesView?.addControls(controlNameLatLng)
    }

    override fun getPerformerParticipants(){
        val mapUtilities = MapUtilities()
        var routePointsLatLng = mapUtilities.getAllParticipantRoutePoints(participants)
        val bounds : LatLngBounds = mapUtilities.determineNESW(routePointsLatLng)
        topRoutesView?.showRoute(participants,bounds)
    }

    override fun setPerformerParticipants(participants : List<Participant>){
        this.participants=participants
    }

    override fun onDestroy() {
        topRoutesView = null
    }
}

/**
 * Listener handles interactor responses
 *
 * @param performancePerformer
 * @param performanceView
 */
class GetTopParticipantsOnFinishedListener(topRoutesPerformer : ITopRoutesContract.ITopRoutesPerformer, topRoutesView : ITopRoutesContract.ITopRoutesView) :
    IOnFinishedListener<List<Participant>> {
    // Events view
    private var topRoutesPerformer : ITopRoutesContract.ITopRoutesPerformer
    // Events presenter
    private var topRoutesView : ITopRoutesContract.ITopRoutesView

    /**
     * Initialises view, presenter
     */
    init{
        this.topRoutesView = topRoutesView
        this.topRoutesPerformer = topRoutesPerformer
    }

    /**
     * On successful response, ask view to fill recycler view with events information
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<List<Participant>>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                topRoutesPerformer.setPerformerParticipants(response.body()?.entity!!)
                topRoutesPerformer.processInformation()
                topRoutesPerformer.getControls()
                topRoutesPerformer.getPerformerParticipants()
            } else {
                topRoutesView.onResponseError()
            }
        } else {
            topRoutesView.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (topRoutesView!=null){
            topRoutesView.onResponseFailure(t)
        }
    }
}