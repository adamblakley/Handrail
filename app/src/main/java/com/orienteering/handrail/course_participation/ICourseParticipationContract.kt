package com.orienteering.handrail.course_participation

import com.orienteering.handrail.models.Event
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.models.PerformanceUploadRequest

interface ICourseParticipationContract {

    interface ICourseActivity{
        fun onEventGetSuccess(event : Event)
        fun onEventGetError()
        fun onEventGetFailure()
        fun onParticipantPostSuccess(participant : Participant)
        fun onParticipantPostError()
        fun onParticipantPostFailure()

    }

    interface ICoursePresenter{
        fun getDataFromDatabase()
        fun uploadParticipantControlPerformances(performanceUploadRequest : PerformanceUploadRequest)
    }

}