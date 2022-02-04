package models

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import database.AccountDao
import kotlinx.serialization.Serializable

@Serializable
@DatabaseTable(tableName = "accounts")
class Account {
    @DatabaseField(id = true)
    private var name: String? = null

    @DatabaseField
    private var password: String? = null

    fun Account() {
        // ORMLite needs a no-arg constructor
    }

    fun Account(name: String?, password: String?) {
        this.name = name
        this.password = password
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password
    }
}