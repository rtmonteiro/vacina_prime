package com.lenkeryan.kafka.consumers

import br.lenkeryan.kafka.utils.TwilioApi
import com.lenkeryan.kafka.models.Notification
import com.lenkeryan.kafka.models.NotificationType
import com.lenkeryan.kafka.utils.Constants
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import java.time.Duration
import java.util.*

object NotificationConsumer: Runnable {
    private val bootstrapServer = "localhost:9092"
    private val topic = Constants.notificationsTopic
    private val twilioApi = TwilioApi()

    @JvmStatic
    fun main(args: Array<String>) {
        try {

        } catch (err: Error) {
            println(err.localizedMessage)
        }
        run();

    }

    override fun run() {
        runKafkaStreams()
    }

    private fun runKafkaStreams() {
        val prop = Properties()

        prop.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "notifications")
        prop.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        prop[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest";
        prop.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
        prop.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)

        val builder = StreamsBuilder()

        val notifications = builder.stream<String, String>(Constants.notificationsTopic)

        notifications
            .filter { _:String, value: String? ->
                return@filter value != null
            }
            .mapValues { value -> Json.decodeFromString<Notification>(value) }
            .peek { _, value -> println("[Notification]: ${value.message}") }

        val streams = KafkaStreams(builder.build(), prop)

        streams.cleanUp()
        streams.start()

        Runtime.getRuntime().addShutdownHook(Thread(streams::close))
    }

    private fun runDefaultConsumer() {
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
                    NotificationType.DISCARD -> {
                        for (managerInfo in notification.managersToNotificate) {
                            println("[NotificationConsumer] DISCARD ${notification.message}")
//                            twilioApi.sendMessage(managerInfo.phone, notification.message)
                        }
                    }
                    NotificationType.WARN -> {
                        for (managerInfo in notification.managersToNotificate) {
                            println("[NotificationConsumer] WARN Manager ${managerInfo.name}: ${notification.message}")
//                            twilioApi.sendMessage(managerInfo.phone, notification.message)
                        }
                    }
                    NotificationType.CAUTION -> {
                        for (managerInfo in notification.managersToNotificate) {
                            println("[NotificationConsumer] CAUTION Manager ${managerInfo.name}: ${notification.message}")
                        }
                    }
                }
            }
        }
    }
}