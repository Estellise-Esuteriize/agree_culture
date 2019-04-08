package com.capstone.agree_culture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class DeliveryDestinationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Marker deliveryCar;

    private final int REQUEST_LOCATION_PERMISSION = 101;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private LatLng oldPosition;

    private ImageButton targetPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_destination_map);

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        targetPosition = (ImageButton) findViewById(R.id.destination_map_target_position);


        if(!Places.isInitialized()){
            Places.initialize(this, "AIzaSyBCXGVFnN4GANxHUo4E90Q3gOhcZgE8reo");
        }

        PlacesClient placesClient = Places.createClient(this);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelection());
        targetPosition.setOnClickListener(new TargetPosition());


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        Log.d("Location Updates", location.getLatitude() + location.getLongitude() + "");

                        if(mMap != null){

                            LatLng lang = new LatLng(location.getLatitude(), location.getLongitude());

                            oldPosition = lang;

                            if(deliveryCar != null){

                                Log.d("StatusLocationUpdate", "Updating Location");

                                deliveryCar.setPosition(lang);

                                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lang, 16.2f));

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(lang));

                            }

                        }
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        else{
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

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

        Log.d("StatusMapReady", "Map is ready");

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        deliveryCar = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.2f));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }

                break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    class PlaceSelection implements PlaceSelectionListener{
        @Override
        public void onPlaceSelected(@NonNull Place place) {

        }

        @Override
        public void onError(@NonNull Status status) {

        }
    }

    class TargetPosition implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(deliveryCar != null && oldPosition != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(oldPosition, 16.2f));
            }

        }
    }

}
