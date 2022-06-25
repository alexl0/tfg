package com.example.passengerapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link seeHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class seeHistoryFragment extends Fragment {

    private ListView listViewHistory;
    private ArrayAdapter<String> myAdapter;
    Button deleteHistoryButton;
    LinearLayout deleteHistoryBorder;
    AlertDialog.Builder builder;
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

        deleteHistoryButton = view.findViewById(R.id.deleteHistoryButton);
        deleteHistoryBorder = view.findViewById(R.id.deleteHistoryBorder);
        deleteHistoryBorder.setBackgroundColor(Color.parseColor("#FF0000"));

        deleteHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask user first
                builder = new AlertDialog.Builder(requireContext());

                //Setting message manually and performing action on button click
                builder.setMessage(R.string.deleteHistoryConfirmDialog).setTitle(R.string.Warning)
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Delete history
                                SingletonClass.get().deleteHistory();
                                myAdapter.notifyDataSetChanged();
                                SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(requireContext());
                                sqLiteManager.deleteHistory();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }
}