package com.capstone.agree_culture;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;

public class ProductsCreationActivity extends AppCompatActivity {


    private EditText product_name;
    private EditText product_price;
    private EditText product_quantity;
    private EditText product_minimum;


    private NumberFormat currentFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_creation);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        product_name = (EditText) findViewById(R.id.product_create_name);
        product_price = (EditText) findViewById(R.id.product_create_price);
        product_quantity = (EditText) findViewById(R.id.product_create_quantity);
        product_minimum = (EditText) findViewById(R.id.product_create_minimum);

        currentFormat = NumberFormat.getCurrencyInstance();


        product_price.addTextChangedListener(new PriceCurrencyFormat());


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
