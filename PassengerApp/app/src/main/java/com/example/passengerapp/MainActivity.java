package com.example.passengerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProviderType provider;

    public enum ProviderType {
        BASIC,
        GOOGLE
    }

    private ConstraintLayout parent;
    private Button btnTesting;
    private TextView textViewWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        parent = findViewById(R.id.parent);
        btnTesting = findViewById(R.id.btnTesting);
        textViewWelcome = findViewById(R.id.textViewWelcome);

        setUp();

        btnTesting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar();
                openNearbyDevicesActivity();
            }
        });

        //Data saving (so when the app is closed, the user does not have to re enter the credentials again)
        SharedPreferences.Editor prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
        prefs.putString("email", user.getEmail().toString());
        prefs.putString("provider", provider.name());
        prefs.apply();

        //Get permissions and enable bluetooth if disabled
        getPermissions();

        //Start scanning for bluetooth devices to store them in the SingletonDevices class
        //BluetoothThread bluetoothThread = new BluetoothThread();
        //bluetoothThread.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check if bluetooth was successfuly enabled
        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode==RESULT_OK)
                Toast.makeText(MainActivity.this, "BlueTooth enabled successfuly", Toast.LENGTH_LONG).show();
            if(resultCode==RESULT_CANCELED)
                Toast.makeText(MainActivity.this, "BlueTooth could not be enabled", Toast.LENGTH_LONG).show();
        }
    }

    private void getPermissions() {
        //Location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        PERMISSION_CODE);
            }
        }

        //Setup BlueTooth
        BluetoothAdapter bluetoothAdapter = SingletonDevices.get().getBluetoothAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(MainActivity.this, "Your device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
        }
        //Request the user to enable bluetooth if disabled
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    private void setUp() {
        if(getIntent().hasExtra("user")){
            user = (FirebaseUser) getIntent().getExtras().get("user");
            if(getIntent().hasExtra("provider")){
                provider = (ProviderType) getIntent().getExtras().get("provider");
                textViewWelcome.setText(getString(R.string.welcome) + ", " + user.getEmail() + " (" + provider.name() + ")");
            } else{
                textViewWelcome.setText(user.getDisplayName());
            }
        } else
            textViewWelcome.setText("");
    }

    //Mostrar Snackbar (como un cuadro de dialogo abajo tipo system.out.println o system.out)
    private void showSnackBar() {
        Snackbar.make(parent, "Testing", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Retry clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    //Top bar navigation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Listener para cuando se pulsa la rueda de ajustes
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.settings_menu:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_menu:
                //Remove data saved
                SharedPreferences.Editor prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
                prefs.clear();
                prefs.apply();

                mAuth.signOut();
                //openAuthActivity();
                onBackPressed();
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Methods that change the screen of the app
     */
    private void openNearbyDevicesActivity() {
        Intent intent = new Intent(this, NearbyDevicesActivity.class);
        startActivity(intent);
    }
    private void openAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

}