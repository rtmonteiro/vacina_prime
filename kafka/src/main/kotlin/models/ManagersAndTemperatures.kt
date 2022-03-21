package models

class ManagersAndTemperatures(tempInfo: TemperatureInfo) {
    var temperatureInfo: TemperatureInfo? = tempInfo
    var managerCoordinates = ArrayList<ManagerCoordinates>()

    constructor(tempInfo: TemperatureInfo, manager: ManagerCoordinates) : this(tempInfo) {
        this.managerCoordinates.add(manager)
    }

    public fun addManager(manager: ManagerCoordinates) {
        managerCoordinates.add(manager)
    }

}