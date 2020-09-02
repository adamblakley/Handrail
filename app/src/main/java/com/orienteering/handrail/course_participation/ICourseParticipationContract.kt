package com.orienteering.handrail.course_participation

import com.orienteering.handrail.models.Event
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.models.PerformanceUploadRequest

/**
 * MVP contract responsible for defining relationships of presenter and view for course participation
 *
 */
interface ICourseParticipationContract {
    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface ICourseActivity{
        fun onEventGetSuccess(event : Event)
        fun onEventGetError()
        fun onEventGetFailure()
        fun onParticipantPostSuccess(participant : Participant)
        fun onParticipantPostError()
        fun onParticipantPostFailure()

    }
    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface ICoursePresenter{
        fun getDataFromDatabase()
        fun onDestroy()
        fun uploadParticipantControlPerformances(performanceUploadRequest : PerformanceUploadRequest)
    }

}