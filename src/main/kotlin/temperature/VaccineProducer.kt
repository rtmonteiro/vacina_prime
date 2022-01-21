package temperature

import TopicCreator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.TemperatureInfo
import models.TemperatureProducerInfo
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import kotlin.random.Random


class VaccineProducer(producerInfo: TemperatureProducerInfo): Runnable {

    var producerInfo: TemperatureProducerInfo? = producerInfo
    var topicCreator = TopicCreator()

    public override fun run() {

        topicCreator.deleteTopic("hospital-santa-paula")
        //cria produtor com as devidas propriedades (SerDes customizado)
        var producer: KafkaProducer<String, String> = createProducer();

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            println("fechando aplicação... ")
            producer.close()
        })
        var temp = 0.0
        while(true) {

//            var temperature: Temperature = Temperature(Random.nextDouble())
            temp = Random.nextDouble()
            if (producerInfo == null) { return }
            val temperature = TemperatureInfo(temp, this.producerInfo!!)
            val data = Json.encodeToString(temperature)
            val record = ProducerRecord<String, String>(producerInfo!!.hospital,producerInfo!!.id, data)
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
//            }
//                producer.flush();
//            }
            println("Mimir")
            Thread.sleep(1000);
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
//        admin = AdminClient.create(prop)
//        val options = CreatePartitionsOptions()
//        var partitions = HashMap<String, NewPartitions>()
//        partitions[producerInfo!!.hospital] = NewPartitions.increaseTo(1)
//        admin!!.createPartitions(partitions)
        return KafkaProducer<String, String>(prop)
    }

}