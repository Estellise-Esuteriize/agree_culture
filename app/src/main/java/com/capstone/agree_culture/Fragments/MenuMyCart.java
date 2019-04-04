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
import com.capstone.agree_culture.Model.Messages;
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

public class MenuMyCart extends Fragment implements MenuMyCartListAdapter.OnProductClick {


    private List<Orders> orders = new ArrayList<>();

    private RecyclerView recyclerView;
    private MenuMyCartListAdapter mAdapter;

    private Button orderButton;
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
        orderButton = (Button) view.findViewById(R.id.menu_cart_order_btn);
        progressBar = view.findViewById(R.id.progress_bar);

        currentUser = Helper.currentUser;

        fragment = this;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if(orders.isEmpty()){

            mAdapter = new MenuMyCartListAdapter(this, orders);


            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);


            mDatabase.collection(GlobalString.ORDERS).whereEqualTo("productBuyerUidRef", currentUser.getDocumentId()).whereEqualTo("status", Orders.PENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.GONE);

                    if(task.isSuccessful()){

                        for(DocumentSnapshot ref : task.getResult()){
                            Orders order = ref.toObject(Orders.class);
                            order.setCollectionId(ref.getId());
                            orders.add(0, order);
                            mAdapter.notifyItemRangeInserted(orders.size() - 1, orders.size());
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
        else{

            for(Orders order : Helper.newOrder){

                orders.add(0, order);
                mAdapter.notifyItemRangeInserted(orders.size() - 1, orders.size());

            }

        }

        Helper.newMessage = new ArrayList<>();


        orderButton.setOnClickListener(null);
        orderButton.setOnClickListener(new OrderProduct());

    }


    @Override
    public void onRemove(int index) {

        orders.remove(index);
        mAdapter.notifyItemChanged(index);

    }


    class OrderProduct implements View.OnClickListener{

        public OrderProduct(){

        }

        @Override
        public void onClick(View v) {

            WriteBatch batch = mDatabase.batch();

            for(Orders order : orders){

                DocumentReference rec = mDatabase.collection(GlobalString.ORDERS).document(order.getCollectionId());
                batch.update(rec, "status", Orders.ORDER);

            }

            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        orders = new ArrayList<>();
                        mAdapter.notifyDataSetChanged();
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
