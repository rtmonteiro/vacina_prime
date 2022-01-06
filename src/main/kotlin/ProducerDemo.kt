import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*

object ProducerDemo {
    @JvmStatic
    fun main(args: Array<String>) {
        val BootstrapServer = "localhost:9092"
        val Topic = "aula-kafka"
        val prop = Properties()
        val logger = LoggerFactory.getLogger(ProducerDemo::class.java)
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

        // Criar um produtor
        val producer = KafkaProducer<String, String>(prop)
        val record = ProducerRecord<String, String>(Topic, "Testando Ryan")
        producer.send(record) { recordMetadata, e ->
            if (e == null) {
                logger.info(
                    """
                        Metadados recebios 
                        Topic: ${recordMetadata.topic()}
                        Partition: ${recordMetadata.partition()}
                        Offset: ${recordMetadata.offset()}
                        Timestamp: ${recordMetadata.timestamp()}
                        """.trimIndent()
                )
            } else {
                logger.error("Algo deu errado")
            }
        }
        producer.flush()
    }
}