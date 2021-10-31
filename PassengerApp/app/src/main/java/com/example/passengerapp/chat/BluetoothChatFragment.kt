
package com.example.passengerapp.chat

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.bluetooth.Message
import com.example.passengerapp.R
import com.example.passengerapp.bluetooth.ChatServer
import com.example.passengerapp.databinding.FragmentBluetoothChatBinding
import com.example.passengerapp.gone
import com.example.passengerapp.visible
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.firestore.DocumentReference

import com.example.passengerapp.SingletonClass
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "BluetoothChatFragment"

class BluetoothChatFragment : Fragment() {


    private var _binding: FragmentBluetoothChatBinding? = null
    // this property is valid between onCreateView and onDestroyView.
    private val binding: FragmentBluetoothChatBinding
        get() = _binding!!

    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when(state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                Log.d(TAG, "Gatt connection observer: have device $device")
                chatWith(device)
            }
            is DeviceConnectionState.Disconnected -> {
                showDisconnected()

                /**
                 * Restart server.
                 * Because if not, when disconecting the vehicle app and connecting it again, it remains conected.
                 * It's not good because the passenger could use the buse once and once again
                 */
                ChatServer.stopServer()
                ChatServer.startServer(requireActivity().application)
            }
        }

    }

    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        Log.d(TAG, "Connection request observer: have device $device")
        ChatServer.setCurrentChatConnection(device)
    }

    private val messageObserver = Observer<Message> { message ->
        Log.d(TAG, "Have message ${message.text}")
        adapter.addMessage(message)
    }

    private val adapter = MessageAdapter()

    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBluetoothChatBinding.inflate(inflater, container, false)

        Log.d(TAG, "chatWith: set adapter $adapter")
        binding.messages.layoutManager = LinearLayoutManager(context)
        binding.messages.adapter = adapter

        showDisconnected()

        binding.connectDevices.setOnClickListener {
            findNavController().navigate(R.id.action_find_new_device)
        }

        binding.buyVouchers.setOnClickListener {
            findNavController().navigate(R.id.action_bluetoothChatFragment_to_buyVouchersFragment)
        }

        binding.myVouchers.setOnClickListener {
            findNavController().navigate(R.id.action_bluetoothChatFragment_to_myVouchersFragment)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.app_name)
        ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun chatWith(device: BluetoothDevice) {
        //binding.connectedContainer.visible()
        //binding.notConnectedContainer.gone()
        binding.connectDevices.isEnabled = false;

        /*val chattingWithString = resources.getString(R.string.chatting_with_device, device.address)
        binding.connectedDeviceName.text = chattingWithString
        binding.sendMessage.setOnClickListener {
            val message = binding.messageText.text.toString()
            // only send message if it is not empty
            if (message.isNotEmpty()) {
                ChatServer.sendMessage(message)
                // clear message
                binding.messageText.setText("")
            }
        }*/
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val chattingWithString = resources.getString(R.string.connected_device_message) + device.name + "\n" + currentDate
        binding.connectDeviceMessage.text = chattingWithString
        binding.connectDeviceMessage.setBackgroundColor(Color.parseColor("#09ff00"))
    }

    private fun showDisconnected() {
        hideKeyboard()
        //binding.notConnectedContainer.visible()
        binding.connectedContainer.gone()
        binding.connectDeviceMessage.text = resources.getString(R.string.no_connected_device_message)
        binding.connectDeviceMessage.setBackgroundColor(Color.parseColor("#ff0000"))
        binding.connectDevices.isEnabled = true;
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}