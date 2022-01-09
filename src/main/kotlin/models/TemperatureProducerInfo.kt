package models

import kotlinx.serialization.Serializable

@Serializable
data class Coordinate(var x: String, var y:String)

@Serializable
class TemperatureProducerInfo(var id: String, var hospital: String, var coordinates: Coordinate, var vaccines: List<Vaccine>? = null) {

}
