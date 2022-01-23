package utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.ManagerInfo
import models.TemperatureConsumerInfo
import models.TemperatureProducerInfo
import java.io.File

class JsonReader {

    fun readProducerJsonInfo(filename: String) : List<TemperatureProducerInfo>{
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

    fun readConsumerJsonInfo(filename: String) : List<TemperatureConsumerInfo>{
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

    fun readManagerJsonInfo(filename: String) : List<ManagerInfo>{
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

}