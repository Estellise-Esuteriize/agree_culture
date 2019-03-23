package com.capstone.agree_culture.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.agree_culture.R;
import com.capstone.agree_culture.model.Product;

import java.util.List;

public class SearchProductListAdapter extends RecyclerView.Adapter<SearchProductListAdapter.MyViewHolder> {

    private List<Product> products;

    public SearchProductListAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_search_product_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.product_name.setText("Saging");
        myViewHolder.product_detail.setText("Kaon Kaon ta");
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
