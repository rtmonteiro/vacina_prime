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
import org.apache.kafka.common.serialization.StringDeserializer
import utils.Constants
import java.time.Duration
import java.util.*

class ManagerConsumer: Runnable {
    private val topic = Constants.managersTopic
    private val bootstrapServer = Constants.bootstrapServer

    public override fun run() {
        val prop = Properties()

        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic)

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

    private fun analyseManagerInfo(record: ConsumerRecord<String, String>) {
        val info: ManagerCoordinates = Json.decodeFromString(record.value())
        val managerExists = ProgramData.returnIfManagerExists(record.key())
        if(!managerExists) {
            println("[ManagerConsumer] Novo manager com nome ${info.manager!!.name} registrado no consumidor.")
            managers[record.key()] = info.manager!!
        } else {
            val actualManager = managers[record.key()]
            if (actualManager != null) {
                actualManager.initialCoordinate = Coordinate(info.lat, info.lon)

            }
        }
    }

}