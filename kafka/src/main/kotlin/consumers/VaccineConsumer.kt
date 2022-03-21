package consumers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.*
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


class VaccineConsumer(consumerInfo: TemperatureConsumerInfo): Runnable {
    private var consumerInfo: TemperatureConsumerInfo? = consumerInfo
    private val bootstrapServer = Constants.bootstrapServer
//    private var notificationProducer = createNotificationProducer() // Produtor das notificações


    override fun run() {
        // Cria o consumidor das vacinas
        val consumer = createConsumer()

        runNotificationProducer()

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            println("[VaccineConsumer] fechando aplicação... ")
//            this.notificationProducer.close()
        })

        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                analyseTemperatureInfo(record)
            }
        }
    }

    private fun runNotificationProducer() {
        val prop = Properties()
        prop.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "hospital-santa-paula")
        prop.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        prop.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
        prop.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)

        val builder = StreamsBuilder()

        val vaccineTemperatures = builder.stream<String, String>("hospital-santa-paula")

        val managersTable = runManagerLocationConsumer()

        vaccineTemperatures
            .mapValues { value -> Json.decodeFromString<TemperatureInfo>(value) }
            .peek { key: String, info: TemperatureInfo -> println("\tPEEK: $key: ${info.value}") }
            .filter { _: String, info: TemperatureInfo ->
                return@filter info.producerInfo != null
                        && info.producerInfo!!.vaccines != null
            }
            .filterNot { _: String, info: TemperatureInfo ->
                return@filterNot info.producerInfo?.vaccines?.all { vaccine ->
                    vaccine.maxTemperature >= info.value - Constants.tolerance
                            && vaccine.minTemperature <= info.value + Constants.tolerance } == true
            }
            .peek { _, info -> println("\tIS OUT OF BOUNDS: ${info.producerInfo?.id} = ${info.value}") }
//            .mapValues { value ->
//                value.temperatureInfo?.producerInfo
//                if(value.temperatureInfo?.producerInfo != null) {
//                    val notification = sendWarnNotificationToNearestManager(value.temperatureInfo!!, value.temperatureInfo?.producerInfo!!)
//                    return@mapValues notification
//                } else {
//                    return@mapValues null
//                }
//            }
//
//            .filter { key: String, info: Notification? ->
//                return@filter info != null
//            }
//            .peek {_, info -> println("Notification: ${info?.message}")}
//            .mapValues { value ->
//                return@mapValues Json.encodeToString(value)
//            }
//            .to(Constants.notificationsTopic) // Jogamos as notificações para o tópico de notificações

