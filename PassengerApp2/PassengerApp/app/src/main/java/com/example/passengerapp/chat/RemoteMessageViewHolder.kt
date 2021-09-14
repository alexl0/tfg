
package com.example.passengerapp.chat

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.bluetooth.Message
import com.example.passengerapp.R

class RemoteMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val messageText = itemView.findViewById<TextView>(R.id.message_text)

    fun bind(message: Message.RemoteMessage) {
        messageText.text = message.text
    }
}