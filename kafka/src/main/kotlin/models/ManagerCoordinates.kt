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

    @DatabaseField(generatedId = true, canBeNull = false)
    var id: Int? = null

    @DatabaseField(canBeNull = false)
    var lat: Double? = null

    @DatabaseField(canBeNull = false)
    var lon: Double? = null

    @DatabaseField(canBeNull = false, foreign = true)
    var manager: ManagerInfo? = null

    @DatabaseField()
    var created_at: Long = System.currentTimeMillis()

    constructor() {} // Required for ORM

    constructor(lat: Double, lon: Double, manager: ManagerInfo) {
        this.lat = lat
        this.lon = lon
        this.manager = manager
    }
}