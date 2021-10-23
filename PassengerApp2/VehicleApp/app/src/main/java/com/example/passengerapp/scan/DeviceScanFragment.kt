
package com.example.passengerapp.scan

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.*
import com.example.passengerapp.R
import com.example.passengerapp.bluetooth.ChatServer
import com.example.passengerapp.databinding.FragmentDeviceScanBinding
import com.example.passengerapp.scan.DeviceScanViewState.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*


private const val TAG = "DeviceScanFragment"
const val GATT_KEY = "gatt_bundle_key"

class DeviceScanFragment : Fragment() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var _binding: FragmentDeviceScanBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding
        get() = _binding!!

    private val viewModel: DeviceScanViewModel by viewModels()

    private val deviceScanAdapter by lazy {
        DeviceScanAdapter(onDeviceSelected)
    }

    private val viewStateObserver = Observer<DeviceScanViewState> { state ->
        when (state) {
            is ActiveScan -> showLoading()
            is ScanResults -> showResults(state.scanResults)
            is Error -> showError(state.message)
            is AdvertisementNotSupported -> showAdvertisingError()
        }.exhaustive
    }

    private val onDeviceSelected: (BluetoothDevice) -> Unit = { device ->
        ChatServer.setCurrentChatConnection(device)
        // navigate back to chat fragment
        findNavController().popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeviceScanBinding.inflate(inflater, container, false)
        val devAddr = getString(R.string.your_device_address) + ChatServer.getYourDeviceAddress()
        binding.yourDeviceAddr.text = devAddr
        binding.deviceList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceScanAdapter
        }

        /**
         * Get current user
         */
        val currentUser:FirebaseUser = SingletonClass.get().hashObjects["user"] as FirebaseUser
        val currentUserEmail: String = currentUser!!.email as String

        /**
         * Set up listeners for the 2 buttons
         */
        binding.buttonChangeName.setOnClickListener(View.OnClickListener {
            db.collection("plates").document(currentUserEmail).set(
                hashMapOf("name" to binding.textInputEditTextNewName.text.toString())
            )
        })

        binding.buttonChangePlate.setOnClickListener(View.OnClickListener {
            db.collection("plates").document(currentUserEmail).set(
                hashMapOf("plate" to binding.textInputEditTextNewPlate.text.toString())
            )
        })

        /**
         * Add listener to database changes
         */
        val docRef = db.collection("plates").document(currentUserEmail)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
                val stringData = snapshot.data.toString()
                binding.currentPlateTextView.setText(binding.currentPlateTextView.text.toString() + " " + stringData.substring(stringData.lastIndexOf("=") + 1, stringData.length - 1))
            } else {
                Log.d(TAG, "Current data: null")
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.device_list_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner, viewStateObserver)
    }

    private fun showLoading() {
        Log.d(TAG, "showLoading")
        binding.scanning.visible()

        binding.deviceList.gone()
        binding.noDevices.gone()
        binding.error.gone()
        binding.chatConfirmContainer.gone()
    }

    private fun showResults(scanResults: Map<String, BluetoothDevice>) {
        if (scanResults.isNotEmpty()) {
            binding.deviceList.visible()
            deviceScanAdapter.updateItems(scanResults.values.toList())

            binding.scanning.gone()
            binding.noDevices.gone()
            binding.error.gone()
            binding.chatConfirmContainer.gone()
        } else {
            showNoDevices()
        }
    }

    private fun showNoDevices() {
        //nodevicebinding.noDevices.visible()

        binding.deviceList.gone()
        binding.scanning.gone()
        binding.error.gone()
        binding.chatConfirmContainer.gone()
    }

    private fun showError(message: String) {
        Log.d(TAG, "showError: ")
        binding.error.visible()
        binding.errorMessage.text = message

        // hide the action button if one is not provided
        binding.errorAction.gone()
        binding.scanning.gone()
        binding.noDevices.gone()
        binding.chatConfirmContainer.gone()
    }

    private fun showAdvertisingError() {
        showError("BLE advertising is not supported on this device")
    }
}
