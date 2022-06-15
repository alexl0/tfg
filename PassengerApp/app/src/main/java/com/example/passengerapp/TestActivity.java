package com.example.passengerapp;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    //UI
    Button testLoginButton;
    Button testBTButton;
    Button testDBButton1;
    Button testDBButton2;
    Button testDBButton3;
    Button testDBButton4;
    Button logoutButton;
    TextView textViewLogin;
    TextView textViewDetect;
    TextView textViewModify;

    //Google Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Testing
    Button testAppButton;
    private final String testEmail = "pruebatests@mail.com";
    private final String testPassword = "pruebatests1";
    private boolean modifyDBisOk = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testLoginButton = findViewById(R.id.testLoginButton);
        testBTButton = findViewById(R.id.testBTButton);
        testDBButton1 = findViewById(R.id.testDBButton1);
        testDBButton2 = findViewById(R.id.testDBButton2);
        testDBButton3 = findViewById(R.id.testDBButton3);
        testDBButton4 = findViewById(R.id.testDBButton4);
        logoutButton = findViewById(R.id.logoutButton);
        textViewLogin = findViewById(R.id.textViewLogin);
        textViewDetect = findViewById(R.id.textViewDetect);
        textViewModify = findViewById(R.id.textViewModify);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        testLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLoginButton.setEnabled(false);
                testSignIn(testEmail, testPassword);
            }
        });
        testDBButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDatabase1();
            }
        });
        testDBButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDatabase2();
            }
        });
        testDBButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDatabase3();
            }
        });
        testDBButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDatabase4();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutButton.setEnabled(false);
                testLoginButton.setEnabled(true);
                testDBButton1.setEnabled(false);
                testDBButton2.setEnabled(false);
                testDBButton3.setEnabled(false);
                testDBButton4.setEnabled(false);
                textViewLogin.setText("-");
                textViewLogin.setTextColor(Color.WHITE);
                logOut();
            }
        });
    }


    /**
     * Sign in with email and password for testing purposes
     * @param email
     * @param password
     */
    private void testSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            textViewLogin.setText("OK");
                            textViewLogin.setTextColor(Color.GREEN);
                            testDBButton1.setEnabled(true);
                            testDBButton2.setEnabled(true);
                            testDBButton3.setEnabled(true);
                            testDBButton4.setEnabled(true);
                            logoutButton.setEnabled(true);
                            Toast.makeText(TestActivity.this, "OK. Logged in successfully.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(TestActivity.this, "Authentication failed with testing credentials.",
                                    Toast.LENGTH_LONG).show();
                            textViewLogin.setText("KO");
                            textViewLogin.setTextColor(Color.RED);
                            Toast.makeText(TestActivity.this, "KO. Error logging in.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void testDatabase1(){
        DocumentReference docRef = db.collection("users").document(testEmail);

        //Put 0 voyages to every zone (remove all voyages)
        addVoyages(docRef, 0);
    }
    private void testDatabase2(){
        DocumentReference docRef = db.collection("users").document(testEmail);

        //Check user has 0 voyages for every zone
        checkVoyages(docRef, 0);
    }
    private void testDatabase3(){
        DocumentReference docRef = db.collection("users").document(testEmail);

        //Put 50 voyages to every zone
        addVoyages(docRef, 50);
    }
    private void testDatabase4(){
        DocumentReference docRef = db.collection("users").document(testEmail);

        //Check user has 50 voyages for every zone
        checkVoyages(docRef, 50);
    }

    /**
     * Puts "numberOfVoyages" to every zone
     * @param docRef
     * @param numberOfVoyages
     */
    private void addVoyages(DocumentReference docRef, int numberOfVoyages) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Create a new user with the updated data
                    Map<String, Object> userHash = new HashMap<>();
                    for(int i=1; i<=7; i++)
                        userHash.put("tripsZone"+i, numberOfVoyages);

                    //Add/update the user to/from the database
                    db.collection("users").document(testEmail).set(userHash);
                    textViewModify.setText("OK");
                    textViewModify.setTextColor(Color.GREEN);
                    Toast.makeText(TestActivity.this, "OK. Every zone has now " + numberOfVoyages + " trips.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("ERROR" + " get failed with " + task.getException());
                    Toast.makeText(TestActivity.this, "ERROR" + " get failed with " + task.getException(),
                            Toast.LENGTH_LONG).show();
                    modifyDBisOk=false;
                    textViewModify.setText("KO");
                    textViewModify.setTextColor(Color.RED);
                }
            }
        });
    }

    /**
     * Checks if the user has "numZones" voyages
     * @param docRef
     * @param numZones
     */
    private void checkVoyages(DocumentReference docRef, int numZones) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(document.getData().get("tripsZone1")!=null) {
                            for (int i = 1; i <= 7; i++) {
                                if (numZones != Integer.parseInt(document.getData().get("tripsZone" + i).toString()))
                                    modifyDBisOk = false;
                            }
                            if(modifyDBisOk) {
                                textViewModify.setText("OK");
                                textViewModify.setTextColor(Color.GREEN);
                                Toast.makeText(TestActivity.this, "OK. There are " + numZones + " trips for each zone.",
                                        Toast.LENGTH_SHORT).show();
                            } else{
                                textViewModify.setText("KO");
                                textViewModify.setTextColor(Color.RED);
                                Toast.makeText(TestActivity.this, "KO. There are NOT " + numZones + " trips for each zone.",
                                        Toast.LENGTH_LONG).show();
                            }
                            modifyDBisOk=true;
                        }
                        else{
                            System.out.println("INFO" + " There are no vouchers");
                            Toast.makeText(TestActivity.this, "INFO" + " There are no vouchers",
                                    Toast.LENGTH_LONG).show();
                            modifyDBisOk = false;
                        }
                    } else {
                        System.out.println("INFO" + " No such document");
                        Toast.makeText(TestActivity.this, "INFO" + " No such document",
                                Toast.LENGTH_LONG).show();
                        modifyDBisOk = false;
                    }
                } else {
                    System.out.println("ERROR" + " get failed with " + task.getException());
                    Toast.makeText(TestActivity.this, "ERROR" + " get failed with " + task.getException(),
                            Toast.LENGTH_LONG).show();
                    modifyDBisOk = false;
                }
            }
        });
    }

    /**
     * Log out (in firebase)
     */
    public void logOut(){
        mAuth.getInstance().signOut();
    }
}