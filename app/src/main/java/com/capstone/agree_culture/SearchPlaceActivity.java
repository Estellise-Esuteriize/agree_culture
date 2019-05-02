package com.capstone.agree_culture;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.PlacesSearch;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.Help;

import java.util.Arrays;

public class SearchPlaceActivity extends AppCompatActivity {


    private FirebaseFirestore mDatabase;

    private View progressBar;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        context = this;


        mDatabase = FirebaseFirestore.getInstance();


        progressBar = findViewById(R.id.progress_bar);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        if(!Places.isInitialized()){
            Places.initialize(this, Helper.API_GOOGLE_GEO);
        }

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelection());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class PlaceSelection implements PlaceSelectionListener {
        @Override
        public void onPlaceSelected(@NonNull Place place) {

            Log.d("PlaceSearchName", place.getName() + "");
            Log.d("PlaceSearchAddress", place.getAddress() + "");

            final Place fPlace = place;

            try{
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;


                PlacesSearch places = new PlacesSearch(latitude, longitude);


                Helper.ProgressDisplayer(context, progressBar);

                mDatabase.collection(GlobalString.PLACES).document(place.getName()).set(places).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Helper.ProgressRemover(context, progressBar);

                        if(task.isSuccessful()){

                            Intent intent = new Intent();

                            intent.putExtra("address", fPlace.getName());

                            setResult(RESULT_OK, intent);

                            finish();

                        }
                        else{
                            try{
                                throw task.getException();
                            }
                            catch (Exception ex){
                                Helper.ToastDisplayer(context, ex.getMessage(), Toast.LENGTH_LONG);
                            }
                        }


                    }
                });



            }
            catch (Exception ex){

                Helper.ProgressRemover(context, progressBar);

                ex.printStackTrace();
                Helper.ToastDisplayer(context, ex.getMessage(), Toast.LENGTH_SHORT);
            }


        }

        @Override
        public void onError(@NonNull Status status) {

        }
    }
}
