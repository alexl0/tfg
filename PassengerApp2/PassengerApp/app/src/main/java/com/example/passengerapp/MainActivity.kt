
package com.example.passengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.passengerapp.bluetooth.ChatServer

class MainActivity : AppCompatActivity() {

    enum class ProviderType {
        BASIC, GOOGLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Run the chat server as long as the app is on screen
    override fun onStart() {
        super.onStart()
        ChatServer.startServer(application)
    }

    override fun onStop() {
        super.onStop()
        ChatServer.stopServer()
    }

}