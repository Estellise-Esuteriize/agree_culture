package com.capstone.agree_culture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

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
     * Product List
     */
    private List<Product> products = new ArrayList<Product>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_creation);

        products = (ArrayList<Product>) getIntent().getSerializableExtra("product_list");

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

        progress_bar = findViewById(R.id.progress_bar);

        product_price.addTextChangedListener(new PriceCurrencyFormat());


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    class CreateProduct implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String prod_name = product_name.getText().toString();
            Double prod_price = Double.parseDouble(product_price.getText().toString());
            Integer prod_quantity = Integer.parseInt(product_quantity.getText().toString());
            Integer prod_minimum = Integer.parseInt(product_minimum.getText().toString());

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

                final Product product = new Product(user_uuid, prod_name, prod_price, prod_quantity, prod_minimum);

                mDatabase.collection("products").add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isComplete()) {
                            product.setCollection_id(task.getResult().getId());
                            products.add(0, product);

                            Intent intent = new Intent();
                            intent.putExtra("product_list_result", (Serializable)products);
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

        private String current;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                product_price.removeTextChangedListener(this);

                String cleanString = s.toString().replaceAll("[$,.]", "");

                double parsed = Double.parseDouble(cleanString);
                String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

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
