package com.orienteering.handrail.results

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.ParticipantInteractor
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.utilities.GeofencePerformanceCalculator
import retrofit2.Response

class ResultsPerformer(resultsView : IResultsContract.IResultsView, participantInteractor: ParticipantInteractor) : IResultsContract.IResultsPerformer {

    // Coursesview
    private var resultsView : IResultsContract.IResultsView?
    // Courses interactor for service requests
    private var participantInteractor: ParticipantInteractor
    // Listener to handles interactor responses
    private var getParticipantsOnFinishedListener : IOnFinishedListener<List<Participant>>

    private lateinit var participants : List<Participant>

    init{
        this. resultsView=resultsView
        this. participantInteractor=participantInteractor
        this.getParticipantsOnFinishedListener = GetParticipantsOnFinishedListener(this,resultsView)

    }

    override fun processInformation(){
        val geofencePerformanceCalculator = GeofencePerformanceCalculator()
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
        resultsView?.showRecyclerInformation(names,times,positions,ids,imageUrls)
    }

    override fun requestDataFromServer(eventId: Int) {
        participantInteractor.getParticipants(eventId,getParticipantsOnFinishedListener)
    }
    override fun setPerformerParticipants(participants : List<Participant>){
        this.participants=participants
    }

    override fun onDestroy() {
        resultsView = null
    }
}

/**
 * Listener handles interactor responses
 *
 * @param performancePerformer
 * @param performanceView
 */
class GetParticipantsOnFinishedListener(resultsPerformer : IResultsContract.IResultsPerformer, resultsView : IResultsContract.IResultsView) : IOnFinishedListener<List<Participant>> {
    // Events view
    private var resultsPerformer : IResultsContract.IResultsPerformer
    // Events presenter
    private var resultsView : IResultsContract.IResultsView

    /**
     * Initialises view, presenter
     */
    init{
        this.resultsView = resultsView
        this.resultsPerformer = resultsPerformer
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
                resultsPerformer.setPerformerParticipants(response.body()?.entity!!)
                resultsPerformer.processInformation()
            } else {
                resultsView.onResponseError()
            }
        } else {
            resultsView.onResponseError()
        }
    }

    /**
     * Unsuccessful response of data request, call view failure handler to display to user
     *
     * @param t
     */
    override fun onFailure(t: Throwable) {
        if (resultsView!=null){
            resultsView.onResponseFailure()
        }
    }
}