package br.lenkeryan.kafka.producers

import br.lenkeryan.kafka.utils.TopicCreator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import br.lenkeryan.kafka.models.Coordinate
import br.lenkeryan.kafka.models.TemperatureInfo
import br.lenkeryan.kafka.models.TemperatureProducerInfo
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import br.lenkeryan.kafka.utils.JsonReader
import java.lang.Error
import java.util.*
import kotlin.random.Random

class VaccineProducer(producerInfo: TemperatureProducerInfo) : Runnable {

    var producerInfo: TemperatureProducerInfo? = producerInfo
    var topicCreator = TopicCreator()
    var jsonReader = JsonReader()
    private val sleepingTime = 2.0 // Time in seconds

//    @JvmStatic
//    fun main(args: Array<String>) {
//        try {
//            val filename = args[0]
//            if (filename == null) {
//                println("Por favor informe o nome do arquivo")
//            }
//            var data = jsonReader.readProducerJsonInfo(filename)
//            producerInfo = data[0]
//        } catch (err: Error) {
//            println(err.localizedMessage)
//        }
//        run();
//    }

    public override fun run() {
        topicCreator.deleteTopic("hospital-santa-paula")
        //cria produtor com as devidas propriedades (SerDes customizado)
        var producer: KafkaProducer<String, String> = createProducer();

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            println("fechando aplicação... ")
            producer.close()
        })
        while (true) {
//            var temperature: Temperature = Temperature(Random.nextDouble())
            if (producerInfo == null ) {
                return
            }
            val temperature = getTemperatureInfo()
            val data = Json.encodeToString(temperature)
            val record = ProducerRecord(producerInfo!!.hospital, producerInfo!!.id, data)
            //enviar Temperatura serializada para Kafka
            producer.send(record) { recordMetadata, e -> //executes a record if success or exception is thrown
                if (e == null) {
                    println("Producer: " + producerInfo!!.id)
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

        val topicName: String = producerInfo!!.hospital
        topicCreator.createTopic(topicName, 10)
        return KafkaProducer<String, String>(prop)
    }

    private fun getTemperatureInfo(): TemperatureInfo {
        val latitude = Random.nextDouble(-90.0, 90.0)
        val longitude = Random.nextDouble(-180.0, 180.0)
        val coord = Coordinate(latitude, longitude)
        val temp = Random.nextDouble() * 100
        return  TemperatureInfo(temp, producerInfo!!, coord)
    }

}