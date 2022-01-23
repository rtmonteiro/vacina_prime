package models

import kotlinx.serialization.Serializable

@Serializable
data class Coordinate(var lat: Double, var lon: Double)

@Serializable
class TemperatureProducerInfo(
    var id: String,
    var hospital: String,
    var vaccines: List<Vaccine>? = null,
) {

}
