
package com.example.passengerapp.bluetooth

/**
 * This sealed class represents the messages sent between connected devices.
 * The RemoteMessage class represents a message coming from a remote device.
 * The LocalMessage class represents a message the user wants to send to the remote device.
 *
 * @param text is the message text the user sends to the other connected device.
 */
sealed class Message(val text: String) {
    class RemoteMessage(text: String) : Message(text)
    class LocalMessage(text: String) : Message(text)
}