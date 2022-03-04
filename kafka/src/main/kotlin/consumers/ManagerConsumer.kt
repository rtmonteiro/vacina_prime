package consumers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.Coordinate
import models.ManagerCoordinates
import models.ProgramData
import models.ProgramData.managers
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KTable
import utils.Constants
import java.time.Duration
import java.util.*


class ManagerConsumer: Runnable {
    private val topic = Constants.managersTopic
    private val bootstrapServer = Constants.bootstrapServer

    override fun run() {
        val prop = Properties()

        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic)

        runManagerLocationConsumer()

        // Criar um Consumidor
        val consumer = KafkaConsumer<String, String>(prop)
        consumer.subscribe(listOf(topic))
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                analyseManagerInfo(record)
            }
        }
    }

    private fun runManagerLocationConsumer() {
        val prop = Properties()

        prop.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "manager-location")
        prop.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        prop.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
        prop.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)

        val builder = StreamsBuilder()

        val managerLocations = builder.table<String, String>(Constants.managersTopic)

        val locationsTable: KTable<String, ManagerCoordinates> = managerLocations
            .mapValues { value -> Json.decodeFromString<ManagerCoordinates>(value) }

        locationsTable.toStream()
            .peek { _, value -> println("NAME: ${value.manager.name}\n\tLAT: ${value.lat}\n\tLON: ${value.lon}") }

        val streams = KafkaStreams(builder.build(), prop)

        streams.cleanUp()
        streams.start()

        Runtime.getRuntime().addShutdownHook(Thread(streams::close))
    }

    private fun analyseManagerInfo(record: ConsumerRecord<String, String>) {
        val info: ManagerCoordinates = Json.decodeFromString(record.value())
        val managerExists = ProgramData.returnIfManagerExists(record.key())
        if(!managerExists) {
            println("[ManagerConsumer] Novo manager com nome ${info.manager.name} registrado no consumidor.")
            managers[record.key()] = info.manager
        } else {
            val actualManager = managers[record.key()]
            if (actualManager != null) {
                actualManager.initialCoordinate = Coordinate(info.lat, info.lon)

            }
        }
    }

}