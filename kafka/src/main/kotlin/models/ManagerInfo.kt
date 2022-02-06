package models

import kotlinx.serialization.Serializable

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

