package models

import kotlinx.serialization.Serializable

@Serializable
class TemperatureInfo {
    var value: Double = 0.0
    var producerInfo: TemperatureProducerInfo? = null
    var actualCoordinate: Coordinate? = null

    constructor(value: Double, producerInfo: TemperatureProducerInfo, coordinate: Coordinate) {
        this.value = value
        this.producerInfo = producerInfo
        this.actualCoordinate = coordinate
    }
}
