package com.capstone.agree_culture.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Cart;
import com.capstone.agree_culture.Model.User;
import com.capstone.agree_culture.R;
import com.capstone.agree_culture.Model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class SearchProductListAdapter extends RecyclerView.Adapter<SearchProductListAdapter.MyViewHolder> {

    private List<Product> products;
    private DecimalFormat format;

    private Context context;

    private FirebaseFirestore mDatabase;

    private final int SMS_MESSAGE = 107;

    public SearchProductListAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;

        format = new DecimalFormat("#,###,###");

        mDatabase = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_search_product_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Product product = products.get(i);

        myViewHolder.product_name.setText(product.getProduct_name());
        myViewHolder.product_detail.setText(myViewHolder.itemView.getContext().getResources().getString(R.string.search_product_list_item, format
                .format(product.getProduct_price()), product.getProduct_quantity().toString(), product.getProduct_minimum().toString()));


        final String desc = myViewHolder.product_desc.getText().toString();

        if(desc.isEmpty()){
            if(product.getUser() != null){

                myViewHolder.product_desc.setText(myViewHolder.itemView.getContext().getResources().getString(R.string.product_search_desc_name,
                        product.getUser().getFull_name(),
                        product.getUser().getRole(),
                        product.getUser().getPhone_number()));

            }
        }

        if(!TextUtils.isEmpty(product.getProductPhoto())){
            Glide.with(context).load(product.getProductPhoto()).placeholder(R.drawable.imageview_rectangular).into(myViewHolder.product_image);
        }


        myViewHolder.message.setOnClickListener(null);
        myViewHolder.cart.setOnClickListener(null);

        myViewHolder.cart.setOnClickListener(new CartProduct(product));
        myViewHolder.message.setOnClickListener(new Message(product.getUser()));

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView product_image;
        public TextView product_name, product_detail, product_desc;
        public ImageButton message, cart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            product_image = itemView.findViewById(R.id.search_product_list_image);
            product_name = itemView.findViewById(R.id.search_product_list_label);
            product_desc = itemView.findViewById(R.id.search_product_list_desc);
            product_detail = itemView.findViewById(R.id.search_product_list_detail);

            message = (ImageButton) itemView.findViewById(R.id.msg_product);
            cart = (ImageButton) itemView.findViewById(R.id.add_to_cart_product);

        }
    }


    class Message implements View.OnClickListener{

        User user;

        Message(User user){
            this.user = user;
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Intent.ACTION_SENDTO);

            intent.setData(Uri.parse("smsto:" + user.getPhone_number()));

            ((Activity)context).startActivityForResult(intent, SMS_MESSAGE);

            /**
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                ((Activity)context).startActivityForResult(intent, SMS_MESSAGE);
            }
            else{
                // Display error message to say Google Android SMS APP required.
                Toast.makeText(context, context.getResources().getString(R.string.cart_add_error), Toast.LENGTH_SHORT ).show();
            }
             */

        }
    }


    class CartProduct implements View.OnClickListener{

        Product product;

        CartProduct(Product product){
            this.product = product;
        }

        @Override
        public void onClick(View v) {


            if(product.getProduct_quantity() <= 0){
                Toast.makeText(context, context.getResources().getString(R.string.cart_add_quantity_zero), Toast.LENGTH_LONG).show();

                return;
            }

            int quantity = product.getProduct_minimum();

            if(quantity > product.getProduct_quantity()){
                quantity = product.getProduct_quantity();
            }

            final Cart cart = new Cart(product.getProduct_name(), quantity, product.getProduct_price(), product.getCollection_id(), product.getUser_id(), Helper.currentUser.getDocumentId());


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(context.getResources().getString(R.string.confirm));
            builder.setMessage(context.getResources().getString(R.string.cart_add_message));

            builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mDatabase.collection(GlobalString.CART).whereEqualTo("ownerUidRef", cart.getOwnerUidRef()).whereEqualTo("buyerUidRef", cart.getBuyerUidRef()).whereEqualTo("productUidRef", cart.getProductUidRef()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            if(queryDocumentSnapshots.isEmpty()){

                                mDatabase.collection(GlobalString.CART).document().set(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, context.getResources().getString(R.string.card_add_success), Toast.LENGTH_LONG).show();
                                        } else {
                                            try {

                                                throw task.getException();

                                            } catch (Exception ex) {
                                                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    }
                                });

                            }
                            else{
                                Toast.makeText(context, context.getResources().getString(R.string.cart_add_product_exists), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }
            }).setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.create().show();


        }
    }



}
