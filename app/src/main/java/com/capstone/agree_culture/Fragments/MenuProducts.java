package com.capstone.agree_culture.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.capstone.agree_culture.Adapter.MainMenuProductListsAdapter;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.ProductsCreationActivity;
import com.capstone.agree_culture.R;
import com.capstone.agree_culture.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuProducts extends Fragment {


    private List<Product> products = new ArrayList<Product>();

    private FloatingActionButton float_button;

    private final int ADD_PRODUCT_ID = 111;

    private View progress_bar;


    /**
     * Firebase
     *  - Firestore
     * @return
     */
    private FirebaseFirestore mDatabase;

    /**
     * User UUID
     */
    private String user_uid;


    /**
     * Recycler view
     * Adapter for Recyclerview
     */

    private RecyclerView product_recyclerview;
    private MainMenuProductListsAdapter mAdapter;

    private Boolean isInitialize = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_menu_products, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDatabase = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        float_button = (FloatingActionButton) view.findViewById(R.id.fab);
        product_recyclerview = view.findViewById(R.id.menu_products_list);
        progress_bar = view.findViewById(R.id.progress_bar);

        mAdapter = new MainMenuProductListsAdapter(products);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        product_recyclerview.setLayoutManager(mLayoutManager);
        product_recyclerview.setItemAnimator(new DefaultItemAnimator());
        product_recyclerview.setAdapter(mAdapter);

        if(products.isEmpty()){
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progress_bar.setVisibility(View.VISIBLE);


            mDatabase.collection(GlobalString.PRODUCTS).whereEqualTo("user_id", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isComplete()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            products.add(document.toObject(Product.class));
                            products.get(products.size() - 1).setCollection_id(document.getId());
                        }

                        Log.d("ProductsSize", "" + products.size());

                        progress_bar.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        mAdapter.notifyDataSetChanged();
                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){

                            progress_bar.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });
        }


        float_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("OhYeahh", "Fack You");

                Intent intent = new Intent(getActivity(), ProductsCreationActivity.class);
                startActivityForResult(intent, ADD_PRODUCT_ID);
            }
        });


        product_recyclerview.setAdapter(mAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ADD_PRODUCT_ID && resultCode == Activity.RESULT_OK){
            Product product = (Product)data.getExtras().getSerializable("product");
            products.add(0, product);
            mAdapter.notifyItemInserted(0);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}
