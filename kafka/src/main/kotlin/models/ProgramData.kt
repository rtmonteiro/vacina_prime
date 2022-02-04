package models

import br.lenkeryan.kafka.models.ManagerInfo
import br.lenkeryan.kafka.models.TemperatureProducerInfo
import java.util.concurrent.ConcurrentHashMap

object ProgramData {
    var managers = ConcurrentHashMap<String, ManagerInfo>()

    var knownFreezersMap = ConcurrentHashMap<String, TemperatureProducerInfo>()

}