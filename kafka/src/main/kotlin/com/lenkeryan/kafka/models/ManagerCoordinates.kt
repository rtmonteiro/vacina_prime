package com.lenkeryan.kafka.models

import com.j256.ormlite.table.DatabaseTable
import kotlinx.serialization.Serializable

@Serializable
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