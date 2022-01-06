package models

class Vaccine(marca: String) {
    var marca: String? = null
    var lote: String? = null
    var quantidade: Int = 0
    var temperaturaMax: Double = 0.0
    var temperaturaMin: Double = 0.0
}