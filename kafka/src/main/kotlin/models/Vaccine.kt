package br.lenkeryan.kafka.models

import kotlinx.serialization.Serializable

@Serializable
class Vaccine {
    constructor(brand: String, volume: String, maxTemperature: Double, MinTemperature: Double, maxDuration: Double) {
        this.brand = brand
        this.volume = volume
        this.maxTemperature = maxTemperature
        this.minTemperature = minTemperature
        this.maxDuration = maxDuration
    }

    private var brand: String? = null
    private var volume: String? = null
    private var maxTemperature: Double = 0.0
    private var minTemperature: Double = 0.0
    var maxDuration: Double = 0.0
    var lastTimeOutOfBounds: Long = 0

    fun checkIfTemperatureIsOutOfBounds(temperature: Double): Boolean {
        if(temperature < minTemperature || temperature > maxTemperature) {
            return true
        }
        return false
    }

}