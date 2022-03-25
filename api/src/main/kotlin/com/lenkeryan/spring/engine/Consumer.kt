package com.lenkeryan.spring.engine

import com.lenkeryan.spring.models.ManagerCoordinates
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class Consumer {
//    private val logger: Logger = LoggerFactory.getLogger(ManagerCoordinates::class.java)
//
//    @KafkaListener(topics = ["managers-coordinates"], groupId = "group_id")
//    @Throws(IOException::class)
//    fun consume(record: ConsumerRecord<String, String>) {
//
//    }
}