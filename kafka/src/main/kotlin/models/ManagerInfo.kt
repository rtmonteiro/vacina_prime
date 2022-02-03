package br.lenkeryan.kafka.models

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.serialization.Serializable

@DatabaseTable(tableName = "managers")
@Serializable
data class ManagerInfo(
    @DatabaseField(id = true)
    val id: String,

    @DatabaseField()
    val name: String,

    @DatabaseField()
    val phone: String,

    @DatabaseField()
    val email: String
) {
    var coordinate: Coordinate? = null

    constructor(id: String, name: String, phone: String, email: String, coordinate: Coordinate) : this(
        id, name, phone, email
    ) {

        this.coordinate = coordinate
    }
}