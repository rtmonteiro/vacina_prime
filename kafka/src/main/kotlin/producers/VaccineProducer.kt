package br.lenkeryan.kafka.producers

import br.lenkeryan.kafka.utils.TopicManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Coordinate
import models.TemperatureInfo
import models.TemperatureProducerInfo
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import br.lenkeryan.kafka.utils.JsonReader
import utils.Constants
import java.lang.Error
import java.util.*
import kotlin.random.Random

object VaccineProducer : Runnable {

    var producerInfo: TemperatureProducerInfo? = null
    var topicManager = TopicManager()
    var jsonReader = JsonReader()
    var temperatureOutOfBounds = true
    private val sleepingTime = 25.0 // Time in seconds

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val filename = args[0]
            if (filename == null) {
                println("Por favor informe o nome do arquivo")
            }
            val data = jsonReader.readProducerJsonInfo(filename)
            producerInfo = data

            temperatureOutOfBounds = args[1].toBoolean()
            if (temperatureOutOfBounds) {
                println("[VaccineProducer] Produzindo vacinas fora do limite de temperatura")
            } else {
                println("[VaccineProducer] Produzindo vacinas dentro do limite de temperatura")
            }
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

        val topicName: String = producerInfo!!.hospital
        topicManager.createTopic(topicName, Constants.vaccineNumberPartitions)
        return KafkaProducer<String, String>(prop)
    }

    private fun getTemperatureInfo(): TemperatureInfo {
        val latitude = Random.nextDouble(-0.001, 0.001) + this.producerInfo!!.initialCoordinate.lat
        val longitude = Random.nextDouble(-0.001, 0.001) + this.producerInfo!!.initialCoordinate.lon
        val coord = Coordinate(latitude, longitude)
        var temp = 0.0
        if (temperatureOutOfBounds) {
            val vaccine = this.producerInfo!!.vaccines?.get(0)!!
            temp = Random.nextDouble(vaccine.maxTemperature, vaccine.maxTemperature + 10);
        } else {
            val vaccine = this.producerInfo!!.vaccines?.get(0)!!
            temp = Random.nextDouble(vaccine.minTemperature , vaccine.maxTemperature);
        }
        return TemperatureInfo(temp, producerInfo!!, coord)
    }

}