package com.example.passengerapp.appTesting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.passengerapp.R;
import com.example.passengerapp.SingletonClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment {

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

    //Layouts
    private LinearLayout layoutButtonsTesting;
    private TableLayout tableLayoutTesting;
    public TestFragment() {
        // Required empty public constructor
    }

    public static TestFragment newInstance() {
        TestFragment fragment = new TestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        testLoginButton = view.findViewById(R.id.testLoginButton);
        testBTButton = view.findViewById(R.id.testBTButton);
        testDBButton1 = view.findViewById(R.id.testDBButton1);
        testDBButton2 = view.findViewById(R.id.testDBButton2);
        testDBButton3 = view.findViewById(R.id.testDBButton3);
        testDBButton4 = view.findViewById(R.id.testDBButton4);
        logoutButton = view.findViewById(R.id.logoutButton);
        textViewLogin = view.findViewById(R.id.textViewLogin);
        textViewDetect = view.findViewById(R.id.textViewDetect);
        textViewModify = view.findViewById(R.id.textViewModify);
        //Layouts
        layoutButtonsTesting = view.findViewById(R.id.layoutButtonsTesting);
        tableLayoutTesting = view.findViewById(R.id.tableLayoutTesting);

        //TestingBT
        boolean testingBT = SingletonClass.get().getTestingBT();
        boolean testingBTSuccess = SingletonClass.get().getTestingBTSuccess();
        if(!testingBT){
            textViewDetect.setText("-");
            textViewDetect.setTextColor(Color.WHITE);
        } else if(testingBTSuccess){
            textViewDetect.setText("OK");
            textViewDetect.setTextColor(Color.GREEN);
        } else{
            textViewDetect.setText("KO");
            textViewDetect.setTextColor(Color.RED);
        }

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
        testBTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingletonClass.get().setTestingBT(true);
                SingletonClass.get().setTestingBTSuccess(false);
                SingletonClass.get().setWeHaveTestedBT(false);
                //Navigate to device_scan_fragment_test
                Navigation.findNavController(view).navigate(R.id.action_testFragment_to_deviceScanFragTest);
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

        return view;
    }

    /**
     * Sign in with email and password for testing purposes
     * @param email
     * @param password
     */
    private void testSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
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
                            Toast.makeText(getActivity(), "OK. Logged in successfully.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed with testing credentials.",
                                    Toast.LENGTH_LONG).show();
                            textViewLogin.setText("KO");
                            textViewLogin.setTextColor(Color.RED);
                            Toast.makeText(getActivity(), "KO. Error logging in.",
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
                    Toast.makeText(getActivity(), "OK. Every zone has now " + numberOfVoyages + " trips.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("ERROR" + " get failed with " + task.getException());
                    Toast.makeText(getActivity(), "ERROR" + " get failed with " + task.getException(),
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
                                Toast.makeText(getActivity(), "OK. There are " + numZones + " trips for each zone.",
                                        Toast.LENGTH_SHORT).show();
                            } else{
                                textViewModify.setText("KO");
                                textViewModify.setTextColor(Color.RED);
                                Toast.makeText(getActivity(), "KO. There are NOT " + numZones + " trips for each zone.",
                                        Toast.LENGTH_LONG).show();
                            }
                            modifyDBisOk=true;
                        }
                        else{
                            System.out.println("INFO" + " There are no vouchers");
                            Toast.makeText(getActivity(), "INFO" + " There are no vouchers",
                                    Toast.LENGTH_LONG).show();
                            modifyDBisOk = false;
                        }
                    } else {
                        System.out.println("INFO" + " No such document");
                        Toast.makeText(getActivity(), "INFO" + " No such document",
                                Toast.LENGTH_LONG).show();
                        modifyDBisOk = false;
                    }
                } else {
                    System.out.println("ERROR" + " get failed with " + task.getException());
                    Toast.makeText(getActivity(), "ERROR" + " get failed with " + task.getException(),
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
        Toast.makeText(getActivity(), "OK. Logged out successfully.",
                Toast.LENGTH_SHORT).show();
    }

}