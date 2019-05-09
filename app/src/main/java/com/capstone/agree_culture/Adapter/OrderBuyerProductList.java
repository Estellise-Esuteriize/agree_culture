package com.capstone.agree_culture.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Orders;
import com.capstone.agree_culture.Model.Product;
import com.capstone.agree_culture.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

public class OrderBuyerProductList extends RecyclerView.Adapter<OrderBuyerProductList.MyViewHolder> {


    private List<Orders> orders;

    private Context context;

    private FirebaseFirestore mDatabase;

    private OnOrderBuyerProducts onOrderBuyerProducts;

    public OrderBuyerProductList(Context context, List<Orders> orders){
        this.context = context;
        this.orders = orders;

        onOrderBuyerProducts = (OnOrderBuyerProducts) context;

        mDatabase = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_order_buyer_product_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Orders order = orders.get(i);


        final int quantity = order.getProductQuantity();

        final MyViewHolder item = myViewHolder;

        mDatabase.collection(GlobalString.PRODUCTS).document(order.getProductUidRef()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        Product product = task.getResult().toObject(Product.class);

                        double price;

                        try{
                            price = product.getProductPrice();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            price = 0d;
                        }


                        double totalPrice = quantity * price;
                        double productTaxPercentage = Helper.calculatePercentage(price);

                        Log.d("TaxPercentage", productTaxPercentage + "");

                        totalPrice += productTaxPercentage;


                        DecimalFormat format = new DecimalFormat("#,###.00");

                        Glide.with(item.itemView.getContext()).load(product.getProductPhoto()).placeholder(R.drawable.imageview_rectangular).into(item.photo);

                        /*
                        item.name.setText(product.getProductName());
                        item.quantity.setText(item.itemView.getContext().getResources().getString(R.string.buyer_list_quantity, String.format("%s", quantity)));
                        item.price.setText(item.itemView.getContext().getResources().getString(R.string.order_buyer_product_total_amount, "Price per kg.", format.format(price)));

                        item.totalPrice.setText(item.itemView.getContext().getResources().getString(R.string.order_buyer_product_total_amount, "Total Price", format.format(totalPrice)));
                        /*/

                        String prodQuantity = "";
                        String prodWeight = "";
                        String prodPrice = "";
                        String prodTax = "";
                        String prodTotalPrice = "";

                        try {
                            prodQuantity = Integer.toString(quantity);
                            prodPrice = format.format(price);
                            prodTax = Integer.toString(Helper.productTax);
                            prodTax += "%";
                            prodTotalPrice = Double.toString(totalPrice);
                            prodWeight = Integer.toString(product.getProductKg());
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                        }

                        item.desc.setText(item.itemView.getContext().getResources().getString(R.string.productDesc2, prodQuantity, prodWeight, prodPrice, prodTax, prodTotalPrice));

                        item.cancel.setOnClickListener(null);
                        item.cancel.setOnClickListener(new CancelProduct(item.itemView.getContext(), order));

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


        public ImageView photo, cancel;
        public TextView name, quantity, price, totalPrice, desc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = (ImageView) itemView.findViewById(R.id.order_buyer_product_photo);
            cancel = (ImageView) itemView.findViewById(R.id.menu_order_cancel);

            name = (TextView) itemView.findViewById(R.id.order_buyer_product_name);
            desc = (TextView) itemView.findViewById(R.id.order_buyer_product_desc);
            /*
            quantity = (TextView) itemView.findViewById(R.id.order_buyer_product_quantity);
            price = (TextView) itemView.findViewById(R.id.order_buyer_product_price);
            totalPrice = (TextView) itemView.findViewById(R.id.order_buyer_product_total);
            */

        }
    }

    class CancelProduct implements View.OnClickListener{


        private Orders order;
        private Context context;

        public CancelProduct(Context context, Orders order){
            this.context = context;
            this.order = order;
        }

        @Override
        public void onClick(View v) {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.confirm));
            builder.setMessage(context.getResources().getString(R.string.cancel_product));
            builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onOrderBuyerProducts.onCancelOrder(order);
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




    public interface OnOrderBuyerProducts{
        void onCancelOrder(Orders order);
    }

}
