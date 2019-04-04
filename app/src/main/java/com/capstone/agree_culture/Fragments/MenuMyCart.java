package com.capstone.agree_culture.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.capstone.agree_culture.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuMyCart extends Fragment {



    private RecyclerView recyclerView;


    private Button orderButton;


    /**
     * Firebase variables
     *  -
     */
    private FirebaseFirestore mDatabase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_cart, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDatabase = FirebaseFirestore.getInstance();


        recyclerView = (RecyclerView) view.findViewById(R.id.menu_cart_recycler);
        orderButton = (Button) view.findViewById(R.id.menu_cart_order_btn);




    }



}
