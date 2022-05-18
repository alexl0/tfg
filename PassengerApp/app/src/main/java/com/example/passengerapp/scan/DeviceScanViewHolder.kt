
package com.example.passengerapp.scan

import android.bluetooth.BluetoothDevice
import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.R

class DeviceScanViewHolder(
    view: View,
    val onDeviceSelected: (BluetoothDevice) -> Unit
) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val name = itemView.findViewById<TextView>(R.id.device_name)
    private val address = itemView.findViewById<TextView>(R.id.device_address)
    private var bluetoothDevice: BluetoothDevice? = null

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(device: BluetoothDevice) {
        bluetoothDevice = device
        var deviceName:String = device.name
        var deviceNameSub:String = deviceName.subSequence(0, deviceName.length-5).toString()
        name.text = deviceNameSub
        //address.text = device.address
    }

    override fun onClick(view: View) {
        bluetoothDevice?.let { device ->
            onDeviceSelected(device)
        }
    }
}