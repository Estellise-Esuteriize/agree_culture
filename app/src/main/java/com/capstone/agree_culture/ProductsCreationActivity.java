package com.capstone.agree_culture;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;

public class ProductsCreationActivity extends AppCompatActivity {


    private EditText product_name;
    private EditText product_price;
    private EditText product_quantity;
    private EditText product_minimum;

    private String user_uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_creation);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            finish();
        }
        else{
            user_uuid = user.getUid();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        product_name = (EditText) findViewById(R.id.product_create_name);
        product_price = (EditText) findViewById(R.id.product_create_price);
        product_quantity = (EditText) findViewById(R.id.product_create_quantity);
        product_minimum = (EditText) findViewById(R.id.product_create_minimum);


        product_price.addTextChangedListener(new PriceCurrencyFormat());




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    class CreateProduct implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String prod_name = product_name.getText().toString();
            Double prod_price = Double.parseDouble(product_price.getText().toString());
            Integer prod_quantity = Integer.parseInt(product_quantity.getText().toString());
            Integer prod_minimum = Integer.parseInt(product_minimum.getText().toString());

            Boolean has_error = false;

            if(TextUtils.isEmpty(prod_name)){
                product_name.setError(getResources().getString(R.string.product_create_name_error));
                has_error = true;
            }

            if(prod_price <= 0){
                product_price.setError(getResources().getString(R.string.product_create_number_error, 'Price'));
                has_error = true;
            }




        }
    }


    class PriceCurrencyFormat implements TextWatcher{

        private String current;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!s.toString().equals(current)){
                product_price.removeTextChangedListener(this);

                String cleanString = s.toString().replaceAll("[$,.]", "");

                double parsed = Double.parseDouble(cleanString);
                String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                current = formatted;
                product_price.setText(formatted);
                product_price.setSelection(formatted.length());

                product_price.addTextChangedListener(this);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }




}
