package com.capstone.agree_culture.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.capstone.agree_culture.ProductsUpdateActivity;
import com.capstone.agree_culture.R;
import com.capstone.agree_culture.Model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.List;

public class MainMenuProductListsAdapter extends RecyclerView.Adapter<MainMenuProductListsAdapter.MyViewHolder> {

    private List<Product> products;

    private DecimalFormat format;

    private FirebaseFirestore mDatabase;

    private Context context;

    private Fragment fragment;

    private OnProductClick onProduct;

    private final int PRODUC_UPDATE_ID = 101;

    public MainMenuProductListsAdapter(List<Product> products, OnProductClick onProduct, Fragment fragment){
        this.products = products;
        this.onProduct = onProduct;
        this.fragment = fragment;

        format = new DecimalFormat("#,###,###");

        mDatabase = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_main_product_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Product product = products.get(i);

        myViewHolder.product_label.setText(product.getProductName());
        myViewHolder.product_desc.setText("----------");

        myViewHolder.product_detail.setText(myViewHolder.itemView.getContext().getResources().getString(R.string.main_menu_product_list_item, format.format(product.getProductPrice()), product.getProductQuantity().toString(), product.getProductMinimum().toString()));

        myViewHolder.product_delete.setOnClickListener(new ProductDelete(product));

        if(Helper.currentUser.getRole().equals(GlobalString.SUPPLIER)){
            myViewHolder.itemView.setOnClickListener(new ProductUpdate(product));
        }

        if(!TextUtils.isEmpty(product.getProductPhoto())){

            Log.d("MainMenuProductList", product.getProductPhoto());

            Glide.with(context).load(product.getProductPhoto()).placeholder(R.drawable.imageview_rectangular).into(myViewHolder.product_image);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView product_image;
        public ImageButton product_delete;
        public TextView product_label, product_desc, product_detail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            product_image = itemView.findViewById(R.id.main_menu_product_list_image);
            product_label = itemView.findViewById(R.id.main_menu_product_list_label);
            product_desc = itemView.findViewById(R.id.main_menu_product_list_desc);
            product_detail = itemView.findViewById(R.id.main_menu_product_list_detal);
            product_delete = itemView.findViewById(R.id.main_menu_product_list_delete);

        }
    }

    class ProductUpdate implements View.OnClickListener{

        private Product product;
        private int position;

        ProductUpdate(Product product){
            this.product = product;

            position = products.indexOf(product);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context, ProductsUpdateActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("position", position);
            fragment.startActivityForResult(intent, PRODUC_UPDATE_ID);

        }
    }

    class ProductDelete implements View.OnClickListener{

        private Product product;


        ProductDelete(Product product){
            this.product = product;
        }

        @Override
        public void onClick(View v) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(context.getResources().getString(R.string.product));
            dialog.setMessage(context.getResources().getString(R.string.main_menu_product_delete_message));
            dialog.setCancelable(true);
            dialog.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mDatabase.collection(GlobalString.PRODUCTS).document(product.getCollectionId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            onProduct.onDelete(product);
                            Toast.makeText(context, context.getResources().getString(R.string.main_menu_product_delete_success), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }).setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.create().show();

        }
    }


    public interface OnProductClick{
        void onDelete(Product product);
    }

}
