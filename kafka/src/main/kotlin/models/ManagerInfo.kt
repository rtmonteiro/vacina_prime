package br.lenkeryan.kafka.models

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

@Serializable
class ManagerInfo{
    lateinit var id: String
    lateinit var name: String
    lateinit var phone: String
    lateinit var email: String
    lateinit var coordinate: Coordinate

    fun ManagerInfo() {} // Required for ORMlite

    fun ManagerInfo(id: String, name: String, phone: String, email: String, coordinate: Coordinate) {
        this.id = id
        this.name = name
        this.phone = phone
        this.email = email
        this.coordinate = coordinate
    }



}

