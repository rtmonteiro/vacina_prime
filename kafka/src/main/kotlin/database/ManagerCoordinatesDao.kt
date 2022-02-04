package database

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import models.ManagerCoordinates

class ManagerCoordinatesDao(connectionSource: ConnectionSource?, dataClass: Class<ManagerCoordinates>?) :
    BaseDaoImpl<ManagerCoordinates, String>(connectionSource, dataClass) {

}