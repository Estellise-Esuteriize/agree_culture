package com.capstone.agree_culture;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.agree_culture.Adapter.OrderBuyerProductList;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Orders;
import com.capstone.agree_culture.Model.Product;
import com.capstone.agree_culture.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderBuyerProducts extends AppCompatActivity implements OrderBuyerProductList.OnOrderBuyerProducts {


    private List<Orders> orders = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    private View progressBar;
    private RecyclerView recyclerView;

    private TextView total;
    private Button completed;

    private OrderBuyerProductList mAdapter;

    private FirebaseFirestore mDatabase;

    private User buyer;


    private double totalAmount = 0;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_buyer_products);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        buyer = (User) getIntent().getSerializableExtra("buyer");


        mDatabase = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.order_buyer_products_recycler);
        progressBar = findViewById(R.id.progress_bar);

        total = (TextView) findViewById(R.id.order_buyer_product_total_amount);
        completed = (Button) findViewById(R.id.order_buyer_product_delivered);

        mAdapter = new OrderBuyerProductList(this, orders);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        if(orders.isEmpty()){

            mDatabase.collection(GlobalString.ORDERS).whereEqualTo("productBuyerUidRef", buyer.getDocumentId()).whereEqualTo("productOwnerUidRef", Helper.currentUser.getDocumentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    if(task.isSuccessful()){

                        if(!task.getResult().isEmpty()){


                            for(DocumentSnapshot item : task.getResult()){

                                Orders order = item.toObject(Orders.class);
                                order.setCollectionId(item.getId());


                                if(!order.getStatus().equals(Orders.ORDER) && !order.getStatus().equals(Orders.DELIVERY)){
                                    continue;
                                }


                                orders.add(order);

                                final Orders fOrder = order;
                                final int quantity = order.getProductQuantity();


                                mDatabase.collection(GlobalString.PRODUCTS).document(order.getProductUidRef()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if(task.isSuccessful()){

                                            if(task.getResult().exists()){

                                                Product product = task.getResult().toObject(Product.class);

                                                Product prod = new Product(buyer.getDocumentId(), product.getProductName(), product.getProductPrice(), fOrder.getProductQuantity(), product.getProductMinimum(), buyer.getRole());
                                                prod.setProductPhoto(product.getProductPhoto());

                                                products.add(prod);

                                                double price = product.getProductPrice();

                                                totalAmount += quantity * price;

                                                DecimalFormat format = new DecimalFormat("#,###.00");

                                                total.setText(getResources().getString(R.string.order_buyer_product_total_amount, format.format(totalAmount)));
                                            }


                                        }

                                    }
                                });

                            }

                            mAdapter.notifyDataSetChanged();

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

    @Override
    public void onCancelOrder(final Orders order) {


        mDatabase.collection(GlobalString.ORDERS).document(order.getCollectionId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    int index = orders.indexOf(order);

                    orders.remove(index);
                    mAdapter.notifyItemRemoved(index);

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.success_product_remove), Toast.LENGTH_LONG).show();
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


    class CompletedDeliver implements View.OnClickListener{

        public CompletedDeliver(){}

        @Override
        public void onClick(View v) {

            WriteBatch batch = mDatabase.batch();

            for(Orders order : orders){

                DocumentReference ref = mDatabase.collection(GlobalString.ORDERS).document(order.getCollectionId());
                batch.update(ref, "status", Orders.COMPLETED);

            }

            for(Product product : products){

                mDatabase.collection(GlobalString.PRODUCTS).add(product);

            }

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.successfully_delivered), Toast.LENGTH_SHORT).show();
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

}
