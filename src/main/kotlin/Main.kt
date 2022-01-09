import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.TemperatureProducerInfo
import temperature.VaccineProducer
import java.io.File
import java.util.*


fun main(args: Array<String>) {
    println("Hello World!")

    var producers = LinkedList<VaccineProducer>()

    var temperatureInfoList = readJsonInfo()
        temperatureInfoList.forEach { item ->
            val producer = VaccineProducer(item)
            producers.add(producer)
        }
    // TODO Produtores criarem partição para cada camara de vacina, com id baseado no array
    // TODO Definir aonde a classe Admin vai ficar

}

private fun readJsonInfo() : List<TemperatureProducerInfo>{
    val filename = "producer1.json"
    val fileContent = File(filename).readText()

    return if (fileContent != null)
             Json.decodeFromString<List<TemperatureProducerInfo>>(fileContent)
            else LinkedList()
}

