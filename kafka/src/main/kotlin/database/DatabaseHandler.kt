package br.lenkeryan.kafka.database

import br.lenkeryan.kafka.models.ManagerInfo
import com.j256.ormlite.table.TableUtils
import database.ManagerDao
import com.j256.ormlite.jdbc.JdbcConnectionSource
import database.ManagerCoordinatesDao
import models.ManagerCoordinates

object DatabaseHandler {
    val pathToDb = "/Users/matheuslenke/Developer/College/2021-2/Kafka/vacina_prime/kafka/src/main/db/vaccineDB"
    val connectionSource = JdbcConnectionSource("jdbc:sqlite:$pathToDb")

    val managerDAO = ManagerDao(connectionSource, ManagerInfo::class.java)
    val managerCoordinatesDao = ManagerCoordinatesDao(connectionSource, ManagerCoordinates::class.java)

    fun init() {
        TableUtils.createTableIfNotExists(connectionSource, ManagerInfo::class.java)
        TableUtils.createTableIfNotExists(connectionSource, ManagerCoordinates::class.java)
    }
}