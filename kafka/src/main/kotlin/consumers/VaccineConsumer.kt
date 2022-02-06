package br.lenkeryan.kafka.consumers

import br.lenkeryan.kafka.models.*
import br.lenkeryan.kafka.producers.VaccineProducer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import br.lenkeryan.kafka.utils.JsonReader
import kotlinx.serialization.encodeToString
import models.Notification
import models.NotificationType
import models.ProgramData
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import utils.Constants
import java.time.Duration
import java.util.*


class VaccineConsumer(consumerInfo: TemperatureConsumerInfo): Runnable {
    private var consumerInfo: TemperatureConsumerInfo? = consumerInfo
    private var knowFreezers = ProgramData.knownFreezersMap
    private var managers = ProgramData.managers
//    val jsonReader = JsonReader()
    private val bootstrapServer = Constants.bootstrapServer
    // Produtor das notificações
    private var notificationProducer = createNotificationProducer()

//    @JvmStatic
//    fun main(args: Array<String>) {
//        try {
//            val filename = args[0]
//            var data = jsonReader.readConsumerJsonInfo(filename)
//            consumerInfo = data
//        } catch (err: Error) {
//            println(err.localizedMessage)
//        }
//        run();
//
//    }

    public override fun run() {

        // Cria o consumidor das vacinas
        val consumer = createConsumer();

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            println("fechando aplicação... ")
            this.notificationProducer.close()
        })

        while (true) {

            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                analyseTemperatureInfo(record)
            }
        }
    }

    private fun analyseTemperatureInfo(record: ConsumerRecord<String, String>) {
        val info: TemperatureInfo = Json.decodeFromString(record.value())
        if (info.producerInfo != null
            && info.producerInfo!!.vaccines != null) {
            // Checa primeiro se esta camara de vacinas já está registrada
            val contains = ProgramData.returnIfFreezerExists(info.producerInfo!!.id)
            if(!contains)
                knowFreezers[info.producerInfo!!.id] = info.producerInfo!!

            val now = record.timestamp()
            val freezer = knowFreezers[info.producerInfo!!.id] ?: return

            freezer.vaccines!!.forEachIndexed { index, vaccine ->
                val isTemperatureOutOfBounds = vaccine.checkIfTemperatureIsOutOfBounds(info.value)

                if(isTemperatureOutOfBounds) {
                    // Temperatura fora do limite desejado
                    // Fora por quanto tempo??
                    println("Temperatura fora dos limites para uma vacina! valor: ${info.value}")
                    if (vaccine.lastTimeOutOfBounds.compareTo(0.0) != 0) {
                        val timeDifference = now - vaccine.lastTimeOutOfBounds
                        println("Diferença em ms da última vez que a vacina esteve fora do limite: $timeDifference")
                        if (timeDifference >= vaccine.maxDuration * 1000 * 3600) {
                            // Descarte
                            val notification = Notification(
                                type = NotificationType.DISCARD,
                                message = "Descarte a vacina da câmara de vacinas de id ${freezer.id} do hospital ${freezer.hospital}")
                            this.sendDiscardNotification(notification, freezer)
                            println("Descarte a vacina!")
                        } else {
                            // Avisar gestor mais próximo
                            val nearestManager = info.actualCoordinate?.let { ProgramData.getNearestManager(it) }
                            if(nearestManager == null) {
                                println("Não existe um Manager próximo conhecido!")
                            } else {
                                val notification = Notification(
                                    type = NotificationType.WARN,
                                    message = "Atenção! A câmara de vacina de id ${freezer.id} do hospital ${freezer.hospital} está com temperaturas fora do limite, por favor verifique",
                                    manager = nearestManager)
                                this.sendWarningNotification(notification, freezer)
                                println("Avisando manager mais proximo(${nearestManager.name}) no telefone ${nearestManager.phone}")
                            }
                        }
                    } else {
                        freezer?.vaccines!![index].lastTimeOutOfBounds = now
                    }
                } else {
                    // Temperatura tudo ok
                    freezer?.vaccines!![index].lastTimeOutOfBounds = 0L
                }
            }
        }
    }

    private fun createConsumer(): KafkaConsumer<String, String> {
        val topic = consumerInfo!!.hospital
        val prop = Properties()
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic)

        // Criar um Consumidor
        val consumer = KafkaConsumer<String, String>(prop)
        consumer.subscribe(listOf(topic))
        return consumer
    }

    private fun createNotificationProducer(): KafkaProducer<String, String> {
        val prop = Properties()
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

        VaccineProducer.topicCreator.createTopic(Constants.notificationsTopic, Constants.notificationsNumPartitions)
        return KafkaProducer<String, String>(prop)
    }

    private fun sendDiscardNotification(notification: Notification, freezer: TemperatureProducerInfo) {
        val record = ProducerRecord(Constants.notificationsTopic, freezer.hospital, Json.encodeToString(notification) )
        notificationProducer.send(record) { recordMetadata, e -> //executes a record if success or exception is thrown
            if (e == null) {
                println("Producer -> VaccineConsumer de id: " + this.consumerInfo!!.id)
                println(
                    """Metadados recebidos
                                         Topic ${recordMetadata.topic()}
                                         Partition: ${recordMetadata.partition()}
                                        Offset: ${recordMetadata.offset()}
                                        Timestamp: ${recordMetadata.timestamp()}"""
                )
            } else {
                println(e.localizedMessage)
            }
        }
    }

    private fun sendWarningNotification(notification: Notification, freezer: TemperatureProducerInfo) {
        val record = ProducerRecord(Constants.notificationsTopic, freezer.hospital, Json.encodeToString(notification) )
        notificationProducer.send(record) { recordMetadata, e -> //executes a record if success or exception is thrown
            if (e == null) {
                println("Producer -> VaccineConsumer de id: " + this.consumerInfo!!.id)
                println(
                    """Metadados recebidos
                                         Topic ${recordMetadata.topic()}
                                         Partition: ${recordMetadata.partition()}
                                        Offset: ${recordMetadata.offset()}
                                        Timestamp: ${recordMetadata.timestamp()}"""
                )
            } else {
                println(e.localizedMessage)
            }
        }
    }
}