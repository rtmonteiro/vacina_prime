package models

import br.lenkeryan.kafka.models.ManagerInfo
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.time.LocalDate
import java.time.temporal.TemporalField
import java.util.*
import kotlin.time.*
@Serializable
@DatabaseTable(tableName = "manager_coordinates")
class ManagerCoordinates {
    var id: Int? = null
    var lat: Double
    var lon: Double
    var manager: ManagerInfo

    constructor(lat: Double, lon: Double, manager: ManagerInfo) {
        this.lat = lat
        this.lon = lon
        this.manager = manager
    }
}