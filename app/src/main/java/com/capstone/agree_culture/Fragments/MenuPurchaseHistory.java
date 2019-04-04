package com.capstone.agree_culture.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.capstone.agree_culture.Adapter.MenuMyCartListAdapter;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Orders;
import com.capstone.agree_culture.Model.User;
import com.capstone.agree_culture.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MenuPurchaseHistory extends Fragment {


    private List<Orders> orders = new ArrayList<>();

    private RecyclerView recyclerView;
    private MenuMyCartListAdapter mAdapter;

    private View progressBar;

    private User currentUser;

    private Fragment fragment;

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
        progressBar = view.findViewById(R.id.progress_bar);

        currentUser = Helper.currentUser;

        fragment = this;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if(orders.isEmpty()){

            mAdapter = new MenuMyCartListAdapter(this, orders);


            /*
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
            */

            mDatabase.collection(GlobalString.ORDERS).whereEqualTo("productBuyerUidRef", currentUser.getDocumentId()).whereEqualTo("status", Orders.DELIVERY).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().isEmpty()){

                            for(DocumentSnapshot ref : task.getResult()){
                                Orders order = ref.toObject(Orders.class);
                                order.setCollectionId(ref.getId());
                                orders.add(0, order);
                                mAdapter.notifyItemRangeInserted(0, orders.size());
                            }
                        }

                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            mDatabase.collection(GlobalString.ORDERS).whereEqualTo("productBuyerUidRef", currentUser.getDocumentId()).whereEqualTo("status", Orders.ORDER).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    if(task.isSuccessful()){

                        if(!task.getResult().isEmpty()) {

                            for (DocumentSnapshot ref : task.getResult()) {
                                Orders order = ref.toObject(Orders.class);
                                order.setCollectionId(ref.getId());
                                orders.add(0, order);
                                mAdapter.notifyItemRangeInserted(0, orders.size());
                            }
                        }
                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            mDatabase.collection(GlobalString.ORDERS).whereEqualTo("productBuyerUidRef", currentUser.getDocumentId()).whereEqualTo("status", Orders.COMPLETED).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    if(task.isSuccessful()){

                        if(!task.getResult().isEmpty()) {

                            for (DocumentSnapshot ref : task.getResult()) {
                                Orders order = ref.toObject(Orders.class);
                                order.setCollectionId(ref.getId());
                                orders.add(order);
                                mAdapter.notifyItemRangeInserted(orders.size() - 1, orders.size());
                            }
                        }

                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            mDatabase.collection(GlobalString.ORDERS).whereEqualTo("productBuyerUidRef", currentUser.getDocumentId()).whereEqualTo("status", Orders.CANCELED).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().isEmpty()) {

                            for (DocumentSnapshot ref : task.getResult()) {
                                Orders order = ref.toObject(Orders.class);
                                order.setCollectionId(ref.getId());
                                orders.add(order);
                                mAdapter.notifyItemRangeInserted(orders.size() - 1, orders.size());
                            }
                        }
                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (Exception ex){
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

        }


    }





}
