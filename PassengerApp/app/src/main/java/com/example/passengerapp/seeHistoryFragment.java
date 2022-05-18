package com.example.passengerapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link seeHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class seeHistoryFragment extends Fragment {

    private ListView listViewHistory;
    private ArrayAdapter<String> myAdapter;

    public seeHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment seeHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static seeHistoryFragment newInstance(String param1, String param2) {
        seeHistoryFragment fragment = new seeHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_see_history, container, false);
        listViewHistory = view.findViewById(R.id.listViewHistory);
        myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,SingletonClass.get().getHistory());
        listViewHistory.setAdapter(myAdapter);

        return view;
    }
}