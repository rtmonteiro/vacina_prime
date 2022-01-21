package temperature

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.TemperatureConsumerInfo
import models.TemperatureInfo
import models.TemperatureProducerInfo
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.List

class VaccineConsumer(temperatureConsumerInfo: TemperatureConsumerInfo): Runnable {
    var consumerInfo: TemperatureConsumerInfo? = temperatureConsumerInfo

    public override fun run() {
        val BootstrapServer = "localhost:9092"
        val Topic = consumerInfo!!.hospital
        val prop = Properties()
        val logger: Logger = LoggerFactory.getLogger(VaccineConsumer::class.java)

        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "aula-kafka-group")

        // Criar um Consumidor
        val consumer = KafkaConsumer<String, String>(prop)
        consumer.subscribe(List.of(Topic))
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                var info = Json.decodeFromString<TemperatureInfo>(record.value())
                println(info.value)
//                logger.info("Key: " + record.key() + " Value: " + record.value() + " Partition: " + record.partition())
            }
        }
    }
}