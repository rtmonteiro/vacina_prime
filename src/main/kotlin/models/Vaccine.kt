package models

import kotlinx.serialization.Serializable

@Serializable
class Vaccine {
    constructor(brand: String, volume: String, maxTemperature: Int, MinTemperature: Double, maxDuration: Double)

    private var brand: String? = null
    private var volume: String? = null
    private var maxTemperature: Int = 0
    private var minTemperature: Double = 0.0
    private var maxDuration: Double = 0.0



}