package br.lenkeryan.kafka.models

import kotlinx.serialization.Serializable

@Serializable
data class ManagerInfo(
    val id: String,
    val name: String,
    val phone: String,
    val email: String
) {
    var coordinate: Coordinate? = null

    constructor(id: String, name: String, phone: String, email: String, coordinate: Coordinate) : this(
        id, name, phone, email
    ) {

        this.coordinate = coordinate
    }
}