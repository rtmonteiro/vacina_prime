package models

import kotlinx.serialization.Serializable

@Serializable
class TemperatureConsumerInfo(
    var id: String,
    var hospital: String,
) {

}
