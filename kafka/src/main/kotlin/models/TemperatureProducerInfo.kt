package models

import br.lenkeryan.kafka.models.Vaccine
import kotlinx.serialization.Serializable

@Serializable
class TemperatureProducerInfo(
    var id: String,
    var hospital: String,
    var vaccines: List<Vaccine>? = null,
    var coordinate: Coordinate
)
