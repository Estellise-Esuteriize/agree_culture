package com.capstone.agree_culture;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProductsCreationActivity extends AppCompatActivity {


    private EditText product_name;
    private EditText product_price;
    private EditText product_quantity;
    private EditText product_minimum;


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







    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }





}
