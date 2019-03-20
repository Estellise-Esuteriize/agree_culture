package com.capstone.agree_culture.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.agree_culture.R;
import com.capstone.agree_culture.model.Product;

import java.util.List;

public class MainMenuProductListsAdapter extends RecyclerView.Adapter<MainMenuProductListsAdapter.MyViewHolder> {

    private List<Product> products;



    public MainMenuProductListsAdapter(List<Product> products){
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_main_product_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Product product = products.get(i);

        myViewHolder.product_label.setText(product.getProduct_name());
        myViewHolder.product_detail.setText("");
        myViewHolder.product_desc.setText(myViewHolder.itemView.getContext().getResources().getString(R.string.main_menu_product_list_item, product.getProduct_price(), product.getProduct_quantity(), product.getProduct_minimum()));

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

            product_image = itemView.findViewById(R.id.main_menu_product_list_image);
            product_label = itemView.findViewById(R.id.main_menu_product_list_label);
            product_desc = itemView.findViewById(R.id.main_menu_product_list_desc);
            product_detail = itemView.findViewById(R.id.main_menu_product_list_detal);
            product_delete = itemView.findViewById(R.id.main_menu_product_list_delete);

        }
    }

}
