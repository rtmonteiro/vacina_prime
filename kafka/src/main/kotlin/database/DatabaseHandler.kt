package br.lenkeryan.kafka.database

import br.lenkeryan.kafka.models.ManagerInfo
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import database.AccountDao
import database.ManagerDao
import models.Account

class DatabaseHandler () {
    val pathToDb = "/Users/matheuslenke/Developer/College/2021-2/Kafka/vacina_prime/kafka/src/main/db/vaccineDB"
    val connectionSource = JdbcConnectionSource("jdbc:sqlite:$pathToDb")

    val managerDAO = ManagerDao(connectionSource, ManagerInfo::class.java)

    init {
        TableUtils.createTable(managerDAO)
    }
}