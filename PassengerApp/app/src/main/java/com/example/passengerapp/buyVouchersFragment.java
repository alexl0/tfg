package com.example.passengerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link buyVouchersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class buyVouchersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Paypal properties
    private static final String YOUR_CLIENT_ID = "AQ05jcZl3mJsIHeVKqorhWg1ERaOJYdWg59YK7AvIHLzYSsamOVWcSem_IWVlUFJ-NszPHLj7zmeeOXy";
    PayPalButton payPalButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public buyVouchersFragment() {
        // Required empty public constructor
    }

    /**
     * Precio por numero de zonas por cada 10 viajes
     */
    public Map<Integer, Double> pricesZones = new HashMap<Integer, Double>();

    //Zonas seleccionadas
    int numZonesSelected=1;
    int numTripsSelected=10;
    double totalPriceOfVouchers = 9.0;

    /**
     * Main attributes
     */
    FirebaseFirestore db;
    Spinner spinnerZones;
    Spinner spinnerTrips;
    ConstraintLayout parent;
    TextView textViewTotalPrice;
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

        //Setup prices
        pricesZones.put(1, 9.0);
        pricesZones.put(2, 14.3);
        pricesZones.put(3, 21.0);
        pricesZones.put(4, 28.0);
        pricesZones.put(5, 40.6);
        pricesZones.put(6, 54.0);
        pricesZones.put(7, 67.3);
        pricesZones.put(8, 86.8);

        //Set up database
        db = FirebaseFirestore.getInstance();
        spinnerZones = view.findViewById(R.id.spinnerNumZonesBuy);
        spinnerTrips = view.findViewById(R.id.spinnerNumTripsBuy);
        parent = view.findViewById(R.id.parentConstraintLayout);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);

        textViewTotalPrice.setText(getString(R.string.totalPrice) + " " + this.pricesZones.get(1)+" €");

        //Paypal button
        payPalButton=view.findViewById(R.id.payPalButton);
        CheckoutConfig config = new CheckoutConfig(
                getActivity().getApplication(),
                YOUR_CLIENT_ID,
                Environment.SANDBOX,
                "com.example.passengerapp://paypalpay",
                CurrencyCode.EUR,
                UserAction.PAY_NOW
        );
        PayPalCheckout.setConfig(config);
        payPalButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.EUR)
                                                        .value("9.00")
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                (List)purchaseUnits,null
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                                //Payment successful, so we upgrade the voyages on the database
                                int numZonesSelected = Integer.parseInt(spinnerZones.getSelectedItem().toString());
                                int numTripsSelected = Integer.parseInt(spinnerTrips.getSelectedItem().toString());

                                FirebaseUser currentUser = (FirebaseUser) SingletonClass.get().hashObjects.get("user");
                                String currentUserEmail = currentUser.getEmail();

                                //Get current user remaining trips with that zone
                                DocumentReference docRef = db.collection("users").document(currentUserEmail);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();


                                            // Create a new user with the updated data
                                            Map<String, Object> userHash = new HashMap<>();
                                            for(int i=1; i<=7; i++){
                                                //Si el usuario ya tiene viajes para la zona i
                                                if(document.exists() && document.getData().get("tripsZone"+i)!=null){
                                                    //Y ademas es la que ha seleccionado
                                                    if(i==numZonesSelected){
                                                        //Se le suman
                                                        int currentTripsBefore = ((Long) document.getData().get("tripsZone"+numZonesSelected)).intValue();
                                                        userHash.put("tripsZone"+numZonesSelected, numTripsSelected + currentTripsBefore);
                                                    }
                                                    //Sino
                                                    else{
                                                        //Se le ponen los viajes que ya tenga
                                                        int currentTripsBefore = ((Long) document.getData().get("tripsZone"+i)).intValue();
                                                        userHash.put("tripsZone"+i, currentTripsBefore);
                                                    }
                                                }
                                                //Sino
                                                else{
                                                    if(i==numZonesSelected){
                                                        userHash.put("tripsZone"+numZonesSelected, numTripsSelected);
                                                    }
                                                    else
                                                        //Se le ponen 0 viajes
                                                        userHash.put("tripsZone"+i, 0);
                                                }
                                            }

                                            //Add/update the user to/from the database
                                            db.collection("users").document(currentUserEmail).set(userHash);

                                            showSnackBar();
                                        } else {
                                            Log.d("ERROR", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
        );
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
        spinnerZones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public Map<Integer, Double> pricesZones = new HashMap<Integer, Double>();

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Setup prices
                pricesZones.put(1, 9.0);
                pricesZones.put(2, 14.3);
                pricesZones.put(3, 21.0);
                pricesZones.put(4, 28.0);
                pricesZones.put(5, 40.6);
                pricesZones.put(6, 54.0);
                pricesZones.put(7, 67.3);
                pricesZones.put(8, 86.8);
                numZonesSelected = Integer.parseInt(parentView.getItemAtPosition(position).toString());
                totalPriceOfVouchers = this.pricesZones.get(numZonesSelected) * numTripsSelected/10;
                textViewTotalPrice.setText(getString(R.string.totalPrice) + " " + Double.toString(totalPriceOfVouchers) + " €");

                /**
                 * Update paypal button
                 */
                payPalButton.setup(
                        new CreateOrder() {
                            @Override
                            public void create(@NotNull CreateOrderActions createOrderActions) {
                                ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                                purchaseUnits.add(
                                        new PurchaseUnit.Builder()
                                                .amount(
                                                        new Amount.Builder()
                                                                .currencyCode(CurrencyCode.EUR)
                                                                .value(Double.toString(totalPriceOfVouchers))
                                                                .build()
                                                )
                                                .build()
                                );
                                Order order = new Order(
                                        OrderIntent.CAPTURE,
                                        new AppContext.Builder()
                                                .userAction(UserAction.PAY_NOW)
                                                .build(),
                                        (List)purchaseUnits,null
                                );
                                createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                            }
                        },
                        new OnApprove() {
                            @Override
                            public void onApprove(@NotNull Approval approval) {
                                approval.getOrderActions().capture(new OnCaptureComplete() {
                                    @Override
                                    public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                        Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                                        //Payment successful, so we upgrade the voyages on the database
                                        int numZonesSelected = Integer.parseInt(spinnerZones.getSelectedItem().toString());
                                        int numTripsSelected = Integer.parseInt(spinnerTrips.getSelectedItem().toString());

                                        FirebaseUser currentUser = (FirebaseUser) SingletonClass.get().hashObjects.get("user");
                                        String currentUserEmail = currentUser.getEmail();

                                        //Get current user remaining trips with that zone
                                        DocumentReference docRef = db.collection("users").document(currentUserEmail);
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();


                                                    // Create a new user with the updated data
                                                    Map<String, Object> userHash = new HashMap<>();
                                                    for(int i=1; i<=7; i++){
                                                        //Si el usuario ya tiene viajes para la zona i
                                                        if(document.exists() && document.getData().get("tripsZone"+i)!=null){
                                                            //Y ademas es la que ha seleccionado
                                                            if(i==numZonesSelected){
                                                                //Se le suman
                                                                int currentTripsBefore = ((Long) document.getData().get("tripsZone"+numZonesSelected)).intValue();
                                                                userHash.put("tripsZone"+numZonesSelected, numTripsSelected + currentTripsBefore);
                                                            }
                                                            //Sino
                                                            else{
                                                                //Se le ponen los viajes que ya tenga
                                                                int currentTripsBefore = ((Long) document.getData().get("tripsZone"+i)).intValue();
                                                                userHash.put("tripsZone"+i, currentTripsBefore);
                                                            }
                                                        }
                                                        //Sino
                                                        else{
                                                            if(i==numZonesSelected){
                                                                userHash.put("tripsZone"+numZonesSelected, numTripsSelected);
                                                            }
                                                            else
                                                                //Se le ponen 0 viajes
                                                                userHash.put("tripsZone"+i, 0);
                                                        }
                                                    }

                                                    //Add/update the user to/from the database
                                                    db.collection("users").document(currentUserEmail).set(userHash);

                                                    showSnackBar();
                                                } else {
                                                    Log.d("ERROR", "get failed with ", task.getException());
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing happens
            }

        });
        spinnerTrips.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public Map<Integer, Double> pricesZones = new HashMap<Integer, Double>();

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Setup prices
                pricesZones.put(1, 9.0);
                pricesZones.put(2, 14.3);
                pricesZones.put(3, 21.0);
                pricesZones.put(4, 28.0);
                pricesZones.put(5, 40.6);
                pricesZones.put(6, 54.0);
                pricesZones.put(7, 67.3);
                pricesZones.put(8, 86.8);
                numTripsSelected = Integer.parseInt(parentView.getItemAtPosition(position).toString());
                totalPriceOfVouchers = this.pricesZones.get(numZonesSelected) * numTripsSelected/10;
                textViewTotalPrice.setText(getString(R.string.totalPrice) + " " + Double.toString(totalPriceOfVouchers) + " €");

                /**
                 * Update paypal button
                 */
                payPalButton.setup(
                        new CreateOrder() {
                            @Override
                            public void create(@NotNull CreateOrderActions createOrderActions) {
                                ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                                purchaseUnits.add(
                                        new PurchaseUnit.Builder()
                                                .amount(
                                                        new Amount.Builder()
                                                                .currencyCode(CurrencyCode.EUR)
                                                                .value(Double.toString(totalPriceOfVouchers))
                                                                .build()
                                                )
                                                .build()
                                );
                                Order order = new Order(
                                        OrderIntent.CAPTURE,
                                        new AppContext.Builder()
                                                .userAction(UserAction.PAY_NOW)
                                                .build(),
                                        (List)purchaseUnits,null
                                );
                                createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                            }
                        },
                        new OnApprove() {
                            @Override
                            public void onApprove(@NotNull Approval approval) {
                                approval.getOrderActions().capture(new OnCaptureComplete() {
                                    @Override
                                    public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                        Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                                        //Payment successful, so we upgrade the voyages on the database
                                        int numZonesSelected = Integer.parseInt(spinnerZones.getSelectedItem().toString());
                                        int numTripsSelected = Integer.parseInt(spinnerTrips.getSelectedItem().toString());

                                        FirebaseUser currentUser = (FirebaseUser) SingletonClass.get().hashObjects.get("user");
                                        String currentUserEmail = currentUser.getEmail();

                                        //Get current user remaining trips with that zone
                                        DocumentReference docRef = db.collection("users").document(currentUserEmail);
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();


                                                    // Create a new user with the updated data
                                                    Map<String, Object> userHash = new HashMap<>();
                                                    for(int i=1; i<=7; i++){
                                                        //Si el usuario ya tiene viajes para la zona i
                                                        if(document.exists() && document.getData().get("tripsZone"+i)!=null){
                                                            //Y ademas es la que ha seleccionado
                                                            if(i==numZonesSelected){
                                                                //Se le suman
                                                                int currentTripsBefore = ((Long) document.getData().get("tripsZone"+numZonesSelected)).intValue();
                                                                userHash.put("tripsZone"+numZonesSelected, numTripsSelected + currentTripsBefore);
                                                            }
                                                            //Sino
                                                            else{
                                                                //Se le ponen los viajes que ya tenga
                                                                int currentTripsBefore = ((Long) document.getData().get("tripsZone"+i)).intValue();
                                                                userHash.put("tripsZone"+i, currentTripsBefore);
                                                            }
                                                        }
                                                        //Sino
                                                        else{
                                                            if(i==numZonesSelected){
                                                                userHash.put("tripsZone"+numZonesSelected, numTripsSelected);
                                                            }
                                                            else
                                                                //Se le ponen 0 viajes
                                                                userHash.put("tripsZone"+i, 0);
                                                        }
                                                    }

                                                    //Add/update the user to/from the database
                                                    db.collection("users").document(currentUserEmail).set(userHash);

                                                    showSnackBar();
                                                } else {
                                                    Log.d("ERROR", "get failed with ", task.getException());
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing happens
            }

        });
    }



    //Mostrar Snackbar (como un cuadro de dialogo abajo tipo system.out.println o system.out)
    private void showSnackBar() {
        Snackbar.make(parent, getText(R.string.tripsBought), Snackbar.LENGTH_INDEFINITE)
                .setAction(getText(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(), getText(R.string.ok), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

}