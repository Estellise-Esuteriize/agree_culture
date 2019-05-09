package com.capstone.agree_culture.Fragments;

import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.capstone.agree_culture.Adapter.MenuOrdersListAdapter;
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

public class MenuOrders extends Fragment implements MenuOrdersListAdapter.OnOrderClick {


    private List<User> buyers = new ArrayList<>();
    private List<Orders> orders = new ArrayList<>();


    private RecyclerView recyclerView;
    private MenuOrdersListAdapter mAdapter;

    private View progressBar;

    private FirebaseFirestore mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseFirestore.getInstance();

        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) view.findViewById(R.id.menu_order_recycler);

        buyers.clear();

        mAdapter = new MenuOrdersListAdapter(this, buyers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);


        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);

        mDatabase.collection(GlobalString.ORDERS).whereEqualTo("productOwnerUidRef", Helper.currentUser.getDocumentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.GONE);


                if(task.isSuccessful()){

                    List<String> ownerUuid = new ArrayList<>();

                    if(!task.getResult().isEmpty()){

                        for(DocumentSnapshot item : task.getResult()){

                            Orders order = item.toObject(Orders.class);
                            order.setCollectionId(item.getId());


                            if(!order.getStatus().equals(Orders.ORDER) && !order.getStatus().equals(Orders.DELIVERY)){
                                continue;
                            }

                            orders.add(order);

                            ownerUuid.add(order.getProductBuyerUidRef());

                        }

                        ownerUuid = handleDuplicates(ownerUuid);

                        for(String ids : ownerUuid){

                            mDatabase.collection(GlobalString.USER).document(ids).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){

                                        if(task.getResult().exists()){

                                            User user = task.getResult().toObject(User.class);
                                            user.setDocumentId(task.getResult().getId());

                                            buyers.add(user);

                                            mAdapter.notifyDataSetChanged();

                                        }

                                    }
                                    else{
                                        try {
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
                else{
                    try {
                        throw task.getException();
                    }
                    catch (Exception ex){

                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });


    }

    public <T> ArrayList<T> handleDuplicates(List<T> uuIds){

        ArrayList<T> newList = new ArrayList<>();

        for(T element : uuIds){

            if(!newList.contains(element)){
                newList.add(element);
            }

        }

        return newList;

    }


    @Override
    public void onCancelOrder(User user) {


        final User userz = user;

        mDatabase.collection(GlobalString.ORDERS).whereEqualTo("status", Orders.ORDER).whereEqualTo("productOwnerUidRef", Helper.currentUser.getDocumentId()).whereEqualTo("productBuyerUidRef", user.getDocumentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){


                    if(!task.getResult().isEmpty()){

                        WriteBatch batch = mDatabase.batch();

                        for(DocumentSnapshot item : task.getResult()){

                            Orders order = item.toObject(Orders.class);
                            order.setCollectionId(item.getId());
                            DocumentReference ref = mDatabase.collection(GlobalString.ORDERS).document(order.getCollectionId());
                            ref.update("status", Orders.CANCELED);
                        }

                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(getContext(), getResources().getString(R.string.menu_order_success_cancel_order), Toast.LENGTH_SHORT).show();

                                    int index = buyers.indexOf(userz);
                                    mAdapter.notifyItemMoved(index, index + 1);

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
                else{

                    try{
                        throw task.getException();
                    }
                    catch(Exception ex){
                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });


    }

    @Override
    public void onDeliverOrder(User user) {


        String orderUidRef = "";

        for(Orders order : orders){

            if(order.getProductBuyerUidRef().equals(user.getDocumentId())){
                orderUidRef = order.getCollectionId();
            }

        }

        Log.d("StringUUIDREF", orderUidRef);

        WriteBatch batch = mDatabase.batch();
        DocumentReference ref = mDatabase.collection(GlobalString.ORDERS).document(orderUidRef);

        batch.update(ref, "status", Orders.DELIVERY);

        batch.commit();


    }
}
