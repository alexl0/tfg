
package com.example.passengerapp.chat

import android.bluetooth.BluetoothAdapter
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
import com.example.passengerapp.bluetooth.ChatServer
import com.example.passengerapp.bluetooth.Message
import com.example.passengerapp.databinding.FragmentBluetoothChatBinding
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager
import android.app.AlertDialog
import com.jakewharton.processphoenix.ProcessPhoenix

import android.app.PendingIntent

import android.content.Intent
import android.widget.Toast
import com.example.passengerapp.*
import java.io.File


private const val TAG = "BluetoothChatFragment"

class BluetoothChatFragment : Fragment() {


    private var _binding: FragmentBluetoothChatBinding? = null
    // this property is valid between onCreateView and onDestroyView.
    private val binding: FragmentBluetoothChatBinding
        get() = _binding!!

    private var bluetoothAdapter: BluetoothAdapter? = null

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
                 * It's not good because the passenger could use the bus once and once again
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

        /**
         * Set up bluetooth adapter to change bluetooth name
         */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        changeDeviceName()

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

        binding.disconnectFromDevice.setOnClickListener {
            //Ask user first
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.Warning)
            builder.setMessage(R.string.wantToDisconnect)
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                disconnectFromDeviceFunction()
            }
            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(requireContext(),
                    android.R.string.no, Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }

        binding.seeHistory.setOnClickListener {
            findNavController().navigate(R.id.action_bluetoothChatFragment_to_seeHistoryFragment)
        }

        return binding.root
    }

    fun disconnectFromDeviceFunction(){
        //Stop server
        ChatServer.hardStopServer()
        //Delete cache
        deleteCache(requireContext())
        //Restart app
        val mStartActivity = Intent(context, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(
            requireContext(),
            mPendingIntentId,
            mStartActivity,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
        System.exit(0)
        //Restart the app
        try{
            ProcessPhoenix.triggerRebirth(context);
        } catch(e:IllegalStateException){
            //The IllegalStateException is expected, everithing is working fine
            throw e //The application must crash to restart completely. It's a trick
        } catch (e: Exception){
            Log.d(TAG, "Something went wrong when restarting the app to disconnect from the vehicle.")
        }
    }

    fun changeDeviceName() {
        Log.i(
            "BT",
            "localdevicename : " + bluetoothAdapter!!.name + " localdeviceAddress : " + bluetoothAdapter!!.address
        )
        bluetoothAdapter!!.name = "PassengerAppDevice"
        Log.i(
            "BT",
            "localdevicename : " + bluetoothAdapter!!.name + " localdeviceAddress : " + bluetoothAdapter!!.address
        )
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
        binding.disconnectFromDevice.isEnabled = true;
        //binding.connectDevices.visibility = View.GONE;
        //binding.disconnectFromDevice.visibility = View.VISIBLE;

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
        val deviceName = device.name
        val deviceNameWithZonesWordLocated = deviceName.subSequence(0, deviceName.length-6).toString() + "\n" +
                                             deviceName.subSequence(deviceName.length-6, deviceName.length-5).toString()+ " " + getString(R.string.zonesMin)
        val chattingWithString = resources.getString(R.string.connected_device_message) + " " + deviceNameWithZonesWordLocated + "\n" + currentDate
        //Add to local history in case
        var deviceNameSub:String = deviceName.subSequence(0, deviceName.length-5).toString()
        var definitiveHistoryField = deviceNameSub + " " + getString(R.string.zonesMin) + " " + currentDate
        SingletonClass.get().history.add(definitiveHistoryField)
        var sqliteManager = SQLiteManager.instanceOfDatabase(requireContext())
        sqliteManager.addDataFieldToDB(definitiveHistoryField)
        binding.connectDeviceMessage.text = chattingWithString
        binding.connectDeviceMessage.setBackgroundColor(Color.parseColor("#09ff00"))
        binding.connectDeviceMessage.setTextColor(Color.parseColor("#000000"))
    }

    private fun showDisconnected() {
        hideKeyboard()
        //binding.notConnectedContainer.visible()
        binding.connectedContainer.gone()
        binding.connectDeviceMessage.text = resources.getString(R.string.no_connected_device_message)
        binding.connectDeviceMessage.setBackgroundColor(Color.parseColor("#ff0000"))
        binding.connectDeviceMessage.setTextColor(Color.parseColor("#ffffff"))

        binding.connectDevices.isEnabled = true;
        binding.disconnectFromDevice.isEnabled = false;
        //binding.connectDevices.visibility = View.VISIBLE;
        //binding.disconnectFromDevice.visibility = View.GONE;

    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    //Delete Cache in order for the restart method to work
    fun deleteCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory()) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile()) {
            dir.delete()
        } else {
            false
        }
    }

}