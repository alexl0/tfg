package com.example.passengerapp.test;


import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class PassengerAppTests {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String testEmail = "pruebatests@mail.com";
    private final String testPassword = "pruebatests1";

    @Before
    public void initialize(){
        db = FirebaseFirestore.getInstance();
        signIn(testEmail, testPassword);
        DocumentReference docRef = db.collection("users").document(testEmail);

        //Put 0 voyages to every zone (remove all voyages)
        addVoyages(docRef, 0);
    }

    @Test
    public void testDatabase(){
        DocumentReference docRef = db.collection("users").document(testEmail);

        //Check user has 0 voyages for every zone
        checkVoyages(docRef, 0);

        //Put 50 voyages to every zone
        addVoyages(docRef, 50);

        //Check user has 50 voyages for every zone
        checkVoyages(docRef, 50);
    }

    @After
    public void logOut(){
        mAuth.getInstance().signOut();
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
                } else {
                    System.out.println("ERROR" + " get failed with " + task.getException());
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
                        if(document.getData().get("tripsZone1")!=null)
                            for(int i=1; i<=7; i++){
                                assertEquals(numZones, document.getData().get("tripsZone"+i).toString());
                            }
                        else
                            System.out.println("INFO" + " There are no vouchers");
                    } else {
                        System.out.println("INFO" + " No such document");
                    }
                } else {
                    System.out.println("ERROR" + " get failed with " + task.getException());
                }
            }
        });
    }

    /**
     * Sign in with email and password
     * @param email
     * @param password
     */
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Sesión iniciada correctamente");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            System.out.println("Error al iniciar sesión");
                        }
                    }
                });
    }

}