package com.capstone.agree_culture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.capstone.agree_culture.Adapter.SearchProductListAdapter;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Product;
import com.capstone.agree_culture.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductsSearchActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private SearchProductListAdapter searchAdapter;
    private List<Product> products = new ArrayList<Product>();
    private RecyclerView.LayoutManager searchLayout;

    private FirebaseFirestore mDatabase;

    private View progress_bar;

    private String searchKeyword;


    private final int SMS_MESSAGE = 107;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        mDatabase = FirebaseFirestore.getInstance();

        /**
         * Toolbar
         * --
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        /**
         * End of Toolbar
         */

        progress_bar = findViewById(R.id.progress_bar);

        recyclerView = (RecyclerView) findViewById(R.id.product_search_list);
        recyclerView.setHasFixedSize(true);
        searchLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(searchLayout);
        searchAdapter = new SearchProductListAdapter(products, this);

        recyclerView.setAdapter(searchAdapter);

        searchKeyword = getIntent().getStringExtra("search_data");

        if(products.isEmpty()){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progress_bar.setVisibility(View.VISIBLE);


            mDatabase.collection(GlobalString.PRODUCTS).whereEqualTo("productName", searchKeyword).whereEqualTo("userProductType",
                    Helper.userProductType(Helper.currentUser.getRole())).whereEqualTo("productStatus", Product.PRODUCT_STATUS_ENABLE).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isComplete()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            products.add(document.toObject(Product.class));
                            products.get(products.size() - 1).setCollectionId(document.getId());

                            mDatabase.collection(GlobalString.USER).document(products.get(products.size() - 1).getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                private int index;

                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();

                                        User user = (User)document.toObject(User.class);

                                        products.get(index).setUser(user);
                                        products.get(index).getUser().setDocumentId(document.getId());

                                        searchAdapter.notifyItemChanged(index);

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

                                private OnCompleteListener<DocumentSnapshot> init(int index){
                                    this.index = index;
                                    return this;
                                }

                            }.init(products.size() - 1));

                        }

                        if(!products.isEmpty()){
                            searchAdapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.product_search_no_available), Toast.LENGTH_SHORT).show();
                        }

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progress_bar.setVisibility(View.GONE);

                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){

                            progress_bar.setVisibility(View.GONE);

                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Toast.makeText(ProductsSearchActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }
            });
        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == SMS_MESSAGE && resultCode == Activity.RESULT_OK){

        }

        Log.d("Message", "Activity Result for Message is success");

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
