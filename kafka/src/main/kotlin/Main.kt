import consumers.VaccineConsumer
import br.lenkeryan.kafka.utils.JsonReader
import consumers.ManagerConsumer
import models.TemperatureConsumerInfo
import consumers.NotificationConsumer

fun main() {
    val jsonReader = JsonReader()

    // <--- Seção Notifications --->
    val notificationsConsumer = NotificationConsumer()
    Thread(notificationsConsumer).start()

    // <--- Seção Managers --->
    val managerConsumer = ManagerConsumer()
    Thread(managerConsumer).start()

//    // <--- Seção Freezers --->
    val vaccineConsumers = ArrayList<VaccineConsumer>()
    val freezerConsumersData = jsonReader.readConsumerJsonList("vaccineConsumer/consumers.json")

    freezerConsumersData.forEach { freezer ->
        val vacConsumer = VaccineConsumer(TemperatureConsumerInfo(freezer.id, freezer.hospital))
        vaccineConsumers.add(vacConsumer)
    }

    vaccineConsumers.forEach { consumer ->
        Thread(consumer).start()
    }

}
