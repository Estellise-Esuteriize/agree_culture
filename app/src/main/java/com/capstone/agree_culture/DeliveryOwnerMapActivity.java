package com.capstone.agree_culture;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.capstone.agree_culture.CustomClass.DirectionsJSONParser;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class DeliveryOwnerMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Marker destinationMarker;
    private Marker deliveryMarker;

    private BitmapDescriptor destinationColor;
    private BitmapDescriptor deliveryColor;

    private LatLng destinationLatLng;
    private LatLng deliveryLatLng;

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



        mDatabase.collection(GlobalString.DELIVERY).whereEqualTo(delivery.stringOwnerProductUuid(), ownerUidRef).whereEqualTo(delivery.stringBuyerProductUuid(), buyerUidRef).limit(QUERY_LIMIT_ONE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){

                    if(!task.getResult().isEmpty()){

                        for(DocumentSnapshot snapshot : task.getResult()){

                            delivery = (Delivery) snapshot.toObject(Delivery.class);

                            delivery.setDocumentId(snapshot.getId());

                        }

                        if(TextUtils.isEmpty(delivery.getDocumentId())){
                            return;
                        }

                        destinationLatLng = new LatLng(delivery.getDestinationLat(), delivery.getDestinationLong());
                        deliveryLatLng = new LatLng(delivery.getDeliveryLat(), delivery.getDeliveryLong());


                        destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination").icon(destinationColor));
                        deliveryMarker = mMap.addMarker(new MarkerOptions().position(deliveryLatLng).title("Delivery").icon(deliveryColor));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(deliveryLatLng, 8.2f));

                        String url = getDirectionsUrl(deliveryLatLng, destinationLatLng);

                        DownloadTask downloadTask = new DownloadTask();

                        downloadTask.execute(url);

                        mDatabase.collection(GlobalString.DELIVERY).document(delivery.getDocumentId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                if(documentSnapshot != null && documentSnapshot.exists()){

                                    Delivery newDeliveryPosition = (Delivery) documentSnapshot.toObject(Delivery.class);

                                    deliveryLatLng = new LatLng(newDeliveryPosition.getDeliveryLat(), newDeliveryPosition.getDeliveryLong());

                                    deliveryMarker.setPosition(deliveryLatLng);

                                }
                            }
                        });


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


    /**
     * Class and Functions
     *  - for Requesting a route in main rode google map
     */

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

    /**
     * End
     * Request of Route
     */




}
