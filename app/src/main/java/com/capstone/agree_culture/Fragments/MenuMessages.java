package com.capstone.agree_culture.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.agree_culture.Adapter.MenuMessagesListAdapter;
import com.capstone.agree_culture.Helper.Helper;
import com.capstone.agree_culture.Model.Messages;
import com.capstone.agree_culture.Model.User;
import com.capstone.agree_culture.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MenuMessages extends Fragment {


    private List<Messages> messages = new ArrayList<>();

    private RecyclerView recyclerView;
    private MenuMessagesListAdapter mAdapter;

    private View progressBar;


    /**
     * Firebase Related
     */
    private FirebaseFirestore mDatabase;

    /**
     * Current User
     */
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDatabase = FirebaseFirestore.getInstance();

        currentUser = Helper.currentUser;


        recyclerView = (RecyclerView) view.findViewById(R.id.menu_messages);
        progressBar = view.findViewById(R.id.progress_bar);


        mAdapter = new MenuMessagesListAdapter(messages, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if(messages.isEmpty()){



        }
        else{

        }

        Helper.newMessage = new ArrayList<>();




    }
}
