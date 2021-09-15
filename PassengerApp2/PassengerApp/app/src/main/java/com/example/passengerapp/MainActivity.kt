
package com.example.passengerapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.passengerapp.bluetooth.ChatServer
import com.google.firebase.auth.FirebaseAuth

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


    //Top bar navigation
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu);
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    //Top bar navigation listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu -> {
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.logout_menu -> {
                //Remove data saved
                val prefs =
                    getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
                prefs.clear()
                prefs.apply()
                FirebaseAuth.getInstance().signOut()

                //Open auth activity
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)

                Toast.makeText(this, getText(R.string.logoutSuccess), Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}