package consumers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.Notification
import models.NotificationType
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import utils.Constants
import java.time.Duration
import java.util.*

class NotificationConsumer: Runnable {
    private val bootstrapServer = "localhost:9092"
    private val topic = Constants.notificationsTopic
//    @JvmStatic
//    fun main(args: Array<String>) {
//        try {
//
//        } catch (err: Error) {
//            println(err.localizedMessage)
//        }
//        run();
//
//    }

    override fun run() {
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
                val notification: Notification = Json.decodeFromString(record.value())
                when (notification.notificationType) {
                    NotificationType.DISCARD -> print("Descarte, Descarte imediatamiente!")
                    NotificationType.WARN -> print("Por sua conta em risco")
                    else -> println("WTF")
                }
            }
        }
    }
}