//        val joined  = vaccineTemperatures.join(managersTable) { leftValue: TemperatureInfo, rightValue: ManagerCoordinates ->
//            val managersAndTemperatures = ManagersAndTemperatures(leftValue, rightValue)
//            return@join managersAndTemperatures
//        }
//        .peek {_, info -> println("Join funcional: ${info.temperatureInfo?.producerInfo?.hospital}, ${info.managerCoordinates.size}") }
//
        val streams = KafkaStreams(builder.build(), prop)

        streams.cleanUp()
        streams.start()

        Runtime.getRuntime().addShutdownHook(Thread(streams::close))
    }

    private fun analyseTemperatureInfo(record: ConsumerRecord<String, String>) {
        val knowFreezers = ProgramData.knownFreezersMap
        val info: TemperatureInfo = Json.decodeFromString(record.value())
        println("[VaccineConsumer] Temperatura recebida da câmara de vacinas id ${info.producerInfo!!.id}: ${info.value}")

        if (info.producerInfo != null
            && info.producerInfo!!.vaccines != null) {
            // Checa primeiro se esta camara de vacinas já está registrada
            val contains = ProgramData.returnIfFreezerExists(info.producerInfo!!.id)
            if(!contains)
                knowFreezers[info.producerInfo!!.id] = info.producerInfo!!

            val now = record.timestamp()
            val freezer = knowFreezers[info.producerInfo!!.id] ?: return
            var willNotificateWarning = false
            var notification: Notification? = null

            freezer.vaccines!!.forEachIndexed { index, vaccine ->
                    val isTemperatureOutOfBounds = vaccine.checkIfTemperatureIsOutOfBounds(info.value)

                    if(isTemperatureOutOfBounds) {
                        // Temperatura fora do limite desejado
                        // Fora por quanto tempo??
                        if (vaccine.lastTimeOutOfBounds.compareTo(0.0) != 0) {
                            val timeDifference = now - vaccine.lastTimeOutOfBounds
                            if (timeDifference >= vaccine.maxDuration * 1000 * 3600) {
                                // Descarte
                                notification = Notification(
                                    type = NotificationType.DISCARD,
                                    message ="Descarte a vacina ${vaccine.brand} da câmara de vacinas de id ${freezer.id} do hospital ${freezer.hospital}",
                                    managers = ProgramData.managers.values.toList() as ArrayList<ManagerInfo>
                                    )
//                                this.sendNotification(notification!!, freezer)
                                println("[VaccineConsumer] Criando notificação do tipo DISCARD de temperatura fora do limite por grande período de tempo!")
                            } else {
                                // Avisar gestor mais próximo
                                notification = sendWarnNotificationToNearestManager(info, freezer)
                                willNotificateWarning = notification != null
                            }
                        } else {
                            freezer.vaccines!![index].lastTimeOutOfBounds = now
                        }
                    } else {
                        val isTemperatureNearOutOfBound = vaccine.checkIfTemperatureIsNearOutOfBounds(info.value)
                        if (isTemperatureNearOutOfBound) {
//                            notification = createCautionNotificationToAllManagers(info, freezer)
                            willNotificateWarning = true
                        } else {
                            // Temperatura tudo ok
                            freezer.vaccines!![index].lastTimeOutOfBounds = 0L
                        }

                }
            }

            if (willNotificateWarning) {
                willNotificateWarning = false
//                this.sendNotification(notification!!, freezer)
//                println("[VaccineConsumer] Avisando manager mais proximo(${notification!!.managerToNotificate!!.name}) no telefone ${notification!!.managerToNotificate!!.phone}")
                if (notification?.notificationType == NotificationType.WARN) {
                    println("[VaccineConsumer] Criando notificação do tipo WARN de temperatura fora dos limites")
                } else {
                    println("[VaccineConsumer] Criando notificação do tipo CAUTION de temperatura fora dos limites")
                }
                notification = null
            }
        }
    }

    private fun sendWarnNotificationToNearestManager(info: TemperatureInfo, freezer: TemperatureProducerInfo): Notification? {
        val nearestManager = info.actualCoordinate?.let { ProgramData.getNearestManager(it) }
        if(nearestManager == null) {
            println("[VaccineConsumer] Não existe um Manager próximo conhecido! não foi possível criar uma notificação")
        } else {
            return Notification(
                type = NotificationType.WARN,
                message = "Atenção ${nearestManager.name}! A câmara de vacina de id ${freezer.id} do hospital ${freezer.hospital} está com temperaturas fora do limite, por favor verifique",
                manager = nearestManager)
        }
        return null
    }

//    private fun createCautionNotificationToAllManagers(info: TemperatureInfo, freezer: TemperatureProducerInfo): Notification? {
//        return Notification(
//            type = NotificationType.CAUTION,
//            message = "Atenção! A câmara de vacina de id ${freezer.id} do hospital ${freezer.hospital} está com temperaturas próximas do limite",
//            managers = ProgramData.managers.values.toList() as ArrayList<ManagerInfo>
//            )
//    }
    private fun runManagerLocationConsumer(): KTable<String, ManagerCoordinates> {
        val prop = Properties()

        prop.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "manager-location")
        prop.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        prop[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest";
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
        return locationsTable
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

//    private fun createNotificationProducer(): KafkaProducer<String, String> {
//        val prop = Properties()
//        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
//        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
//        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
//
//        VaccineProducer.topicManager.createTopic(Constants.notificationsTopic, Constants.notificationsNumPartitions)
//        return KafkaProducer<String, String>(prop)
//    }

//    private fun sendNotification(notification: Notification, freezer: TemperatureProducerInfo) {
//        val record = ProducerRecord(Constants.notificationsTopic, freezer.hospital, Json.encodeToString(notification) )
//        notificationProducer.send(record) { recordMetadata, e -> //executes a record if success or exception is thrown
//            if (e == null) {
//                println(
//                    """[VaccineConsumer SendNotification] Metadados recebidos
//                                        Topic ${recordMetadata.topic()}
//                                        Partition: ${recordMetadata.partition()}
//                                        Offset: ${recordMetadata.offset()}
//                                        Timestamp: ${recordMetadata.timestamp()}"""
//                )
//            } else {
//                println(e.localizedMessage)
//            }
//        }
//    }
}