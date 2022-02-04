import br.lenkeryan.kafka.consumers.VaccineConsumer
import br.lenkeryan.kafka.database.DatabaseHandler
import br.lenkeryan.kafka.models.TemperatureConsumerInfo
import br.lenkeryan.kafka.models.Vaccine
import br.lenkeryan.kafka.utils.JsonReader
import br.lenkeryan.kafka.producers.ManagerProducer
import br.lenkeryan.kafka.producers.VaccineProducer
import consumers.ManagerConsumer
import models.ProgramData
import javax.xml.crypto.Data

fun main() {
//    val jsonReader = JsonReader()
//    DatabaseHandler.init()
//
//    // <--- Seção Managers --->
//    val managerProducers = ArrayList<ManagerProducer>()
//    val managerConsumers = ArrayList<ManagerConsumer>()
//    val managersData = DatabaseHandler.managerDAO.queryForAll()
//
//    managersData.forEach { manager ->
//        val producer = ManagerProducer(manager)
//        managerProducers.add(producer)
//    }
//
//    managerProducers.forEach { producer ->
//        Thread(producer).start()
//    }
//
//    Thread(ManagerConsumer).start()
//
//    // <--- Seção Freezers --->
//
//    val vaccineProducers = ArrayList<VaccineProducer>()
//    val vaccineConsumers = ArrayList<VaccineConsumer>()
//
//    val freezersData = jsonReader.readProducerJsonInfo("producer1.json")
//    println(freezersData)
//    val freezerConsumersData = HashMap<String, TemperatureConsumerInfo>()
//
//    freezersData.forEach { freezer ->
//        freezerConsumersData[freezer.hospital] = TemperatureConsumerInfo(freezer.id, freezer.hospital)
//    }
//
//    freezersData.forEach { freezer ->
//        // Cria um produtor para cada freezer
//        println("Produtor criado")
//        val producer = VaccineProducer(freezer)
//        vaccineProducers.add(producer)
//    }
//
//    freezerConsumersData.forEach { consumer ->
//        // Cria um consumer para cada hospital
//        println("Consumer criado")
//        val vacConsumer = VaccineConsumer(consumer.value)
//        vaccineConsumers.add(vacConsumer)
//    }
//
//    vaccineProducers.forEach { producer ->
//        Thread(producer).start()
//    }
//
//    vaccineConsumers.forEach { consumer ->
//        Thread(consumer).start()
//    }

}
