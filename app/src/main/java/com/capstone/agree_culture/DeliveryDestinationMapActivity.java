package com.capstone.agree_culture;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.capstone.agree_culture.CustomClass.DirectionsJSONParser;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Delivery;
import com.capstone.agree_culture.Model.User;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DeliveryDestinationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Geocoder geocoder;

    private Marker deliveryCar;
    private Marker destinationMarker;

    private FirebaseFirestore mDatabase;


    private final int REQUEST_LOCATION_PERMISSION = 101;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private LatLng oldPosition;
    private LatLng destination;

    private ImageButton targetPosition;

    private List<Address> addresses = new ArrayList<>();

    private User currentUser;
    private User buyer;

    private Delivery delivery;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_destination_map);

        context = this;

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        mDatabase = FirebaseFirestore.getInstance();

        targetPosition = (ImageButton) findViewById(R.id.destination_map_target_position);


        buyer = (User) getIntent().getSerializableExtra("buyer");

        currentUser = Helper.currentUser;

        delivery = new Delivery();

        geocoder = new Geocoder(this, Locale.getDefault());


        if(!Places.isInitialized()){
            Places.initialize(this, "AIzaSyBCXGVFnN4GANxHUo4E90Q3gOhcZgE8reo");
        }

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

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

                            try{

                                WriteBatch batch = mDatabase.batch();
                                DocumentReference ref = mDatabase.collection(GlobalString.DELIVERY).document(delivery.getDocumentId());

                                batch.update(ref, delivery.stringDeliveryLat(), lang.latitude);
                                batch.update(ref, delivery.stringDeliveryLong(), lang.longitude);

                                batch.commit();

                            }
                            catch (Exception ex){
                                ex.printStackTrace();
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


        mapFragment.getMapAsync(this);

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

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        BitmapDescriptor destMarkerColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        final BitmapDescriptor delvMarkerColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        deliveryCar = mMap.addMarker(new MarkerOptions().position(sydney).title("Delivery").icon(delvMarkerColor));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 8.2f));


        try {
            addresses = geocoder.getFromLocationName(buyer.getAddress(), 1);

            if(addresses.size() > 0){

                Address address = addresses.get(0);

                destination = new LatLng(address.getLatitude(), address.getLongitude());
            }
            else{
                Toast.makeText(this, R.string.no_initial_destination, Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {


            Toast.makeText(this, R.string.no_initial_destination, Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }


        if(destination != null){

            destinationMarker = mMap.addMarker(new MarkerOptions().position(destination).title("Destination").icon(destMarkerColor));

        }


        mDatabase.collection(GlobalString.DELIVERY).whereEqualTo(delivery.stringOwnerProductUuid(), currentUser.getDocumentId()).whereEqualTo(delivery.stringBuyerProductUuid(), buyer.getDocumentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){

                    int item = 0;
                    int limit = 0;

                    if(task.getResult().isEmpty()){
                        setDeliveryData();
                        return;
                    }

                    for(DocumentSnapshot snapshot : task.getResult()){

                        if(item > limit){
                            break;
                        }

                        delivery = (Delivery) snapshot.toObject(Delivery.class);
                        delivery.setDocumentId(snapshot.getId());

                        item++;
                    }
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (Exception ex){
                        Helper.ToastDisplayer(context, ex.getMessage(), Toast.LENGTH_SHORT);
                    }
                }

            }
        });


    }

    private void setDeliveryData(){

        final Delivery delivery = new Delivery(currentUser.getDocumentId(), buyer.getDocumentId());

        mDatabase.collection(GlobalString.DELIVERY).add(delivery).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    delivery.setDocumentId(task.getResult().getId());
                }
            }
        });

    }

    private void changeDestinationMaker(LatLng latLng){

        destination = latLng;

        BitmapDescriptor destMarkerColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);

        if(destinationMarker == null){
            destinationMarker = mMap.addMarker(new MarkerOptions().position(destination).icon(destMarkerColor));
        }

        destinationMarker.setPosition(destination);

        String url = getDirectionsUrl(oldPosition, destination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);


        /**
         * Set Destination in Database
         */

        double destLat = destination.latitude;
        double destLong = destination.longitude;

        try{
            WriteBatch batch = mDatabase.batch();
            DocumentReference ref = mDatabase.collection(GlobalString.DELIVERY).document(delivery.getDocumentId());

            batch.update(ref, delivery.stringDestinationLat(), destLat);
            batch.update(ref, delivery.stringDestinationLong(), destLong);

            batch.commit();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

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
            Log.d("StatusGeocoderPlace", place.getAddress() + place.getName() + place.getLatLng() + "");
            changeDestinationMaker(place.getLatLng());

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


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null){
                mMap.addPolyline(lineOptions);
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.no_directions, Toast.LENGTH_LONG).show();
            }
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String key = "key=" + Helper.API_GOOGLE_GEO;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + key + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
