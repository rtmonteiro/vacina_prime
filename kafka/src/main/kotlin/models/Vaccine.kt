package br.lenkeryan.kafka.models

import kotlinx.serialization.Serializable

@Serializable
class Vaccine {
    constructor(brand: String, volume: String, maxTemperature: Double, minTemperature: Double, maxDuration: Double) {
        this.brand = brand
        this.volume = volume
        this.maxTemperature = maxTemperature
        this.minTemperature = minTemperature
        this.maxDuration = maxDuration
    }

    var brand: String? = null
    var volume: String? = null
    var maxTemperature: Double = 0.0
    var minTemperature: Double = 0.0
    var maxDuration: Double = 0.0
    var lastTimeOutOfBounds: Long = 0

    fun checkIfTemperatureIsOutOfBounds(temperature: Double): Boolean {
        if(temperature < minTemperature || temperature > maxTemperature) {
            return true
        }
        return false
    }

    fun checkIfTemperatureIsNearOutOfBounds(temperature: Double): Boolean {
        val tolerance = 1
        if(temperature < minTemperature + tolerance && temperature > minTemperature) {
            return true
        }
        if (temperature > maxTemperature - tolerance && temperature < maxTemperature) {
            return true
        }
        return false
    }

}