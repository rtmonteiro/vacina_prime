package database

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import models.Account
class AccountDao(connectionSource: ConnectionSource?, dataClass: Class<Account>?) :
    BaseDaoImpl<Account, String>(connectionSource, dataClass) {

}