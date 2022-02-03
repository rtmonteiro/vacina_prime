package database

import br.lenkeryan.kafka.models.ManagerInfo
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import models.Account

class ManagerDao(connectionSource: ConnectionSource?, dataClass: Class<ManagerInfo>?) :
    BaseDaoImpl<ManagerInfo, String>(connectionSource, dataClass) {

}