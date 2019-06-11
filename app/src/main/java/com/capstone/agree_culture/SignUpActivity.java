package com.capstone.agree_culture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.rpc.Help;

import java.io.Serializable;
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
    private Button searchPlace;

    private View progress_bar;

    private static final int FULL_NAME_LIMIT = 25;
    private static final int SEARCH_PLACE_ID = 321;

    private FirebaseAuth mAuth;
    //private DatabaseReference mDatabase;
    private FirebaseFirestore mDatabase;

    private static final String USERS = "users";

    private Context cont;

    private User updateUser;
    private FirebaseUser currentUser;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        updateUser = (User) getIntent().getSerializableExtra("user");

        if(null != mAuth.getCurrentUser() && updateUser == null){
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
        else if(mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
        }

        /**
         * Firebase Realtime Database
         * mDatabase = FirebaseDatabase.getInstance().getReference();
         */
        /**
         * Firebase Firestore
         */
        mDatabase = FirebaseFirestore.getInstance();

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
        searchPlace = (Button) findViewById(R.id.sign_up_search_address);




        progress_bar = findViewById(R.id.progress_bar);

        if(updateUser != null){

            sign_up_user_role.setEnabled(false);

            sign_up_user_role.setSelection(((ArrayAdapter)sign_up_user_role.getAdapter()).getPosition(updateUser.getRole()));

            sign_up_full_name.setText(updateUser.getFull_name());
            sign_up_email.setText(currentUser.getEmail());

            sign_up_address.setText(updateUser.getAddress());
            sign_up_city.setText(updateUser.getCity());

            String phoneNumber = updateUser.getPhone_number().substring(3);
            sign_up_phone_number.setText(phoneNumber);

            sign_up_confirm.setText(R.string.update_information);

        }

        searchPlace.setOnClickListener(new SearchPlace());

        sign_up_confirm.setOnClickListener(new SignUpConfirm());

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
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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


    private void updateInformation(){

        boolean hasError = false;

        String fullName = sign_up_full_name.getText().toString();
        String email = sign_up_email.getText().toString();
        String password = sign_up_password.getText().toString();
        String passwordConfirmation = sign_up_password_confirmation.getText().toString();
        String address = sign_up_address.getText().toString();
        String city = sign_up_city.getText().toString();
        String phoneNumber = sign_up_phone_number.getText().toString();


        if(stringValidation(fullName)){
            errorDisplayer(sign_up_full_name, getString(R.string.sign_up_full_name_no_text));
            hasError = true;
        }
        else if(stringValidation(fullName, FULL_NAME_LIMIT)){
            errorDisplayer(sign_up_full_name, getString(R.string.sign_up_full_name_limit));
            hasError = true;
        }

        if(stringValidation(email)){
            errorDisplayer(sign_up_email, getString(R.string.sign_up_email_no_text));
            hasError = true;
        }
        else if(!emailValidation(email)){
            errorDisplayer(sign_up_email, getString(R.string.sign_up_email_not_valid));
            hasError = true;
        }

        if(!stringValidation(password) && !stringValidation(password, passwordConfirmation)){
            errorDisplayer(sign_up_password, getString(R.string.sign_up_password_confirmation_required));
            hasError = true;
        }

        if(stringValidation(address)){
            errorDisplayer(sign_up_address, getString(R.string.sign_up_address_required));
            hasError = true;
        }

        if(stringValidation(city)){
            errorDisplayer(sign_up_city, getString(R.string.sign_up_city_required));
            hasError = true;
        }

        if(stringValidation(phoneNumber)){
            errorDisplayer(sign_up_phone_number, getString(R.string.sign_up_phone_number_required));
            hasError = true;
        }


        if(hasError){
            return;
        }

        progress_bar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(!stringValidation(email, currentUser.getEmail())){
            currentUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(!task.isSuccessful()){
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){
                            Helper.ToastDisplayer(cont, ex.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                }
            });
        }

        if(!stringValidation(password)){
            currentUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){
                            Helper.ToastDisplayer(cont, ex.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                }
            });
        }

        WriteBatch batch = mDatabase.batch();
        DocumentReference ref = mDatabase.collection(GlobalString.USER).document(updateUser.getDocumentId());

        if(!stringValidation(fullName, updateUser.getFull_name())){
            batch.update(ref,"full_name", fullName);
            updateUser.setFull_name(fullName);
        }

        if(!stringValidation(address, updateUser.getAddress())){
            batch.update(ref, "address", address);
            updateUser.setAddress(address);
        }

        if(!stringValidation(city, updateUser.getCity())){
            batch.update(ref, "city", city);
            updateUser.setCity(city);
        }

        phoneNumber = "+63" + phoneNumber;

        if(!stringValidation(phoneNumber, updateUser.getPhone_number())){
            batch.update(ref, "phone_number", phoneNumber);
            updateUser.setPhone_number(phoneNumber);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progress_bar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                if(!task.isSuccessful()){
                    try{
                        throw task.getException();
                    }
                    catch (Exception ex){
                        Helper.ToastDisplayer(cont, ex.getMessage(), Toast.LENGTH_LONG);
                    }
                }
                else{

                    Helper.ToastDisplayer(cont, getString(R.string.successfully_update), Toast.LENGTH_SHORT);

                    Helper.currentUser = updateUser;

                    Intent intent = new Intent();

                    intent.putExtra("updatedUser", (Serializable) updateUser);

                    setResult(RESULT_OK, intent);

                    finish();

                }

            }
        });

    }

    private boolean stringValidation(String text){
        return TextUtils.isEmpty(text);
    }

    private boolean stringValidation(String text, int limit){

        if(text.length() > limit){
            return true;
        }

        return stringValidation(text);

    }

    private boolean stringValidation(String text, String equal){
        return text.equals(equal);

    }

    private boolean emailValidation(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private <E>void errorDisplayer(E eItem, String message){

        if(eItem instanceof TextView){
            TextView item = (TextView) eItem;

            item.setError(message);

        }

    }



    class SignUpConfirm implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            if(updateUser != null){
                updateInformation();
            }
            else{
                signUp();
            }
        }
    }

    class SearchPlace implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplicationContext(), SearchPlaceActivity.class);

            startActivityForResult(intent, SEARCH_PLACE_ID);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode){

            case SEARCH_PLACE_ID:

                if(resultCode == RESULT_OK){

                    String address = data.getStringExtra("address");

                    sign_up_address.setText(address);

                }

                break;

        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
