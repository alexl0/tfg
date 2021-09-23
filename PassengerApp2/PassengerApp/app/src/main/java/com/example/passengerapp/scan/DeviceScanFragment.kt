
package com.example.passengerapp.scan

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.*
import com.example.passengerapp.bluetooth.ChatServer
import com.example.passengerapp.databinding.FragmentDeviceScanBinding
import com.example.passengerapp.scan.DeviceScanViewState.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "DeviceScanFragment"
const val GATT_KEY = "gatt_bundle_key"

class DeviceScanFragment : Fragment() {

    private var _binding: FragmentDeviceScanBinding? = null

    var db: FirebaseFirestore? = null

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
        /**
         * Remove 1 trip
         */
        //Set up database
        db = FirebaseFirestore.getInstance()

        //The name of the vehicle must be an array of characters finished by a number and 'Z'
        //For example: Bus L5 3Z (It's a bus, it's from line 5 and it's a 3 zones bus)
        val str: String = device.name.toString()
        val numberOnly = str.substring(str.length -2, str.length-1)
        val numZonesSelected = numberOnly.toInt()

        val numTripsSelected = -1

        val currentUser = SingletonClass.get().hashObjects["user"] as FirebaseUser?
        val currentUserEmail = currentUser!!.email

        //Get current user remaining trips with that zone

        //Get current user remaining trips with that zone
        val docRef = db!!.collection("users").document(currentUserEmail!!)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                // Create a new user with the updated data
                val userHash: MutableMap<String, Any> = HashMap()
                for (i in 1..7) {
                    //Si el usuario ya tiene viajes para la zona i
                    if (document.exists() && document.data!!["tripsZone$i"] != null) {
                        //Y ademas es la que ha seleccionado
                        if (i == numZonesSelected) {
                            //Se le suman
                            val currentTripsBefore =
                                (document.data!!["tripsZone$numZonesSelected"] as Long?)!!.toInt()
                            userHash["tripsZone$numZonesSelected"] =
                                numTripsSelected + currentTripsBefore
                        } else {
                            //Se le ponen los viajes que ya tenga
                            val currentTripsBefore =
                                (document.data!!["tripsZone$i"] as Long?)!!.toInt()
                            userHash["tripsZone$i"] = currentTripsBefore
                        }
                    } else {
                        if (i == numZonesSelected) {
                            userHash["tripsZone$numZonesSelected"] = numTripsSelected
                        } else  //Se le ponen 0 viajes
                            userHash["tripsZone$i"] = 0
                    }
                }

                //Add/update the user to/from the database
                db!!.collection("users").document(currentUserEmail!!).set(userHash)
                //showSnackBar()
            } else {
                Log.d("ERROR", "get failed with ", task.exception)
            }
        }


        ChatServer.setCurrentChatConnection(device)
        // navigate back to chat fragment
        findNavController().popBackStack()
    }

    //Mostrar Snackbar (como un cuadro de dialogo abajo tipo system.out.println o system.out)
    private fun showSnackBar() {
        Snackbar.make(binding.frameLayoutParentFragmentBTChat, getText(R.string.tripBought), Snackbar.LENGTH_INDEFINITE)
            .setAction(getText(R.string.ok)) {
                //Toast.makeText(getActivity(), getText(R.string.ok), Toast.LENGTH_SHORT).show();
            }
            .show()
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
        binding.noDevices.visible()

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
