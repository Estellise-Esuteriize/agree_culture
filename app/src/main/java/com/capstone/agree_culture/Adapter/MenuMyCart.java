package com.capstone.agree_culture.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Orders;
import com.capstone.agree_culture.Model.Product;
import com.capstone.agree_culture.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MenuMyCart extends RecyclerView.Adapter<MenuMyCart.MyViewHolder> {


    private List<Orders> orders = new ArrayList<>();

    private Context context;

    /**
     * Firebase variables
     *
     */

    private FirebaseFirestore mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    public MenuMyCart(Context context, List<Orders> orders){
        this.context = context;
        this.orders = orders;

        mDatabase = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_main_cart, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Orders order = orders.get(i);

        final  MyViewHolder item = myViewHolder;

        CollectionReference ref = mDatabase.collection(GlobalString.PRODUCTS);
        ref.orderBy("createdAt");
        ref.document(order.getProductUidRef()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    Product product = task.getResult().toObject(Product.class);

                    Glide.with(item.itemView.getContext()).load(product.getProductPhoto()).placeholder(R.drawable.imageview_rectangular).into(item.productPhoto);

                    item.productStatus.setTextColor(Helper.orderStatusColors(product.getProductStatus()));
                    item.productStatus.setText(product.getProductStatus());

                    double price = (double)product.getProductQuantity() * product.getProductPrice();

                    item.productDesc.setText(item.itemView.getContext().getResources().getString(R.string.menu_cart_desc, product.getProductQuantity().toString(), product.getProductPrice().toString(), Double.toString(price)));




                }
                else{
                    try{
                        throw task.getException();
                    }catch (Exception ex){
                        Toast.makeText(item.itemView.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        public ImageView productPhoto, productRemove, productPlus, productMinus;
        public TextView productStatus, productDesc;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productPhoto = (ImageView) itemView.findViewById(R.id.menu_cart_photo);
            productStatus = (TextView) itemView.findViewById(R.id.menu_cart_status);
            productDesc = (TextView) itemView.findViewById(R.id.menu_cart_desc);
            productRemove = (ImageView) itemView.findViewById(R.id.menu_cart_delete);
            productPlus = (ImageView) itemView.findViewById(R.id.menu_cart_addition);
            productMinus = (ImageView) itemView.findViewById(R.id.menu_cart_minus);


        }
    }


    class 


}
