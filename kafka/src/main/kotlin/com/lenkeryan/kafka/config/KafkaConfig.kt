package com.lenkeryan.kafka.config

import com.lenkeryan.kafka.utils.Constants
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration


@Configuration
@EnableKafka
@EnableKafkaStreams
open class KafkaConfig {
    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private val bootstrapAddress: String? = Constants.bootstrapServer

    @Bean(name = arrayOf(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME))
    open fun kStreamsConfig(): KafkaStreamsConfiguration? {
        val props: MutableMap<String, Any?> = HashMap()
        props[APPLICATION_ID_CONFIG] = "streams-app"
        props[BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        props[DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        return KafkaStreamsConfiguration(props)
    }
}