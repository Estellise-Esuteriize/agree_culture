package com.capstone.agree_culture.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.agree_culture.R;
import com.capstone.agree_culture.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class SearchProductListAdapter extends RecyclerView.Adapter<SearchProductListAdapter.MyViewHolder> {

    private List<Product> products;
    private DecimalFormat format;

    public SearchProductListAdapter(List<Product> products) {
        this.products = products;

        format = new DecimalFormat("#,###,###");
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
        myViewHolder.product_detail.setText(myViewHolder.itemView.getContext().getResources().getString(R.string.main_menu_product_list_item, format
                .format(product.getProduct_price()), product.getProduct_quantity().toString(), product.getProduct_minimum().toString()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView product_image;
        public TextView product_name, product_detail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            product_image = itemView.findViewById(R.id.search_product_list_image);
            product_name = itemView.findViewById(R.id.search_product_list_label);
            product_detail = itemView.findViewById(R.id.search_product_list_detail);
        }
    }
}
