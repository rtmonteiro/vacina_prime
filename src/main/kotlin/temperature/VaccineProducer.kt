package temperature

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.TemperatureProducerInfo
import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.CreatePartitionsOptions
import org.apache.kafka.clients.admin.NewPartitions
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.TopicExistsException
import org.apache.kafka.common.requests.DeleteAclsResponse.log
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random


class VaccineProducer(producerInfo: TemperatureProducerInfo) {

    var producerInfo: TemperatureProducerInfo? = null
    var admin: AdminClient? = null

    fun main(args: Array<String>) {


//        readJsonInfo()
        //cria produtor com as devidas propriedades (SerDes customizado)
        var producer: KafkaProducer<String, String> = createProducer();

        //shutdown hook

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            println("fechando aplicação... ")
            producer.close()
        })
        var temp: Double = 0.0
        while(true) {

//            var temperature: Temperature = Temperature(Random.nextDouble())
            temp = Random.nextDouble()
            println(temp)

            if (producerInfo == null) { return }
            println(producer.partitionsFor(producerInfo!!.hospital).toString())
            val record = ProducerRecord<String, String>(producerInfo!!.hospital, producerInfo!!.id.toInt(),"temperature", temp.toString())

            //enviar Temperatura serializada para Kafka
            producer.send(record) { recordMetadata, e -> //executes a record if success or exception is thrown
                if (e == null) {
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
//            }
//                producer.flush();
//            }
            println("Mimir")
            Thread.sleep(1000);
        }

    }

//    fun readJsonInfo() {
//        val filename = "producer1.json"
//        val fileContent = this.javaClass.classLoader.getResource(filename)?.readText()
//
//        if (fileContent != null) {
//            val json = Json.decodeFromString<List<TemperatureProducerInfo>>(fileContent)
//            println(json.id)
//            this.producerInfo = json
//        }
//    }

    //iniciar produtor Kafka
    private fun createProducer(): KafkaProducer<String, String> {
        val BootstrapServer = "localhost:9092"
        //create properties
        val prop = Properties()
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

        admin = AdminClient.create(prop)
        val options = CreatePartitionsOptions()
        var partitions = HashMap<String, NewPartitions>()
        partitions[producerInfo!!.hospital] = NewPartitions.increaseTo(1)
        admin!!.createPartitions(partitions)
        return KafkaProducer<String, String>(prop)
    }

}