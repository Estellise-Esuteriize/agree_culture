package com.capstone.agree_culture.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.Model.Messages;
import com.capstone.agree_culture.Model.User;
import com.capstone.agree_culture.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MenuMessagesListAdapter extends RecyclerView.Adapter<MenuMessagesListAdapter.MyViewHolder> {


    private FirebaseFirestore mDatabase;

    private Context context;
    private List<Messages> messages;


    public MenuMessagesListAdapter(List<Messages> messages, Context context){
        this.messages = messages;
        this.context = context;

        mDatabase = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_main_messages_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Messages message = messages.get(i);

        final MyViewHolder desc = myViewHolder;

        mDatabase.collection(GlobalString.USER).document(message.getToUserUidRef()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        User user = task.getResult().toObject(User.class);

                        desc.desc.setText(desc.itemView.getContext().getResources().getString(R.string.menu_messages_desc, user.getFull_name(), user.getPhone_number()));

                        Glide.with(desc.itemView.getContext()).load(user.getPhoto()).placeholder(R.drawable.imageview_rectangular).into(desc.photo);

                    }

                }
            }
        });

        myViewHolder.itemView.setOnClickListener(null);

        myViewHolder.itemView.setOnClickListener(new OpenSms(message.getToUserNumber()));

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        public ImageView photo;
        public TextView desc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = (ImageView) itemView.findViewById(R.id.message_photo);
            desc = (TextView) itemView.findViewById(R.id.message_desc);

        }
    }


    class OpenSms implements View.OnClickListener{

        String phoneNo;

        public OpenSms(String phoneNo){
            this.phoneNo = phoneNo;
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Intent.ACTION_SENDTO);

            intent.setData(Uri.parse("smsto:" + phoneNo));

            context.startActivity(intent);

        }
    }


}
