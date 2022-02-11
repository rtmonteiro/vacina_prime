package producers

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ManagerInfo
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import br.lenkeryan.kafka.utils.JsonReader
import br.lenkeryan.kafka.utils.TopicManager
import models.ManagerCoordinates
import utils.Constants
import java.util.*
import kotlin.Error
import kotlin.random.Random

object ManagerProducer: Runnable {
    var managerInfo: ManagerInfo? = null
    private var topicManager = TopicManager()
    private var jsonReader = JsonReader()
    private val sleepingTime = 30.0 // Time in seconds
    val managersTopic = Constants.managersTopic

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val filename = args[0]
            val data = jsonReader.readManagerJsonInfo(filename)
            managerInfo = data
        } catch (err: Error) {
            println(err.localizedMessage)
        }
        run()

    }

    override fun run() {

        //cria produtor com as devidas propriedades (SerDes customizado)
        val producer: KafkaProducer<String, String> = createProducer()

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            println("fechando aplicação... ")
            producer.close()
        })
        while (true) {
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
            Thread.sleep((sleepingTime * 1000).toLong())
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

        topicManager.createTopic(managersTopic, Constants.managersNumberPartitions)
        return KafkaProducer<String, String>(prop)
    }

    private fun getManagerCoordinates(): ManagerCoordinates {
        if ( managerInfo == null ) { throw Error() }
        val latitude = Random.nextDouble(-0.001, 0.001) + managerInfo!!.initialCoordinate.lat
        val longitude = Random.nextDouble(-0.001, 0.001) + managerInfo!!.initialCoordinate.lon
        return ManagerCoordinates(latitude, longitude, managerInfo!!)
    }

}