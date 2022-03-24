package com.example.spring

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.WebServer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import kotlin.jvm.javaClass

@EnableKafka
@EnableKafkaStreams
@SpringBootApplication
class Application


@SpringBootApplication
public class KafkaSpringApplication: ServletWebServerFactory {

    //    @KafkaListener(id = "myId", topics = arrayOf("managers-coordinates"))
//    fun listen(inP: String) {
//        print(inP)
//    }

    public fun main(args: Array<String>) {
        SpringApplication.run(javaClass<KafkaSpringApplication>, args)
    }
    override fun getWebServer(vararg initializers: ServletContextInitializer?): WebServer {
        TODO("Not yet implemented")
    }
}

fun main(args: Array<String>) {
    runApplication<KafkaSpringApplication>(*args)
}



