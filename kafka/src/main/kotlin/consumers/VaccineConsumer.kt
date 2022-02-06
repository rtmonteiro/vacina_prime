package br.lenkeryan.kafka.consumers

import br.lenkeryan.kafka.database.DatabaseHandler
import br.lenkeryan.kafka.models.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import br.lenkeryan.kafka.utils.JsonReader
import br.lenkeryan.kafka.utils.TwilioApi
import consumers.ManagerConsumer
import models.ProgramData
import org.apache.kafka.common.serialization.Serdes
import java.time.Duration
import java.util.*
import org.apache.kafka.streams.*
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.kstream.KStream
import org.slf4j.LoggerFactory


class VaccineConsumer(consumerInfo: TemperatureConsumerInfo): Runnable {
    var consumerInfo: TemperatureConsumerInfo? = consumerInfo
    var knowFreezers = ProgramData.knownFreezersMap
    val jsonReader = JsonReader()

//    @JvmStatic
//    fun main(args: Array<String>) {
//        try {
//            val filename = args[0]
//            var data = jsonReader.readConsumerJsonInfo(filename)
//            consumerInfo = data
//        } catch (err: Error) {
//            println(err.localizedMessage)
//        }
//        run();
//
//    }

    public override fun run() {
        val BootstrapServer = "localhost:9092"
        val Topic = consumerInfo!!.hospital
        val prop = Properties()
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerInfo!!.hospital )


        // Criar um Consumidor
        val consumer = KafkaConsumer<String, String>(prop)
        consumer.subscribe(listOf(Topic))
        while (true) {

            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                analyseTemperatureInfo(record)
            }
        }
    }

    private fun analyseTemperatureInfo(record: ConsumerRecord<String, String>) {
        val info: TemperatureInfo = Json.decodeFromString(record.value())
        if (info.producerInfo != null
            && info.producerInfo!!.vaccines != null) {
            // Checa primeiro se esta camara de vacinas já está registrada
            val contains = knowFreezers.contains(info.producerInfo!!.id)
            if(contains == false)
                knowFreezers[info.producerInfo!!.id] = info.producerInfo!!

            val now = record.timestamp()
            val freezer = knowFreezers.get(info.producerInfo!!.id)
            if(freezer == null) { return }
            freezer.vaccines!!.forEachIndexed { index, vaccine ->
                val isTemperatureOutOfBounds = vaccine.checkIfTemperatureIsOutOfBounds(info.value)

                if(isTemperatureOutOfBounds) {
                    // Temperatura fora do limite desejado
                    // Fora por quanto tempo??
                    println("Temperatura fora dos limites para uma vacina! valor: ${info.value}")
                    if (vaccine.lastTimeOutOfBounds.compareTo(0.0) != 0) {
                        val timeDifference = now - vaccine.lastTimeOutOfBounds
                        println("Diferença em ms da última vez que a vacina esteve fora do limite: $timeDifference")
                        if (timeDifference >= vaccine.maxDuration * 1000 * 3600) {
                            // Descarte
//                            twilioApi.sendMessage("+5527999405527", "Descarte a vacina")
                            println("Descarte a vacina!")
                        } else {
                            // Avisar gestor mais próximo
                            val nearestManager = info.actualCoordinate?.let { ProgramData.getNearestManager(it) }
                            if(nearestManager == null) {
                                println("Não existe um Manager próximo conhecido!")
                            } else {
//                                twilioApi.sendMessage(nearestManager.phone, "Meu amigo(a) ${nearestManager.name}, a vacina ta dando ruim lá")
                                println("Avisando manager mais proximo(${nearestManager.name}) no telefone ${nearestManager.phone}")
                            }
                        }
                    } else {
                        freezer?.vaccines!![index].lastTimeOutOfBounds = now
                    }
                } else {
                    // Temperatura tudo ok
                    freezer?.vaccines!![index].lastTimeOutOfBounds = 0L
                }
            }
        }
    }
}