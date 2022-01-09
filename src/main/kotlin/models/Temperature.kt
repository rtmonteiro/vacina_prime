package models

import kotlinx.serialization.Serializable

@Serializable
class Temperature {
    var value: Double = 0.0

    constructor(value: Double) {
        this.value = value
    }
    public constructor() {}
}