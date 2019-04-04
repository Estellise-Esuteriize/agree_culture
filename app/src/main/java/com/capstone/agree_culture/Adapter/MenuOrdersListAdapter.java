package com.capstone.agree_culture.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstone.agree_culture.Model.User;
import com.capstone.agree_culture.OrderBuyerProducts;
import com.capstone.agree_culture.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;

public class MenuOrdersListAdapter extends RecyclerView.Adapter<MenuOrdersListAdapter.MyViewHolder> {

    private List<User> buyers;

    private Context context;

    private FirebaseFirestore mDatabase;

    private OnOrderClick onOrderClick;

    public MenuOrdersListAdapter(Fragment fragment, List<User> buyers){

        context = fragment.getContext();

        this.buyers = buyers;

        onOrderClick = (OnOrderClick) fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_main_orders_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        User user = buyers.get(i);

        Glide.with(myViewHolder.itemView.getContext()).load(user.getPhoto()).placeholder(R.drawable.imageview_rectangular).into(myViewHolder.photo);

        myViewHolder.name.setText(user.getFull_name());

        myViewHolder.address.setText(user.getAddress());

        myViewHolder.number.setText(user.getPhone_number());

        myViewHolder.itemView.setOnClickListener(null);
        myViewHolder.cancel.setOnClickListener(null);
        myViewHolder.deliver.setOnClickListener(null);



        myViewHolder.cancel.setOnClickListener(new CancelOrder(myViewHolder.itemView.getContext(), user));


    }

    @Override
    public int getItemCount() {
        return buyers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        public ImageView photo, deliver, cancel;
        public TextView name, address, number;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = (ImageView) itemView.findViewById(R.id.menu_order_photo);
            deliver = (ImageView) itemView.findViewById(R.id.menu_order_deliver);
            cancel = (ImageView) itemView.findViewById(R.id.menu_order_cancel);
            name = (TextView) itemView.findViewById(R.id.menu_order_user_name);
            address = (TextView) itemView.findViewById(R.id.menu_order_address);
            number = (TextView) itemView.findViewById(R.id.menu_order_number);

        }

    }

    class OpenBoughtProducts implements View.OnClickListener{


        private Context context;
        private User user;

        public OpenBoughtProducts(Context context, User user){
            this.context = context;
            this.user = user;
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context, OrderBuyerProducts.class);
            intent.putExtra("buyer", user);
            (context).startActivity(intent);

        }
    }

    class CancelOrder implements View.OnClickListener{

        private User user;
        private Context context;

        public CancelOrder(Context context, User user){
            this.context = context;
            this.user = user;
        }

        @Override
        public void onClick(View v) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(context.getResources().getString(R.string.confirm));
            dialog.setMessage(context.getResources().getString(R.string.remove_product));
            dialog.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onOrderClick.onCancelOrder(user);
                }
            });
            dialog.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.create().show();

        }
    }



    public interface OnOrderClick{
        void onCancelOrder(User user);
        void onItemClick(User user);
    }


}
