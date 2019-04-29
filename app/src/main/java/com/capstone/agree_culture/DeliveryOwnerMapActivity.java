package com.capstone.agree_culture;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Delivery;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DeliveryOwnerMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Marker destinationMarker;
    private Marker deliveryMarker;

    private BitmapDescriptor destinationColor;
    private BitmapDescriptor deliveryColor;

    private String ownerUidRef;
    private String buyerUidRef;

    private FirebaseFirestore mDatabase;

    private Delivery delivery;

    private final int QUERY_LIMIT_ONE = 1;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_ownery_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        context = this;

        mDatabase = FirebaseFirestore.getInstance();


        delivery = new Delivery();

        ownerUidRef = getIntent().getStringExtra("ownerUidRef");
        buyerUidRef = getIntent().getStringExtra("buyerUidRef");


        destinationColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        deliveryColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);


        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        mDatabase.collection(GlobalString.DELIVERY).whereEqualTo(delivery.getOwnerProductUuid(), ownerUidRef).whereEqualTo(delivery.getBuyerProductUuid(), buyerUidRef).limit(QUERY_LIMIT_ONE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){

                    if(!task.getResult().isEmpty()){
                        
                    }
                    else{
                        Helper.ToastDisplayer(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                    }
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                        Helper.ToastDisplayer(context, ex.getMessage(), Toast.LENGTH_LONG);
                    }
                }


            }
        });




    }
}
