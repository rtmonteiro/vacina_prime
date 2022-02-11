package models

import kotlinx.serialization.Serializable

@Serializable
class ManagerInfo{
    lateinit var id: String
    lateinit var name: String
    lateinit var phone: String
    lateinit var email: String
    lateinit var initialCoordinate: Coordinate

    fun ManagerInfo() {} // Required for ORMlite

    fun ManagerInfo(id: String, name: String, phone: String, email: String, initialCoordinate: Coordinate) {
        this.id = id
        this.name = name
        this.phone = phone
        this.email = email
        this.initialCoordinate = initialCoordinate
    }



}

