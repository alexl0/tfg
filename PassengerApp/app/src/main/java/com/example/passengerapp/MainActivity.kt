
package com.example.passengerapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.passengerapp.bluetooth.ChatServer
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    enum class ProviderType {
        BASIC, GOOGLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFromDBToMemory()
    }

    private fun loadFromDBToMemory() {
        var sqLiteManager = SQLiteManager.instanceOfDatabase(this)
        sqLiteManager.populateHistoryListArray()
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
            R.id.help_menu -> {
                val intent = Intent(this, PdfActivity::class.java);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item)
    }


}