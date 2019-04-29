package com.capstone.agree_culture.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.agree_culture.DeliveryOwnerMapActivity;
import com.capstone.agree_culture.Fragments.MenuPurchaseHistory;
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

import org.w3c.dom.Text;

import java.util.List;

public class MenuPurchaseHistoryListAdapter extends RecyclerView.Adapter<MenuPurchaseHistoryListAdapter.MyViewHolder> {

    private Fragment fragment;
    private List<Orders> orders;

    private FirebaseFirestore mDatabase;

    private PurchaseHistoryEvents purchaseHistoryEvents;

    public MenuPurchaseHistoryListAdapter(Fragment fragment, List<Orders> orders){
        this.fragment = fragment;
        this.orders = orders;

        mDatabase = FirebaseFirestore.getInstance();

        try{
            purchaseHistoryEvents = (PurchaseHistoryEvents) fragment;
        }
        catch (ClassCastException ex){
            Log.d("PurchaseHistory", ex.getMessage() + "");
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_main_purchase_history_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Orders order = orders.get(i);
        final Orders fOrders = order;


        final MyViewHolder item = myViewHolder;
        final int index = i;

        mDatabase.collection(GlobalString.PRODUCTS).document(order.getProductUidRef()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        Product product = task.getResult().toObject(Product.class);

                        product.setCollectionId(task.getResult().getId());

                        Glide.with(item.itemView.getContext()).load(product.getProductPhoto()).placeholder(R.drawable.imageview_rectangular).into(item.productPhoto);

                        item.productStatus.setTextColor(Helper.orderStatusColors(fOrders.getStatus()));
                        item.productStatus.setText(fOrders.getStatus());

                        item.productName.setText(product.getProductName());

                        double price = (double)product.getProductMinimum() * product.getProductPrice();

                        item.productDesc.setText(item.itemView.getContext().getResources().getString(R.string.menu_cart_desc, Integer.toString(product.getProductMinimum()), Double.toString(product.getProductPrice()), Double.toString(price)));


                        if(fOrders.getStatus().equals(Orders.CANCELED) || fOrders.getStatus().equals(Orders.COMPLETED) || fOrders.getStatus().equals(Orders.DELIVERY)){

                            item.productRemove.setVisibility(View.GONE);

                        }
                        else{

                            item.productRemove.setOnClickListener(null);

                            item.productRemove.setOnClickListener(new CancelOrderProduct(index));

                        }

                        if(product.getProductStatus().equals(Orders.DELIVERY)){
                            item.itemView.setOnClickListener(new ViewDelivery(item.itemView.getContext(), fOrders.getProductOwnerUidRef(), fOrders.getProductBuyerUidRef()));
                        }

                    }
                    else{
                        item.productStatus.setText(R.string.product_deleted);
                    }


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

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        public ImageView productPhoto, productRemove;
        public TextView productStatus, productDesc, productName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productPhoto = (ImageView) itemView.findViewById(R.id.menu_cart_photo);
            productStatus = (TextView) itemView.findViewById(R.id.menu_cart_status);
            productName = (TextView) itemView.findViewById(R.id.menu_cart_product_name);
            productDesc = (TextView) itemView.findViewById(R.id.menu_cart_desc);
            productRemove = (ImageView) itemView.findViewById(R.id.menu_cart_delete);

        }

    }


    class CancelOrderProduct implements View.OnClickListener{

        int index;

        public CancelOrderProduct(int index){
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            purchaseHistoryEvents.onCancelOrder(index);
        }

    }

    class ViewDelivery implements View.OnClickListener{

        private String ownerUuidRef;
        private String buyerUuidRef;

        private Context context;


        ViewDelivery(Context context, String ownerUuidRef, String buyerUuidRef){
            this.context = context;
            this.ownerUuidRef = ownerUuidRef;
            this.buyerUuidRef = buyerUuidRef;
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context, DeliveryOwnerMapActivity.class);

            intent.putExtra("ownerUidRef", ownerUuidRef);
            intent.putExtra("buyerUidRef", buyerUuidRef);

            context.startActivity(intent);

        }

    }


    public interface PurchaseHistoryEvents{

        public void onCancelOrder(int index);

    }


}
