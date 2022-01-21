package models

import kotlinx.serialization.Serializable

@Serializable
class TemperatureInfo {
    var value: Double = 0.0
    var producerInfo: TemperatureProducerInfo? = null

    constructor(value: Double, producerInfo : TemperatureProducerInfo) {
        this.value = value
        this.producerInfo = producerInfo
    }
}