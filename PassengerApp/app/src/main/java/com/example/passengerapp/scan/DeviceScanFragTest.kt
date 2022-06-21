
package com.example.passengerapp.scan

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passengerapp.*
import com.example.passengerapp.bluetooth.ChatServer
import com.example.passengerapp.databinding.FragmentDeviceScanBinding
import com.example.passengerapp.scan.DeviceScanViewState.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "DeviceScanFragTest"

class DeviceScanFragTest : Fragment() {

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

        //The name of the vehicle must be an array of characters finished by a number and 'zones'
        //For example: Bus L5 3zones (It's a bus, it's from line 5 and it's a 3 zones bus)
        val str: String = device.name.toString()
        val numberOnly = str.substring(str.length -6, str.length-5)
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
                            if(document.data!!["tripsZone$i"].toString().toInt()>=1){
                                //Se le suman
                                val currentTripsBefore =
                                    (document.data!!["tripsZone$numZonesSelected"] as Long?)!!.toInt()
                                userHash["tripsZone$numZonesSelected"] =
                                    numTripsSelected + currentTripsBefore
                                //Se hace lo que se tiene que hacer cuando se tienen viajes
                                //"Connect" with the bus/train
                                ChatServer.setCurrentChatConnection(device)
                            } else{
                                //TODO Mostrar un mensaje al usuario indicando que no tiene bonos suficientes
                                //Toast.makeText(requireActivity(), getText(R.string.notEnoughVoyages), Toast.LENGTH_SHORT).show();
                            }
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
        binding.deviceList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceScanAdapter
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle("Testing Bluetooth")
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

        //Borrar los dispositivos de los pasajeros para que no aparezcan al buscar
        val nuevosResultados: MutableList<BluetoothDevice> = ArrayList()
        for (i in scanResults.values.toList()) {
            if(i.getName()!=null && i.getName()!="PassengerAppDevice")
                nuevosResultados.add(i)
        }

        if (nuevosResultados.isNotEmpty()) {
            binding.deviceList.visible()
            deviceScanAdapter.updateItems(nuevosResultados.toList())

            binding.scanning.gone()
            binding.noDevices.gone()
            binding.error.gone()
            binding.chatConfirmContainer.gone()

            SingletonClass.get().testingBTSuccess=true;
        } else {
            showNoDevices()
            SingletonClass.get().testingBTSuccess=false;
        }
        if(!SingletonClass.get().weHaveTestedBT && SingletonClass.get().testingBT){
            SingletonClass.get().weHaveTestedBT=true
            findNavController().navigate(R.id.action_deviceScanFragTest_to_testFragment)
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
