package br.lenkeryan.kafka.models

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

@Serializable
@DatabaseTable(tableName = "managers")
class ManagerInfo{
    @DatabaseField(id = true)
    lateinit var id: String

    @DatabaseField()
    lateinit var name: String

    @DatabaseField()
    lateinit var phone: String

    @DatabaseField()
    lateinit var email: String

    fun ManagerInfo() {} // Required for ORMlite

    fun ManagerInfo(id: String, name: String, phone: String, email: String) {
        this.id = id
        this.name = name
        this.phone = phone
        this.email = email
    }

    var coordinate: Coordinate? = null

}

