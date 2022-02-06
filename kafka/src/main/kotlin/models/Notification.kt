package models

import br.lenkeryan.kafka.models.ManagerInfo
import kotlinx.serialization.Serializable

enum class NotificationType {
    DISCARD,
    WARN
}

@Serializable
class Notification {
    var notificationType: NotificationType
    var willNotificateAllManagers = false
    var message: String
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