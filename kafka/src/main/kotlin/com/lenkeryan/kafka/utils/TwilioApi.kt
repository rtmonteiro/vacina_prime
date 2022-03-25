package br.lenkeryan.kafka.utils

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

class TwilioApi {

    val ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID")
    val AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN")

    init {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN)
    }

    fun sendMessage(phoneNumber: String, messageText: String) {
        val message = Message.creator(
            PhoneNumber(phoneNumber),
            PhoneNumber("+17655387554"),
            messageText
        )
            .create()

        println(message.sid)
    }
}