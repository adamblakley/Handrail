package com.orienteering.handrail.map_utilities

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.orienteering.handrail.models.Control
import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.models.RoutePoint
import com.orienteering.handrail.models.User
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * Unit Test Class MapUtilities
 *
 */
internal class MapUtilitiesTest {

    private val mapUtilities : MapUtilities = MapUtilities()
    lateinit var user : User
    lateinit var participant1 : Participant
    lateinit var participant2 : Participant
    lateinit var participant3 : Participant

    /**
     * Initiate variables
     *
     */
    @Before
    fun setup(){
        this.user  = User("Email","FirstName","LastName","1900-01-01","Bio")
        participant1  = Participant(user)
        participant2  = Participant(user)
        participant3  = Participant(user)
    }

    /**
     * Test determineNESW
     *
     */
    @Test
    fun determineNESW() {
        val latLng1 = LatLng(54.71214, -6.5154)
        val latLng2 = LatLng(54.67325, -6.30666)
        val latLng3 = LatLng(54.63591, -6.49206)
        val latLng4 = LatLng(54.5675, -6.62115)
        val latLng5 = LatLng(54.5675, -6.32452)
        val latlngs = mutableListOf(latLng1,latLng2,latLng3,latLng4,latLng5)
        val expected = LatLngBounds(LatLng(54.5675,-6.62115),LatLng(54.71214,-6.30666))
        assertEquals(expected,mapUtilities.determineNESW(latlngs))
    }

    /**
     * Test getAllParticipantRoutePoints
     *
     */
    @Test
    fun getAllParticipantRoutePoints() {
        val routePoint1 : RoutePoint = RoutePoint(0,54.71214, -6.5154)
        val routePoint2 : RoutePoint = RoutePoint(1,54.67325, -6.30666)
        val routePoint3 : RoutePoint = RoutePoint(0,54.63591, -6.49206)
        val routePoint4 : RoutePoint = RoutePoint(1,54.5675, -6.62115)
        val routePoint5 : RoutePoint = RoutePoint(0,54.5675, -6.32452)
        val routePoint6 : RoutePoint = RoutePoint(1,54.71214,-6.30666)
        participant1.routePoints.add(0,routePoint1)
        participant1.routePoints.add(1,routePoint2)
        participant2.routePoints.add(0,routePoint3)
        participant2.routePoints.add(1,routePoint4)
        participant3.routePoints.add(0,routePoint5)
        participant3.routePoints.add(1,routePoint6)
        val participants = mutableListOf<Participant>(participant1,participant2,participant3)
        val expected = mutableListOf<LatLng>(routePoint1.latlng,routePoint2.latlng,routePoint3.latlng,routePoint4.latlng,routePoint5.latlng,routePoint6.latlng)
        assertEquals(expected,mapUtilities.getAllParticipantRoutePoints(participants))
    }

    /**
     * Test testGetAllParticipantRoutePoints
     *
     */
    @Test
    fun testGetAllParticipantRoutePoints() {
        val routePoint1 : RoutePoint = RoutePoint(0,54.71214, -6.5154)
        val routePoint2 : RoutePoint = RoutePoint(1,54.67325, -6.30666)
        val expected = mutableListOf<LatLng>(routePoint1.latlng,routePoint2.latlng)
        participant1.routePoints.add(0,routePoint1)
        participant1.routePoints.add(1,routePoint2)
        assertEquals(expected,mapUtilities.getAllParticipantRoutePoints(participant1))
    }

    /**
     * Test getAllControlPoints
     *
     */
    @Test
    fun getAllControlPoints() {
        val controlOne : Control = Control("Name1","Note1",54.71214, -6.5154,0.0,Calendar.getInstance().time)
        val controlTwo : Control = Control("Name2","Note2",54.67325, -6.30666,0.0,Calendar.getInstance().time)
        val controlThree : Control = Control("Name3","Note3",54.63591, -6.49206,0.0,Calendar.getInstance().time)
        val controlFour : Control = Control("Name4","Note4",54.5675, -6.62115,0.0,Calendar.getInstance().time)
        controlOne.createLatLng()
        controlTwo.createLatLng()
        controlThree.createLatLng()
        controlFour.createLatLng()
        val controls = mutableListOf<Control>(controlOne,controlTwo,controlThree,controlFour)
        val expected = mutableListOf<LatLng>(controlOne.controlLatLng,controlTwo.controlLatLng,controlThree.controlLatLng,controlFour.controlLatLng)
        assertEquals(expected,mapUtilities.getAllControlPoints(controls))
    }

    /**
     * Test calculatePace
     *
     */
    @Test
    fun calculatePace() {
        val expected = 300.0
        assertEquals(expected,mapUtilities.calculatePace(400000,2000.0),0.1)
    }

    /**
     * Test calculateTotalDistance
     *
     */
    @Test
    fun calculateTotalDistance() {
        val latlngs = mutableListOf<LatLng>(LatLng(54.71214, -6.5154),LatLng(54.67325, -6.30666),LatLng(54.63591, -6.49206),LatLng(54.5675, -6.62115),LatLng(54.5675, -6.32452))
        val expected = 57110.0
        assertEquals(expected,mapUtilities.calculateTotalDistance(latlngs),5.0)
    }
}