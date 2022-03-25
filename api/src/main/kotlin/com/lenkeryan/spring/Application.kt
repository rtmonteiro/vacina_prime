package com.lenkeryan.spring

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.state.RocksDBConfigSetter.LOG
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.scheduling.annotation.EnableAsync


@Configuration
@EnableKafkaStreams
@SpringBootApplication
@EnableAsync
class Application {

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    @Primary
    fun kStreamsConfigs(): KafkaStreamsConfiguration? {
        val props: MutableMap<String, Any> = HashMap()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "streams-app"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        return KafkaStreamsConfiguration(props)
    }

    @Bean
    fun managers(): NewTopic? {
        return TopicBuilder.name("managers")
            .partitions(3)
            .compact()
            .build()
    }

    @Bean
    fun stream(builder: StreamsBuilder): KStream<String, String>? {
        val stream: KStream<String, String> = builder
            .stream("managers-coordinates", Consumed.with(Serdes.String(), Serdes.String()))
            .peek { _, o -> LOG.info("Output: {}", o) }

        stream.to("managers")
        return stream
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}



