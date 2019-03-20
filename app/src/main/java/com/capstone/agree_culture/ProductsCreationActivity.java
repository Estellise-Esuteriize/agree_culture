package com.capstone.agree_culture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.agree_culture.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductsCreationActivity extends AppCompatActivity {


    private EditText product_name;
    private EditText product_price;
    private EditText product_quantity;
    private EditText product_minimum;

    private String user_uuid;

    private FirebaseFirestore mDatabase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_creation);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseFirestore.getInstance();

        if(user == null){
            finish();
        }
        else{
            user_uuid = user.getUid();
        }


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

        product_create_btn = (Button) findViewById(R.id.product_create_btn);

        progress_bar = findViewById(R.id.progress_bar);

        product_price.addTextChangedListener(new PriceCurrencyFormat());
        product_create_btn.setOnClickListener(new CreateProduct());


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
            Double prod_price;
            Integer prod_quantity;
            Integer prod_minimum;
            try{
                prod_name = product_name.getText().toString();
                prod_price = Double.parseDouble(product_price.getText().toString().replaceAll(",", ""));
                prod_quantity = Integer.parseInt(product_quantity.getText().toString());
                prod_minimum = Integer.parseInt(product_minimum.getText().toString());
            }
            catch (Exception ex){

                Toast.makeText(cont, ex.getMessage(), Toast.LENGTH_LONG).show();

                return ;
            }

            Boolean has_error = false;

            if (TextUtils.isEmpty(prod_name)) {
                product_name.setError(getResources().getString(R.string.product_create_name_error));
                has_error = true;
            }

            if (prod_price <= 0) {
                product_price.setError(getResources().getString(R.string.product_create_number_error, "Price"));
                has_error = true;
            }

            if (prod_quantity <= 0) {
                product_quantity.setError(getResources().getString(R.string.product_create_number_error, "Quantity"));
                has_error = true;
            }

            if (prod_minimum <= 0) {
                product_minimum.setError(getResources().getString(R.string.product_create_number_error, "Minimum"));
                has_error = true;
            }

            if (!has_error) {

                progress_bar.setVisibility(View.VISIBLE);

                product = new Product(user_uuid, prod_name, prod_price, prod_quantity, prod_minimum);

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
