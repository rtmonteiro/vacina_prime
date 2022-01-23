import consumers.VaccineConsumer
import producers.VaccineProducer
import utils.JsonReader
import java.util.*


fun main(args: Array<String>) {
    val jsonReader = JsonReader()
    var producers = LinkedList<VaccineProducer>()
    var consumers = LinkedList<VaccineConsumer>()

    var temperatureInfoList = jsonReader.readProducerJsonInfo("producer1.json")
        temperatureInfoList.forEach { item ->
            val producer = VaccineProducer
            producers.add(producer)
        }

    // Utilizando threads para rodar os produtores
    val t = Thread()
    producers.forEach { producer ->
        Thread(producer).start()
    }

//    var temperatureConsumerInfo = jsonReader.readConsumerJsonInfo("consumer.json")
//    temperatureConsumerInfo.forEach{ item ->
//        val consumer = VaccineConsumer(item)
//        consumers.add(consumer)
//    }
//
//    consumers.forEach { consumer ->
//        Thread(consumer).start()
//    }
    // TODO Produtores criarem partição para cada camara de vacina, com id baseado no array
    // TODO Definir aonde a classe Admin vai ficar

}

