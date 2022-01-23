package producers

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Coordinate
import models.ManagerInfo
import models.TemperatureInfo
import models.TemperatureProducerInfo
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import utils.JsonReader
import utils.TopicCreator
import java.lang.Error
import java.util.*
import kotlin.random.Random

object ManagerProducer: Runnable {
    private var managerInfo: ManagerInfo? = null
    private var topicCreator = TopicCreator()
    private var jsonReader = JsonReader()
    private val sleepingTime = 10.0 // Time in seconds
    const val managersTopic = "managers-coordinates"
    const val managersKey = "manager"

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val filename = args[0]
            if (filename == null) {
                println("Por favor informe o nome do arquivo")
            }
            var data = jsonReader.readManagerJsonInfo(filename)
            managerInfo = data[0]
        } catch (err: Error) {
            println(err.localizedMessage)
        }

        run();
    }

    public override fun run() {

        topicCreator.deleteTopic(managersTopic)
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
            val record = ProducerRecord<String, String>(managersTopic, managersKey, data)
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

    private fun getManagerCoordinates(): ManagerInfo {
        val latitude = Random.nextDouble(-90.0, 90.0)
        val longitude = Random.nextDouble(-180.0, 180.0)
        val coord = Coordinate(latitude, longitude)
        val info = managerInfo!!
        info.coordinate = coord
        return info
    }
}