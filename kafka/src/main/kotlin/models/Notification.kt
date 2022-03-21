package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy

@Serializable
class Notification {
    var notificationType: NotificationType
    var message: String
    var willNotificateAllManagers = false
    var managersToNotificate: ArrayList<ManagerInfo> = ArrayList()

    constructor(type: NotificationType, manager: ManagerInfo, message: String) {
        this.notificationType = type
        this.managersToNotificate.add(manager)
        this.willNotificateAllManagers = false
        this.message = message
    }

    constructor(type: NotificationType, message: String, managers: ArrayList<ManagerInfo>) {
        this.notificationType = type
        this.message = message
        this.willNotificateAllManagers = true
        this.managersToNotificate = managers
    }

}