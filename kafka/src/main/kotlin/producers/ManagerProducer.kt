package br.lenkeryan.kafka.producers

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import br.lenkeryan.kafka.models.ManagerInfo
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import br.lenkeryan.kafka.utils.JsonReader
import br.lenkeryan.kafka.utils.TopicManager
import models.ManagerCoordinates
import utils.Constants
import java.lang.Error
import java.util.*
import kotlin.random.Random

object ManagerProducer: Runnable {
    var managerInfo: ManagerInfo? = null
    private var topicCreator = TopicManager()
    private var jsonReader = JsonReader()
    private val sleepingTime = 20.0 // Time in seconds
    val managersTopic = Constants.managersTopic

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val filename = args[0]
            var data = jsonReader.readManagerJsonInfo(filename)
            managerInfo = data
        } catch (err: Error) {
            println(err.localizedMessage)
        }
        run();

    }

    public override fun run() {

        //cria produtor com as devidas propriedades (SerDes customizado)
        var producer: KafkaProducer<String, String> = createProducer();

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            println("fechando aplicação... ")
            producer.close()
        })
        while (true) {
//            var temperature: Temperature = Temperature(Random.nextDouble())
            if (managerInfo == null) {
                return
            }

            val info = getManagerCoordinates()
            val data = Json.encodeToString(info)
            val record = ProducerRecord<String, String>(managersTopic, managerInfo!!.id, data)
            //enviar Temperatura serializada para Kafka
            producer.send(record) { recordMetadata, e -> //executes a record if success or exception is thrown
                if (e == null) {
                    println("Manager: " + managerInfo!!.name)
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
            println("Mimir")
            Thread.sleep((sleepingTime * 1000).toLong());
        }
    }

    //iniciar produtor Kafka
    private fun createProducer(): KafkaProducer<String, String> {
        val BootstrapServer = "localhost:9092"
        //create properties
        val prop = Properties()
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

        topicCreator.createTopic(managersTopic, 20)
        return KafkaProducer<String, String>(prop)
    }

    private fun getManagerCoordinates(): ManagerCoordinates {
        val latitude = Random.nextDouble(-90.0, 90.0)
        val longitude = Random.nextDouble(-180.0, 180.0)
        return ManagerCoordinates(latitude, longitude, managerInfo!!)
    }

}