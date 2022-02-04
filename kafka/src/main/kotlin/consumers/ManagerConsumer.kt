package consumers

import br.lenkeryan.kafka.database.DatabaseHandler
import br.lenkeryan.kafka.models.*
import br.lenkeryan.kafka.producers.ManagerProducer
import br.lenkeryan.kafka.producers.VaccineProducer
import br.lenkeryan.kafka.utils.TwilioApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.ManagerCoordinates
import models.ProgramData.managers
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import utils.Constants
import java.time.Duration
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object ManagerConsumer: Runnable {

    @JvmStatic
    fun main(args: Array<String>) {
        try {

        } catch (err: Error) {
            println(err.localizedMessage)
        }
        run();

    }

    public override fun run() {
        val BootstrapServer = "localhost:9092"
        val Topic = Constants.managersTopic
        val prop = Properties()

        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, Constants.managersTopic)

        // Criar um Consumidor
        val consumer = KafkaConsumer<String, String>(prop)
        consumer.subscribe(listOf(Topic))
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                val managerId = record.key()
                println("ManagerID: $managerId")
                analyseManagerInfo(record)
            }
        }
    }

    private fun analyseManagerInfo(record: ConsumerRecord<String, String>) {
        val info: ManagerCoordinates = Json.decodeFromString(record.value())
        val managerExists = managers.containsKey(record.key())
        if(managerExists == false) {
            println("Novo manager com nome ${info.manager!!.name} registrado no consumidor.")
            managers[record.key()] = info.manager!!
        } else {
            val actualManager = managers[record.key()]
            if (actualManager != null) {
                actualManager.coordinate =
                    info.lat?.let { info.lon?.let { it1 -> Coordinate(it, it1) } } // Atualizando a coordenada do manager
            }
        }
    }

    fun getNearestManager(coordinate: Coordinate): ManagerInfo? {
        var nearestManager: ManagerInfo? = null
        var nearestDistance: Double = 0.0
        managers.forEach { manager ->
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

        val a = sin(deltaSigma / 2) * sin(deltaSigma / 2) +
                cos(sigma1) * cos(sigma2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c

    }
}