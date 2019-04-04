package com.capstone.agree_culture.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
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


    private final int PLUS = 101;
    private final int MINUS = 102;

    private OnProductClick productClick;

    public MenuMyCart(Context context, List<Orders> orders){
        this.context = context;
        this.orders = orders;

        mDatabase = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        productClick = (OnProductClick) context;
        
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
        final int index = i;

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

                    double price = (double)product.getProductMinimum() * product.getProductPrice();

                    item.productDesc.setText(item.itemView.getContext().getResources().getString(R.string.menu_cart_desc, Integer.toString(product.getProductMinimum()), Double.toString(product.getProductPrice()), Double.toString(price)));


                    item.productMinus.setOnClickListener(null);
                    item.productPlus.setOnClickListener(null);
                    item.productRemove.setOnClickListener(null);

                    item.productMinus.setOnClickListener(new ProductPlusMinus(MINUS, item.itemView.getContext(), item.productDesc, index, product));
                    item.productPlus.setOnClickListener(new ProductPlusMinus(PLUS, item.itemView.getContext(), item.productDesc, index, product));


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

    class ProductPlusMinus implements View.OnClickListener{

        private int type;
        private int index;

        private Context context;
        private TextView desc;

        private Product product;



        public ProductPlusMinus(int type, Context context, TextView desc, int index, Product product){
            this.type = type;

            this.context = context;
            this.desc = desc;

            this.index = index;

            this.product = product;


        }

        @Override
        public void onClick(View v) {

            Orders order = orders.get(index);

            if(type == PLUS){

                order.setProductQuantity(order.getProductQuantity() + 1);

                if(order.getProductQuantity() > product.getProductQuantity()){

                    order.setProductQuantity(order.getProductQuantity() - 1);

                    Toast.makeText(context, context.getResources().getString(R.string.menu_car_product_plus_limit), Toast.LENGTH_LONG).show();

                }
                else{

                    double price = order.getProductQuantity() * product.getProductPrice();

                    desc.setText(context.getResources().getString(R.string.menu_cart_desc, Integer.toString(order.getProductQuantity()), Double.toString(product.getProductPrice()), Double.toString(price)));

                }

            }
            else if(type == MINUS){

                order.setProductQuantity(order.getProductQuantity() - 1);

                if(order.getProductQuantity() < product.getProductMinimum()){

                    order.setProductQuantity(order.getProductQuantity() + 1);

                    Toast.makeText(context, context.getResources().getString(R.string.menu_car_product_plus_limit), Toast.LENGTH_LONG).show();

                }
                else{

                    double price = order.getProductQuantity() * product.getProductPrice();

                    desc.setText(context.getResources().getString(R.string.menu_cart_desc, Integer.toString(order.getProductQuantity()), Double.toString(product.getProductPrice()), Double.toString(price)));


                }

            }

            orders.set(index, order);

        }
    }


    class RemoveProduct implements View.OnClickListener{


        Context context;
        int index;

        public RemoveProduct(Context context, int index){

            this.context = context;
            this.index = index;

        }

        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.confirm));
            builder.setMessage(context.getResources().getString(R.string.remove_product));
            builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Orders order = orders.get(index);

                    mDatabase.collection(GlobalString.ORDERS).document(order.getCollectionId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                orders.remove(index);
                            }
                            else {
                                try{
                                    throw task.getException();
                                }
                                catch (Exception ex){
                                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    });


                }
            });
            builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.create().show();


        }

    }

    public interface OnProductClick{
        void onRemove(int index);
    }




}
