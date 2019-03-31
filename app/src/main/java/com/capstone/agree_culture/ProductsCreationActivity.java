package com.capstone.agree_culture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Product;
import com.capstone.agree_culture.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class ProductsCreationActivity extends AppCompatActivity {


    private EditText product_name;
    private EditText product_price;
    private EditText product_quantity;
    private EditText product_minimum;
    private ImageView productImageView;


    private String user_uuid;

    /**
     * Firebase Variables
     */
    private FirebaseFirestore mDatabase;
    private StorageReference mStorage;

    /**
     * Product Image Storage Reference
     */
    private StorageReference mProductPhoto;

    private Context cont;

    private View progress_bar;

    /**
     * Create Button
     */
    private Button product_create_btn;

    /**
     * Product List
     */
    private Product product;

    private User currentUser;

    /**
     * Product Image URI
     */
    private Uri productPhoto;

    /**
     *
     * product image request code
     */
    private final int REQUEST_CODE_PRODUCT_PHOTO = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_creation);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        if(user == null){
            finish();
        }
        else{
            user_uuid = user.getUid();
        }

        currentUser = Helper.currentUser;


        cont = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        product_name = (EditText) findViewById(R.id.product_create_name);
        product_price = (EditText) findViewById(R.id.product_create_price);
        product_quantity = (EditText) findViewById(R.id.product_create_quantity);
        product_minimum = (EditText) findViewById(R.id.product_create_minimum);
        productImageView = (ImageView) findViewById(R.id.product_create_image);

        product_create_btn = (Button) findViewById(R.id.product_create_btn);

        progress_bar = findViewById(R.id.progress_bar);

        product_price.addTextChangedListener(new PriceCurrencyFormat());
        product_create_btn.setOnClickListener(new CreateProduct());

        productImageView.setOnClickListener(new ProductPhoto());

        if(!currentUser.getRole().equals(GlobalString.SUPPLIER)){
            product_quantity.getBackground().setAlpha(64);
            product_quantity.setEnabled(false);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    class CreateProduct implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            String prod_name;
            double prod_price;
            int prod_quantity;
            int prod_minimum;
            try{
                prod_name = product_name.getText().toString();
                prod_price = Double.parseDouble(product_price.getText().toString().replaceAll(",", ""));
                prod_quantity = 0;
                prod_minimum = 0;

                if(!currentUser.getRole().equals(GlobalString.DISTRIBUTOR)){
                    prod_quantity = Integer.parseInt(product_quantity.getText().toString());
                }
                if(!TextUtils.isEmpty(product_minimum.getText().toString())){
                    prod_minimum = Integer.parseInt(product_minimum.getText().toString());
                }
            }
            catch (Exception ex){

                ex.printStackTrace();

                Toast.makeText(cont, ex.getMessage(), Toast.LENGTH_LONG).show();

                return;
            }

            boolean has_error = false;

            if (TextUtils.isEmpty(prod_name)) {
                product_name.setError(getResources().getString(R.string.product_create_name_error));
                has_error = true;
            }

            if (prod_price <= 0) {
                product_price.setError(getResources().getString(R.string.product_create_number_error, "Price"));
                has_error = true;
            }

            if (prod_quantity <= 0 && Helper.currentUser.getRole().equals(GlobalString.SUPPLIER)) {
                product_quantity.setError(getResources().getString(R.string.product_create_number_error, "Quantity"));
                has_error = true;
            }

            if (prod_minimum <= 0) {
                product_minimum.setError(getResources().getString(R.string.product_create_number_error, "Minimum"));
                has_error = true;
            }

            if(productPhoto == null){
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.product_create_product_photo_error), Toast.LENGTH_SHORT).show();
                has_error = true;
            }

            if (!has_error) {

                progress_bar.setVisibility(View.VISIBLE);

                mProductPhoto = mStorage.child(GlobalString.PRODUCTS).child(UUID.randomUUID().toString());
                UploadTask uploadTask = mProductPhoto.putFile(productPhoto);


                product = new Product(user_uuid, prod_name, prod_price, prod_quantity, prod_minimum, Helper.currentUser.getRole());

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        return mProductPhoto.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        product.setProductPhoto(task.getResult().toString());

                        mDatabase.collection("products").add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if (task.isComplete()) {
                                    product.setCollection_id(task.getResult().getId());

                                    Intent intent = new Intent();
                                    intent.putExtra("product", product);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();

                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (Exception ex) {
                                        progress_bar.setVisibility(View.GONE);
                                        Toast.makeText(cont, ex.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                        });
                    }
                    else{
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            e.getStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                    }
                });



            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_CODE_PRODUCT_PHOTO:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestPhotoImage();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode){
            case REQUEST_CODE_PRODUCT_PHOTO:

                if(resultCode == RESULT_OK && data.getData() != null){

                    productPhoto = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), productPhoto);
                        productImageView.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestPhotoImage(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PRODUCT_PHOTO);

    }

    class ProductPhoto implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PRODUCT_PHOTO);
                }else{
                    requestPhotoImage();
                }
            }
            else{
                requestPhotoImage();
            }
        }
    }


    class PriceCurrencyFormat implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            product_price.removeTextChangedListener(this);

            try {
                String originalString = s.toString();

                Long longval;
                if (originalString.contains(",")) {
                    originalString = originalString.replaceAll(",", "");
                }
                longval = Long.parseLong(originalString);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                String formattedString = formatter.format(longval);

                //setting text after format to EditText
                product_price.setText(formattedString);
                product_price.setSelection(product_price.getText().length());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            product_price.addTextChangedListener(this);
        }
    }
}
