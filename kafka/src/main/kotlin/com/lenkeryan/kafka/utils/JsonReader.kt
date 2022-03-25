package com.lenkeryan.kafka.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.lenkeryan.kafka.models.ManagerInfo
import com.lenkeryan.kafka.models.TemperatureConsumerInfo
import com.lenkeryan.kafka.models.TemperatureProducerInfo
import java.io.File

class JsonReader {

    fun readProducerJsonInfo(filename: String) : TemperatureProducerInfo {
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

    fun readConsumerJsonInfo(filename: String) : TemperatureConsumerInfo {
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

    fun readManagerJsonInfo(filename: String) : ManagerInfo {
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

    fun readProducerJsonList(filename: String) : ArrayList<TemperatureProducerInfo>{
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

    fun readConsumerJsonList(filename: String) : ArrayList<TemperatureConsumerInfo> {
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

    fun readManagerJsonList(filename: String) : ArrayList<ManagerInfo> {
        val fileContent = File(filename).readText()

        return Json.decodeFromString(fileContent)
    }

}