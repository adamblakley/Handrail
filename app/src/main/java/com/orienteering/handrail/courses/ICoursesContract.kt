package com.orienteering.handrail.courses

import com.orienteering.handrail.models.Course

interface ICoursesContract {

    interface ICoursesPerformer{
        fun onDestroy()
        fun requestDataFromServer()
    }

    interface ICoursesView{
        fun fillRecyclerView(coursesList : ArrayList<Course>)
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
    }
}