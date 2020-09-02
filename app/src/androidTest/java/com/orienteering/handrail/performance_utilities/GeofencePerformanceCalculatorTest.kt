package com.orienteering.handrail.performance_utilities

import org.junit.Test
import junit.framework.Assert.assertEquals

/**
 * Test GeofencePerformanceCalculator
 *
 */
internal class GeofencePerformanceCalculatorTest {

    val geofencePerformanceCalculator = GeofencePerformanceCalculator()

    /**
     * Test convertMilliToMinutes
     *
     */
    @Test
    fun convertMilliToMinutes() {
        val expected = "01:10"
        assertEquals(expected,geofencePerformanceCalculator.convertMilliToMinutes(70000))
        val expected2 = "00:10"
        assertEquals(expected2,geofencePerformanceCalculator.convertMilliToMinutes(10000))
    }
}