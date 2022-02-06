package models

import kotlinx.serialization.Serializable

@Serializable
class Notification {
    var notificationType: NotificationType
    var message: String
    var willNotificateAllManagers = false
    var managerToNotificate: ManagerInfo? = null

    constructor(type: NotificationType, manager: ManagerInfo, message: String) {
        this.notificationType = type
        this.managerToNotificate = manager
        this.willNotificateAllManagers = false
        this.message = message
    }

    constructor(type: NotificationType, message: String) {
        this.notificationType = type
        this.message = message
        this.willNotificateAllManagers = true
    }

}