package com.capstone.agree_culture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Product;
import com.capstone.agree_culture.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductsUpdateActivity extends AppCompatActivity {


    /**
     * Buttons
     *  - Disable Button
     *  - Enable Button
     *  - Update Button
     */

    private Button disable, enable, update;

    /**
     * Edittext
     *  - name of product
     *  - price of product
     *  - quantity of product
     *  - minimum purchase for product
     * ImageView
     *  - productPhoto
     *      - the photo of the product
     */
    private EditText product_name, product_price, product_quantity, product_minimum;
    private ImageView productPhoto;
    /**
     * variables from previous activity
     *  - product
     *      - the product to update
     *  - position
     *      - the index of the product in product list
     */
    private Product product;
    private int position;

    /**
     * Current User
     */
    private User currentUser;

    /**
     * FirebaseFirestore
     *  - mDatabase
     */
    private FirebaseFirestore mDatabase;

    private View progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_update);

        mDatabase = FirebaseFirestore.getInstance();

        product = (Product)getIntent().getSerializableExtra("product");
        position = getIntent().getIntExtra("position", 0);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        currentUser = Helper.currentUser;

        disable = (Button) findViewById(R.id.product_update_disable);
        enable = (Button) findViewById(R.id.product_update_enable);
        update = (Button) findViewById(R.id.product_update_btn);

        product_name = (EditText) findViewById(R.id.product_create_name);
        product_price = (EditText) findViewById(R.id.product_create_price);
        product_quantity = (EditText) findViewById(R.id.product_create_quantity);
        product_minimum = (EditText) findViewById(R.id.product_create_minimum);
        productPhoto = (ImageView) findViewById(R.id.product_create_image);

        progress_bar = findViewById(R.id.progress_bar);

        product_price.addTextChangedListener(new PriceCurrencyFormat());

        if(currentUser.getRole().equals(GlobalString.DISTRIBUTOR)){
            product_quantity.getBackground().setAlpha(64);
            product_quantity.setEnabled(false);
        }

        if(product.getProduct_status().equals(Product.PRODUCT_STATUS_ENABLE)){
            enable.getBackground().setAlpha(64);
            enable.setEnabled(false);
            disable.setEnabled(true);
        }
        else if(product.getProduct_status().equals(Product.PRODUCT_STATUS_DISABLED)){
            disable.getBackground().setAlpha(64);
            disable.setEnabled(false);
            enable.setEnabled(true);

            product_name.setEnabled(false);
            product_price.setEnabled(false);
            product_quantity.setEnabled(false);
            product_minimum.setEnabled(false);


            product_name.getBackground().setAlpha(64);
            product_price.getBackground().setAlpha(64);
            product_quantity.getBackground().setAlpha(64);
            product_minimum.getBackground().setAlpha(64);
        }


        DecimalFormat format = new DecimalFormat("#,###,###");

        product_name.setText(product.getProduct_name());
        product_price.setText(format.format(product.getProduct_price()));
        product_quantity.setText(product.getProduct_quantity().toString());
        product_minimum.setText(product.getProduct_minimum().toString());

        Glide.with(this).load(product.getProductPhoto()).placeholder(R.drawable.imageview_rectangular).into(productPhoto);


        enable.setOnClickListener(new EnableProduct());
        disable.setOnClickListener(new DisableProduct());

        update.setOnClickListener(new UpdateProduct());


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class UpdateProduct implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            WriteBatch batch = mDatabase.batch();
            DocumentReference ref = mDatabase.collection(GlobalString.PRODUCTS).document(product.getCollection_id());

            final String prod_name;
            final double prod_price;
            final int prod_quantity;
            final int prod_minimum;
            try{
                prod_name = product_name.getText().toString();
                prod_price = Double.parseDouble(product_price.getText().toString().replaceAll(",", ""));
                prod_quantity = Integer.parseInt(product_quantity.getText().toString());
                prod_minimum = Integer.parseInt(product_minimum.getText().toString());
            }
            catch (Exception ex){

                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();

                return ;
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

            if (prod_quantity <= 0 && currentUser.getRole().equals(GlobalString.SUPPLIER)) {
                product_quantity.setError(getResources().getString(R.string.product_create_number_error, "Quantity"));
                has_error = true;
            }

            if (prod_minimum <= 0) {
                product_minimum.setError(getResources().getString(R.string.product_create_number_error, "Minimum"));
                has_error = true;
            }

            if(!has_error){


                progress_bar.setVisibility(View.VISIBLE);

                batch.update(ref, "product_name", prod_name);
                batch.update(ref, "product_price", prod_price);
                batch.update(ref, "product_quantity", prod_quantity);
                batch.update(ref, "product_minimum", prod_minimum);



                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            progress_bar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.product_update_success), Toast.LENGTH_SHORT).show();

                            product.setProduct_name(prod_name);
                            product.setProduct_price(prod_price);
                            product.setProduct_quantity(prod_quantity);
                            product.setProduct_minimum(prod_minimum);

                            Intent intent = new Intent();
                            intent.putExtra("product", product);
                            intent.putExtra("position", position);
                            setResult(Activity.RESULT_OK, intent);
                            finish();

                        }
                        else{
                            progress_bar.setVisibility(View.GONE);
                            try{
                                throw task.getException();
                            }catch (Exception ex){
                                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });


            }

        }

    }

    class EnableProduct implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            WriteBatch batch = mDatabase.batch();

            DocumentReference ref = mDatabase.collection(GlobalString.PRODUCTS).document(product.getCollection_id());
            batch.update(ref, "product_status", Product.PRODUCT_STATUS_ENABLE);

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete()){
                        enable.getBackground().setAlpha(64);
                        enable.setEnabled(false);
                        disable.getBackground().setAlpha(255);
                        disable.setEnabled(true);

                        product_name.setEnabled(true);
                        product_price.setEnabled(true);
                        product_minimum.setEnabled(true);


                        product_name.getBackground().setAlpha(255);
                        product_price.getBackground().setAlpha(255);
                        product_minimum.getBackground().setAlpha(255);

                        if(!currentUser.getRole().equals(GlobalString.DISTRIBUTOR)){
                            product_quantity.setEnabled(true);
                            product_quantity.getBackground().setAlpha(255);
                        }

                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    class DisableProduct implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            WriteBatch batch = mDatabase.batch();

            DocumentReference ref = mDatabase.collection(GlobalString.PRODUCTS).document(product.getCollection_id());
            batch.update(ref, "product_status", Product.PRODUCT_STATUS_DISABLED);

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete()){
                        enable.getBackground().setAlpha(255);
                        enable.setEnabled(true);
                        disable.getBackground().setAlpha(64);
                        disable.setEnabled(false);

                        product_name.setEnabled(false);
                        product_price.setEnabled(false);
                        product_minimum.setEnabled(false);


                        product_name.getBackground().setAlpha(61);
                        product_price.getBackground().setAlpha(61);
                        product_minimum.getBackground().setAlpha(61);

                        if(!currentUser.getRole().equals(GlobalString.DISTRIBUTOR)){
                            product_quantity.setEnabled(false);
                            product_quantity.getBackground().setAlpha(61);
                        }

                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
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
