package com.example.spring.service

import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class AppService {

    @Bean
    fun stream(): String {
        val builder = StreamsBuilder()
        val table = builder.table<String, String>("managers-coordinates")

        return "nice"
    }

}