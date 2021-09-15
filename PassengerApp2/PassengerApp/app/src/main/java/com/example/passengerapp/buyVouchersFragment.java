package com.example.passengerapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link buyVouchersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class buyVouchersFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public buyVouchersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment buyVouchersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static buyVouchersFragment newInstance(String param1, String param2) {
        buyVouchersFragment fragment = new buyVouchersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Fragment title (up of the screen)
        requireActivity().setTitle(getString(R.string.buy_vouchers_prompt));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy_vouchers, container, false);

        //Set up spinners
        setupSpinners(view);

        return view;
    }

    /**
     * Set up spinners
     * @param view
     */
    private void setupSpinners(View view) {
        Spinner spinnerZones = (Spinner) view.findViewById(R.id.spinnerNumZonesBuy);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.zonesArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerZones.setAdapter(adapter);

        Spinner spinnerTrips = (Spinner) view.findViewById(R.id.spinnerNumTripsBuy);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTrips = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.tripsArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTrips.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerTrips.setAdapter(adapterTrips);

        //Add listener to the spinners
        spinnerZones.setOnItemSelectedListener(this);
        spinnerTrips.setOnItemSelectedListener(this);
    }

    /**
     * Spinner listeners
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        //System.out.println(parent.getItemAtPosition(pos));
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        //System.out.println("Nothing selected");
    }

}