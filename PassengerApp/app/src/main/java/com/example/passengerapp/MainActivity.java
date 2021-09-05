package com.example.passengerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

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