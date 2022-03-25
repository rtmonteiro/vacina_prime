package com.lenkeryan.spring

import com.lenkeryan.spring.config.StreamConfiguration
import com.lenkeryan.spring.topology.ApplicationTopology
import org.apache.kafka.streams.KafkaStreams
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.scheduling.annotation.EnableAsync

@EnableKafkaStreams
@SpringBootApplication
@EnableAsync
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}



