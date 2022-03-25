package com.lenkeryan.kafka.models

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object ProgramData {
    var managers = ConcurrentHashMap<String, ManagerInfo>()
    var knownFreezersMap = ConcurrentHashMap<String, TemperatureProducerInfo>()

    fun returnIfFreezerExists(key: String): Boolean {
        knownFreezersMap[key] ?: return false
        return true;
    }

    fun returnIfManagerExists(key: String): Boolean {
        managers[key] ?: return false
        return true;
    }

    fun getNearestManager(coordinate: Coordinate): ManagerInfo? {
        var nearestManager: ManagerInfo? = null
        var nearestDistance = 0.0
        managers.values.forEach { manager ->
            if (nearestManager == null) {
                nearestManager = manager
                nearestDistance = calculateDistance(coordinate, nearestManager!!.initialCoordinate)
            } else {
                // Corrigir para distancia entre dois pontos
                val distance = calculateDistance(coordinate, manager.initialCoordinate)

                if (distance != null) {
                    if (distance < nearestDistance) {
                        nearestManager = manager
                    }
                }
            }
        }

        return nearestManager
    }


    private fun calculateDistance(coordinate1: Coordinate, coordinate2: Coordinate): Double {
        val earthRadius = 6371e3 //raio da terra em metros

        val sigma1: Double = coordinate1.lat * Math.PI / 180 // φ, λ in radians

        val sigma2: Double = coordinate2.lat * Math.PI / 180
        val deltaSigma: Double = (coordinate2.lat - coordinate1.lat) * Math.PI / 180
        val deltaLambda: Double = (coordinate2.lon - coordinate1.lon) * Math.PI / 180

        val a = sin(deltaSigma / 2) * sin(deltaSigma / 2) +
                cos(sigma1) * cos(sigma2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c

    }

}