package com.example.passengerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class NearbyDevicesActivity extends AppCompatActivity {

    private ListView devicesListView;
    private Spinner studentsSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_devices);

        //Action bar with back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Action bar title
        setTitle(getString(R.string.nearbyDevices));

        //Get variables from xml
        devicesListView = findViewById(R.id.devicesListView);
        studentsSpinner = findViewById(R.id.studentsSpinner);

        /**
         * ListView devicesListView
         */

        //Create sample data
        ArrayList<String> devicesSampleData = new ArrayList<>();
        for(int i=0; i<=20;i++){
            devicesSampleData.add("device"+i);
        }

        //Create the adapter in order to fetch the list to our listView
        ArrayAdapter<String> devicesAdapter = new ArrayAdapter<>(
                this,
                //We use a simple prebuilt layout
                android.R.layout.simple_list_item_1,
                devicesSampleData
        );
        //Pass the adapter to our listView
        devicesListView.setAdapter(devicesAdapter);

        //Define a listener for the list items
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NearbyDevicesActivity.this, devicesSampleData.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Spinner devicesListSpinner
         */
        //Create sample data
        ArrayList<String> studentsSampleData = new ArrayList<>();
        for(int i=0; i<=20;i++){
            studentsSampleData.add("student"+i);
        }

        //Create the adapter in order to fetch the list to our listView
        ArrayAdapter<String> studentsAdapter = new ArrayAdapter<>(
                this,
                //We use a simple prebuilt layout
                android.R.layout.simple_spinner_dropdown_item,
                studentsSampleData
        );
        //Pass the adapter to our studentsSpinner
        studentsSpinner.setAdapter(studentsAdapter);

        //Define a listener for the list items
        studentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NearbyDevicesActivity.this, studentsSampleData.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(NearbyDevicesActivity.this, "You didn't select anything", Toast.LENGTH_SHORT).show();
            }
        });
    }
}