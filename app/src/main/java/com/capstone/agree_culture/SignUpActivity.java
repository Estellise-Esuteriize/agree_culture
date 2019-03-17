package com.capstone.agree_culture;

import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.capstone.agree_culture.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {


    private Spinner sign_up_user_role;
    private EditText sign_up_full_name;
    private EditText sign_up_email;
    private EditText sign_up_password;
    private EditText sign_up_password_confirmation;
    private EditText sign_up_address;
    private EditText sign_up_city;
    private EditText sign_up_phone_number;

    private Button sign_up_confirm;

    private View progress_bar;

    private static final int FULL_NAME_LIMIT = 25;


    private FirebaseAuth mAuth;
    //private DatabaseReference mDatabase;
    private FirebaseFirestore mDatabase;

    private static final String USERS = "users";

    private Context cont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mAuth = FirebaseAuth.getInstance();

        if(null != mAuth.getCurrentUser()){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            Toast.makeText(this, getResources().getString(R.string.user_exists), Toast.LENGTH_SHORT).show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, Toast.LENGTH_SHORT + 100);
        }

        /**
         * Firebase Realtime Database
         * mDatabase = FirebaseDatabase.getInstance().getReference();
         */
        /**
         * Firebase Firestore
         */
        mDatabase = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDatabase.setFirestoreSettings(settings);

        cont = this;

        /**
         * Toolbar
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        sign_up_user_role = (Spinner) findViewById(R.id.sign_up_user_type);
        sign_up_full_name = (EditText) findViewById(R.id.sign_up_full_name);
        sign_up_email = (EditText) findViewById(R.id.sign_up_email);
        sign_up_password = (EditText) findViewById(R.id.sign_up_password);
        sign_up_password_confirmation = (EditText)findViewById(R.id.sign_up_confirm_password);
        sign_up_address = (EditText) findViewById(R.id.sign_up_address);
        sign_up_city = (EditText) findViewById(R.id.sign_up_city);
        sign_up_phone_number = (EditText) findViewById(R.id.sign_up_phone_number);
        sign_up_confirm = (Button) findViewById(R.id.sign_up_button);


        sign_up_confirm.setOnClickListener(new SignUpConfirm());

        progress_bar = findViewById(R.id.progress_bar);

    }


    private void signUp(){

        final String user_role = sign_up_user_role.getSelectedItem().toString();
        final String full_name = sign_up_full_name.getText().toString();
        final String email = sign_up_email.getText().toString();
        final String password = sign_up_password.getText().toString();
        final String password_confirm = sign_up_password_confirmation.getText().toString();
        final String address = sign_up_address.getText().toString();
        final String city = sign_up_city.getText().toString();
        final String phone_number = "+63" + sign_up_phone_number.getText().toString();

        Boolean has_error = false;

        /**
         * Full name
         */
        if(TextUtils.isEmpty(full_name)){
            sign_up_full_name.setError(getResources().getString(R.string.sign_up_full_name_no_text));
            has_error = true;
        }
        else if(full_name.length() > FULL_NAME_LIMIT){
            sign_up_full_name.setError(getResources().getString(R.string.sign_up_full_name_limit));
            has_error = true;
        }

        /**
         * Email
         */
        if(TextUtils.isEmpty(email)){
            sign_up_email.setError(getResources().getString(R.string.sign_up_email_no_text));
            has_error = true;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            sign_up_email.setError(getResources().getString(R.string.sign_up_email_not_valid));
            has_error = true;
        }

        /**
         * Password
         */
        if(TextUtils.isEmpty(password)){
            sign_up_password.setError(getResources().getString(R.string.sign_up_password_no_text));
            has_error = true;
        }
        else if(!password.equals(password_confirm)){
            sign_up_password_confirmation.setError(getResources().getString(R.string.sign_up_password_confirmation_required));
            has_error = true;
        }

        /**
         * Address
         */
        if(TextUtils.isEmpty(address)){
            sign_up_address.setError(getResources().getString(R.string.sign_up_address_required));
            has_error = true;
        }
        if(TextUtils.isEmpty(city)){
            sign_up_city.setError(getResources().getString(R.string.sign_up_city_required));
            has_error = true;
        }

        /**
         * Phone number
         */

        if(TextUtils.isEmpty(phone_number)){
            sign_up_phone_number.setError(getResources().getString(R.string.sign_up_phone_number_required));
            has_error = true;
        }



        if(!has_error){

            progress_bar.setVisibility(View.VISIBLE);


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        /**
                        User user = new User(full_name, user_role, address, city, phone_number);

                        mDatabase.child(USERS).child(currentUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mAuth.signOut();
                                    finish();
                                }
                                else{
                                    try{
                                        throw task.getException();
                                    }
                                    catch (Exception ex){
                                        Log.e("Create User Error", ex.getMessage());
                                    }
                                }

                            }
                        });
                         */

                        Map<String, Object> user = new HashMap<>();
                        user.put("full_name", full_name);
                        user.put("role", user_role);
                        user.put("address", address);
                        user.put("city", city);
                        user.put("phone_number", phone_number);
                        user.put("created_at", FieldValue.serverTimestamp());

                        mDatabase.collection(USERS).document(currentUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mAuth.signOut();
                                    finish();
                                }
                                else{
                                    try{
                                        throw task.getException();
                                    }
                                    catch (Exception ex){
                                        Log.e("Create User Error", ex.getMessage());
                                    }
                                }

                            }
                        });


                    }
                    else{
                        try{

                            progress_bar.setVisibility(View.GONE);

                            throw task.getException();
                        }
                        catch (FirebaseAuthWeakPasswordException ex){
                            Log.e("Weak Password", ex.getMessage());
                            sign_up_password.setError(ex.getMessage());
                        }
                        catch (FirebaseAuthUserCollisionException ex){
                            Log.e("User Exists", ex.getMessage());
                            sign_up_email.setError(ex.getMessage());
                        }
                        catch (Exception ex){
                            Log.e("Sign Up Error", ex.getMessage());
                            Toast.makeText(cont, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            });


        }



    }


    class SignUpConfirm implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            signUp();
        }
    }

}
