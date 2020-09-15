package com.orienteering.handrail.toproutes

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.ParticipantInteractor
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.performance_utilities.GeofencePerformanceCalculator
import com.orienteering.handrail.map_utilities.MapUtilities
import com.orienteering.handrail.models.Control
import retrofit2.Response

/**
 * Handles logic of retrieveing and displaying top participant routes
 *
 * @constructor
 *
 * @param topRoutesView
 * @param participantInteractor
 */
class TopRoutesPresenter(topRoutesView : ITopRoutesContract.ITopRoutesView, participantInteractor: ParticipantInteractor) : ITopRoutesContract.ITopRoutesPresenter {

    // view, interactor and onfinished listener variables
    private var topRoutesView : ITopRoutesContract.ITopRoutesView?
    private var participantInteractor : ParticipantInteractor
    private var getParticipantsOnFinishedListener : GetTopParticipantsOnFinishedListener
    private var controlList = mutableListOf<Control>()

    // list of retrieves top participants
    private lateinit var participants : List<Participant>

    /**
     * Initialise variables
     */
    init{
        this.topRoutesView=topRoutesView
        this.participantInteractor=participantInteractor
        this.getParticipantsOnFinishedListener = GetTopParticipantsOnFinishedListener(this,topRoutesView)
    }

    /**
     * Request top 5 participants via the interactor
     *
     * @param eventId
     */
    override fun requestDataFromServer(eventId: Int) {
        participantInteractor.getTopParticipants(eventId,getParticipantsOnFinishedListener)
    }

    override fun processInformation(){
        // performance calculator to determine and display each participant performance
        val geofencePerformanceCalculator = GeofencePerformanceCalculator()
        // participant names
        val names = mutableListOf<String>()
        //participant time
        val times = mutableListOf<String>()
        //participant positions
        val positions = mutableListOf<Int>()
        //participant ids
        val ids = mutableListOf<Int?>()
        //participant image urls
        val imageUrls = mutableListOf<String>()
        // for each participant add the names, times, positions ids and photohgraphs to seperate lists for display
        if (participants != null) {
            for (participant in participants){
                names.add(participant.participantUser.userFirstName)
                Log.e("TAG","${participant.participantUser.userFirstName}")
                times.add(geofencePerformanceCalculator.convertMilliToMinutes(participant.participantControlPerformances[participant.participantControlPerformances.size-1].controlTime))
                positions.add(participants.indexOf(participant))
                ids.add(participant.participantId)
                // check if participant has a photo and determine active photo
                if (participant.participantUser.userPhotographs?.size!! >=1){
                    for (photo in participant.participantUser.userPhotographs!!){
                        if (photo.active==true){
                            imageUrls.add(photo.photoPath)
                            break
                        }
                    }
                } else {
                    // add dummy value if no photo is available
                    imageUrls.add("dummy")
                }
            }
        }
        // call view to show participants information
        topRoutesView?.showInformation(names,times,positions,ids,imageUrls)
    }

    override fun getControls() {
        val controlNameLatLng = mutableMapOf<String, LatLng>()
        // for each control in the event course controls, create latlng value and add name and latlng to map for view to display
        if (participants!=null){
            for (participant in participants){
                if (participant.participantControlPerformances!=null){
                    for (pcp in participants.get(0).participantControlPerformances){
                        pcp.pcpControl.createLatLng()
                        controlList.add(pcp.pcpControl)
                        controlNameLatLng.put(pcp.pcpControl.controlName,pcp.pcpControl.controlLatLng)
                    }
                    // view to display controls
                    topRoutesView?.addControls(controlNameLatLng)
                    break
                }
            }
        }
    }

    override fun controlInformation(markerTitle : String) {
        var noteOfControl: String?
        var positionOfControl: Int?
        var imagePathOfControl: String? = null
        for (control in controlList)
            if (control.controlName==markerTitle) {
                noteOfControl = control.controlNote
                positionOfControl = control.controlPosition
                if (control.isControlPhotographInitialised()){
                    for (photo in control.controlPhotographs){
                        if (photo.active!!){
                            imagePathOfControl=photo.photoPath
                        }
                    }
                    topRoutesView?.showControlInformation(markerTitle,noteOfControl,positionOfControl,imagePathOfControl)
                }
            }
    }

    override fun getPerformerParticipants(){
        val mapUtilities = MapUtilities()
        // use maputilities to find all routepoint vlaues for every participant
        val routePointsLatLng = mapUtilities.getAllParticipantRoutePoints(participants)
        // create a bounds for the map display
        val bounds : LatLngBounds = mapUtilities.determineNESW(routePointsLatLng)
        // show participants routes by passing participants and map bounds
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
 * @param topRoutesPresenter
 * @param topRoutesView
 */
class GetTopParticipantsOnFinishedListener(topRoutesPresenter : ITopRoutesContract.ITopRoutesPresenter, topRoutesView : ITopRoutesContract.ITopRoutesView) : IOnFinishedListener<List<Participant>> {
    // Events view
    private var topRoutesPresenter : ITopRoutesContract.ITopRoutesPresenter = topRoutesPresenter

    // Events presenter
    private var topRoutesView : ITopRoutesContract.ITopRoutesView = topRoutesView

    /**
     * On successful response, asks presenter to filter response and view to update
     * If unsuccessful call view error response handler to display to user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<List<Participant>>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                // initiate presenter methods to filter response values into usable data
                topRoutesPresenter.setPerformerParticipants(response.body()?.entity!!)
                topRoutesPresenter.processInformation()
                topRoutesPresenter.getControls()
                topRoutesPresenter.getPerformerParticipants()
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