package com.lenkeryan.spring.config

import com.lenkeryan.spring.topology.ApplicationTopology
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class StreamConfiguration {

    @Bean
    fun kafkaStreamsConfiguration(): Properties {
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "streams-app"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.STATE_DIR_CONFIG] = "/tmp/kafka-streams/managers";
        return props;
    }

    @Bean
    fun kafkaStreams(@Qualifier("kafkaStreamsConfiguration") props: Properties): KafkaStreams {
        val topology = ApplicationTopology.buildTopology()
        val kafkaStreams = KafkaStreams(topology, props)

        kafkaStreams.cleanUp()
        kafkaStreams.start()

        Runtime.getRuntime().addShutdownHook(Thread(kafkaStreams.close().toString()))

        return kafkaStreams
    }

//    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
//    @Primary
//    fun kStreamsConfigs(): KafkaStreamsConfiguration? {
//        val props: MutableMap<String, Any> = HashMap()
//        props[StreamsConfig.APPLICATION_ID_CONFIG] = "streams-app"
//        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
//        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
//        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
//        return KafkaStreamsConfiguration(props)
//    }
//
//    @Bean
//    fun managers(): NewTopic? {
//        return TopicBuilder.name("managers")
//            .partitions(3)
//            .compact()
//            .build()
//    }
//
//    @Bean
//    fun stream(builder: StreamsBuilder): KStream<String, String>? {
//        val stream: KStream<String, String> = builder
//            .stream("managers-coordinates", Consumed.with(Serdes.String(), Serdes.String()))
//            .peek { _, o -> RocksDBConfigSetter.LOG.info("Output: {}", o) }
//
//        stream.to("managers")
//        return stream
//    }
}