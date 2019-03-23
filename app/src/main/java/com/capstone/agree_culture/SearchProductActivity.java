package com.capstone.agree_culture;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.capstone.agree_culture.Adapter.SearchProductListAdapter;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.R;
import com.capstone.agree_culture.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchProductListAdapter searchAdapter;
    private List<Product> products = new ArrayList<Product>();
    private RecyclerView.LayoutManager searchLayout;

    private FirebaseFirestore mDatabase;

    private View progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        mDatabase = FirebaseFirestore.getInstance();

        progress_bar = findViewById(R.id.progress_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.product_search_list);
        recyclerView.setHasFixedSize(true);
        searchLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(searchLayout);
        searchAdapter = new SearchProductListAdapter(products);

        recyclerView.setAdapter(searchAdapter);

        final String search_data = getIntent().getStringExtra("search_data");

        if(products.isEmpty()){
            Log.d("Acabal","Acabal");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progress_bar.setVisibility(View.VISIBLE);


            mDatabase.collection(GlobalString.PRODUCTS).whereEqualTo("product_name", search_data).whereEqualTo("user_product_type",
                    Helper.userProductType(Helper.currentUser.getRole())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isComplete()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            products.add(document.toObject(Product.class));
                            products.get(products.size() - 1).setCollection_id(document.getId());
                        }

                        Log.d("ProductsSize", "" + products.size());

                        progress_bar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        searchAdapter.notifyDataSetChanged();
                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){

                            progress_bar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(SearchProductActivity.this, search_data, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
