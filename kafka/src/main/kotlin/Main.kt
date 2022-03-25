import com.lenkeryan.kafka.utils.JsonReader
import com.lenkeryan.kafka.consumers.ManagerConsumer
import com.lenkeryan.kafka.consumers.VaccineConsumer
import com.lenkeryan.kafka.models.TemperatureConsumerInfo

fun main() {
    val jsonReader = JsonReader()

    // <--- Seção Notifications --->
//    val notificationsConsumer = NotificationConsumer()
//    Thread(notificationsConsumer).start()

    // <--- Seção Managers --->
    val managerConsumer = ManagerConsumer()
    Thread(managerConsumer).start()

//    // <--- Seção Freezers --->
    val vaccineConsumers = ArrayList<VaccineConsumer>()
    val freezerConsumersData: ArrayList<TemperatureConsumerInfo> = jsonReader.readConsumerJsonList("vaccineConsumer/consumers.json")

    freezerConsumersData.forEach { freezer ->
        val vacConsumer = VaccineConsumer(TemperatureConsumerInfo(freezer.id, freezer.hospital))
        vaccineConsumers.add(vacConsumer)
    }

    vaccineConsumers.forEach { consumer ->
        Thread(consumer).start()
    }
}
