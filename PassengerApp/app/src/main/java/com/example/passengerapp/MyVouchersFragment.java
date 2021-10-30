package com.example.passengerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyVouchersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyVouchersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyVouchersFragment() {
        // Required empty public constructor
    }

    /**
     * Main attributes
     */
    FirebaseFirestore db;
    TextView textView1Zone;
    TextView textView2Zone;
    TextView textView3Zone;
    TextView textView4Zone;
    TextView textView5Zone;
    TextView textView6Zone;
    TextView textView7Zone;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyVouchersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyVouchersFragment newInstance(String param1, String param2) {
        MyVouchersFragment fragment = new MyVouchersFragment();
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
        requireActivity().setTitle(getString(R.string.my_vouchers_prompt));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_vouchers, container, false);

        //Set up database
        db = FirebaseFirestore.getInstance();

        //Set up textViews
        textView1Zone = view.findViewById(R.id.textView1Zone);
        textView2Zone = view.findViewById(R.id.textView2Zone);
        textView3Zone = view.findViewById(R.id.textView3Zone);
        textView4Zone = view.findViewById(R.id.textView4Zone);
        textView5Zone = view.findViewById(R.id.textView5Zone);
        textView6Zone = view.findViewById(R.id.textView6Zone);
        textView7Zone = view.findViewById(R.id.textView7Zone);

        /**
         * Load data from database and store it in the text views
         */

        FirebaseUser currentUser = (FirebaseUser) SingletonClass.get().hashObjects.get("user");
        String currentUserEmail = currentUser.getEmail();

        //Get current user remaining trips with that zone
        DocumentReference docRef = db.collection("users").document(currentUserEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(document.getData().get("tripsZone1")!=null)
                            for(int i=1; i<=7; i++){
                                switch(i){
                                    case 1:
                                        textView1Zone.setText(document.getData().get("tripsZone"+i).toString());
                                    case 2:
                                        textView2Zone.setText(document.getData().get("tripsZone"+i).toString());
                                    case 3:
                                        textView3Zone.setText(document.getData().get("tripsZone"+i).toString());
                                    case 4:
                                        textView4Zone.setText(document.getData().get("tripsZone"+i).toString());
                                    case 5:
                                        textView5Zone.setText(document.getData().get("tripsZone"+i).toString());
                                    case 6:
                                        textView6Zone.setText(document.getData().get("tripsZone"+i).toString());
                                    case 7:
                                        textView7Zone.setText(document.getData().get("tripsZone"+i).toString());
                                }
                            }
                        else
                            Log.d("INFO", "There are no vouchers");
                    } else {
                        Log.d("INFO", "No such document");
                    }
                } else {
                    Log.d("ERROR", "get failed with ", task.getException());
                }
            }
        });

        return view;
    }
}