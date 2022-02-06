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

class ManagerConsumer: Runnable {

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



}