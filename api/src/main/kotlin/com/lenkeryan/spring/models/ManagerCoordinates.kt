package com.lenkeryan.spring.models

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