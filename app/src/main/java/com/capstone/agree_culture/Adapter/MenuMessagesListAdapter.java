package com.capstone.agree_culture.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.agree_culture.Model.Messages;

import java.util.List;

public class MenuMessagesListAdapter extends RecyclerView.Adapter<MenuMessagesListAdapter.MyViewHolder> {



    private Context context;
    private List<Messages> messages;


    public MenuMessagesListAdapter(List<Messages> messages, Context context){
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
