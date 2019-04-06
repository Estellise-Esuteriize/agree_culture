package com.capstone.agree_culture.Fragments;

import android.content.DialogInterface;
import android.media.MediaSync;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private RequestQueue queue;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_cart, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDatabase = FirebaseFirestore.getInstance();

        queue = Volley.newRequestQueue(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.menu_cart_recycler);
        orderButton = (Button) view.findViewById(R.id.menu_cart_order_btn);
        progressBar = view.findViewById(R.id.progress_bar);

        currentUser = Helper.currentUser;

        fragment = this;


        mAdapter = new MenuMyCartListAdapter(this, orders);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if(orders.isEmpty()){

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
                        }

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
        else{

            if(Helper.newOrder != null && !Helper.newOrder.isEmpty()){
                for(Orders order : Helper.newOrder){

                    orders.add(0, order);
                    mAdapter.notifyItemRangeInserted(orders.size() - 1, orders.size());

                }
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

        List<String> ownerUidRef;

        public OrderProduct(){

            ownerUidRef = new ArrayList<>();

        }

        @Override
        public void onClick(View v) {

            if(orders.isEmpty()){
                return;
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.confirm));
            builder.setMessage(R.string.menu_cart_confirm_order);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    WriteBatch batch = mDatabase.batch();

                    for(Orders order : orders){

                        DocumentReference rec = mDatabase.collection(GlobalString.ORDERS).document(order.getCollectionId());
                        batch.update(rec, "status", Orders.ORDER);

                        ownerUidRef.add(order.getProductOwnerUidRef());

                    }

                    ownerUidRef = Helper.handleDuplicates(ownerUidRef);

                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.VISIBLE);

                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                orders.clear();
                                mAdapter.notifyDataSetChanged();

                                for(final String ref : ownerUidRef){

                                    mDatabase.collection(GlobalString.USER).document(ref).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            progressBar.setVisibility(View.GONE);

                                            Toast.makeText(getContext(), getResources().getString(R.string.menu_cart_order_success), Toast.LENGTH_SHORT).show();

                                            if(task.isSuccessful()){

                                                if(task.getResult().exists()){

                                                    User user = task.getResult().toObject(User.class);

                                                    final String phoneNumber = user.getPhone_number();

                                                    final String url = "https://www.itexmo.com/php_api/api.php";

                                                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Log.d("Response", response);
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.d("ErrorResponse", error.getMessage());
                                                        }
                                                    }){
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {

                                                            Map<String, String> params = new HashMap<>();

                                                            params.put("1", phoneNumber);
                                                            params.put("2", Helper.MESSAGE_ORDER);
                                                            params.put("3", Helper.ITEXMO_API);

                                                            return params;

                                                        }
                                                    };

                                                    queue.add(request);
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
                                catch (Exception ex){

                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    });



                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.create().show();


        }
    }

}
