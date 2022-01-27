package consumers

import com.twilio.Twilio
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import producers.ManagerProducer
import producers.VaccineProducer
import producers.VaccineProducer.jsonReader
import utils.TwilioApi
import java.lang.Math.*
import java.time.Duration
import java.util.*
import kotlin.math.sqrt
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.atan2


object VaccineConsumer: Runnable {
    var consumerInfo: TemperatureConsumerInfo? = null
    var knownFreezersMap: HashMap<String, TemperatureProducerInfo> = hashMapOf()
    var knownManagers: HashMap<String, ManagerInfo> = hashMapOf()
    val twilioApi = TwilioApi()

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val filename = args[0]
            var data = jsonReader.readConsumerJsonInfo(filename)
            consumerInfo = data[0]
        } catch (err: Error) {
            println(err.localizedMessage)
        }
        run();
    }

    public override fun run() {
        val BootstrapServer = "localhost:9092"
        val Topic = consumerInfo!!.hospital
        val prop = Properties()

        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "hospital-santa-paula")

        // Criar um Consumidor
        val consumer = KafkaConsumer<String, String>(prop)
        consumer.subscribe(listOf(Topic, ManagerProducer.managersTopic))
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                if (record.key() == VaccineProducer.vaccineProducerKey) {
                    analyseTemperatureInfo(record)
                } else if (record.key() == ManagerProducer.managersKey) {
                    analyseManagerInfo(record)
                }
            }
        }
    }

    private fun analyseTemperatureInfo(record: ConsumerRecord<String, String>) {
        val info: TemperatureInfo = Json.decodeFromString(record.value())
        if (info.producerInfo != null
            && info.producerInfo!!.vaccines != null) {
            // Checa primeiro se esta camara de vacinas já está registrada
            if(!knownFreezersMap.contains(info.producerInfo!!.id))
                knownFreezersMap[info.producerInfo!!.id] = info.producerInfo!!

            val now = record.timestamp()
            println("Timestamp de envio: $now")
            knownFreezersMap[info.producerInfo!!.id]?.vaccines!!.forEachIndexed { index, vaccine ->
                val isTemperatureOutOfBounds = vaccine.checkIfTemperatureIsOutOfBounds(info.value)

                if(isTemperatureOutOfBounds) {
                    // Temperatura fora do limite desejado
                    // Fora por quanto tempo??
                    println("Temperatura fora dos limites para uma vacina! valor: ${info.value}")
                    val zero: Long = 0
                    if (vaccine.lastTimeOutOfBounds != zero) {
                        val timeDifference = now - vaccine.lastTimeOutOfBounds
                        if (timeDifference >= vaccine.maxDuration * 1000 * 3600) {
                            // Descarte
                            twilioApi.sendMessage("+5527999405527", "Descarte a vacina")
                            println("Descarte a vacina!")
                        } else {
                            // Avisar gestor mais próximo
                            val nearestManager = info.actualCoordinate?.let { getNearestManager(it) }
                            if(nearestManager == null) {
                                println("Não existe um Manager próximo conhecido!")
                            } else {
                                twilioApi.sendMessage(nearestManager.phone, "Meu amigo(a) ${nearestManager.name}, a vacina ta dando ruim lá ")
                                println("Avisando manager mais proximo(${nearestManager.name}) no telefone ${nearestManager.phone}")
                            }
                        }
                    } else {
                        knownFreezersMap[info.producerInfo!!.id]
                            ?.vaccines!![index].lastTimeOutOfBounds = now
                    }
                } else {
                    // Temperatura tudo ok
                    knownFreezersMap[info.producerInfo!!.id]
                        ?.vaccines!![index].lastTimeOutOfBounds = 0L
                }
            }
        }
    }

    private fun analyseManagerInfo(record: ConsumerRecord<String, String>) {
        val info: ManagerInfo = Json.decodeFromString(record.value())
        val managerExists = knownManagers.contains(info.id)
        if(!managerExists) {
            println("Novo manager com nome ${info.name} registrado no consumidor.")
            knownManagers[info.id] = info
        } else {
            val actualManager = knownManagers[info.id]
            if (actualManager != null) {
                actualManager.coordinate = info.coordinate // Atualizando a coordenada do manager
            }
        }
    }

    private fun getNearestManager(coordinate: Coordinate): ManagerInfo? {
        var nearestManager: ManagerInfo? = null
        var nearestDistance: Double = 0.0
        knownManagers.forEach { manager ->
            if (nearestManager == null) {
                nearestManager = manager.value
                nearestDistance = nearestManager!!.coordinate?.let { calculateDistance(coordinate, it) }!!
            } else {
                // Corrigir para distancia entre dois pontos
                val distance = nearestManager!!.coordinate?.let { calculateDistance(coordinate, it) }

                if (distance != null) {
                    if (distance < nearestDistance) {
                        nearestManager = manager.value
                    }
                }
            }
        }

        return nearestManager
    }

    private fun calculateDistance(coordinate1: Coordinate, coordinate2: Coordinate): Double {
        val earthRadius = 6371e3 //raio da terra em metros

        val sigma1: Double = coordinate1.lat * Math.PI / 180 // φ, λ in radians

        val sigma2: Double = coordinate2.lat * Math.PI / 180
        val deltaSigma: Double = (coordinate2.lat - coordinate1.lat) * Math.PI / 180
        val deltaLambda: Double = (coordinate2.lon - coordinate1.lon) * Math.PI / 180

        val a =sin(deltaSigma / 2) *sin(deltaSigma / 2) +
               cos(sigma1) * cos(sigma2) *
               sin(deltaLambda / 2) *sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c

    }
}