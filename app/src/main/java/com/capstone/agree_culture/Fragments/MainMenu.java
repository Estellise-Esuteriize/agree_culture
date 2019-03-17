package com.capstone.agree_culture.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.capstone.agree_culture.Helper.GlobalString;
import com.capstone.agree_culture.R;
import com.capstone.agree_culture.model.User;

public class MainMenu extends Fragment {


    private EditText home_search;
    private Button home_distibutor;
    private Button home_supplier;

    private User currentUser;

    public static MainMenu newInstance() {
        /*
            Bundle args = new Bundle();

            MainMenu fragment = new MainMenu();
            fragment.setArguments(args);

            return fragment;
        */
        return new MainMenu();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_main, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        home_search = (EditText) view.findViewById(R.id.home_search_text);

        home_distibutor = (Button) view.findViewById(R.id.home_button_distributor);

        home_supplier = (Button) view.findViewById(R.id.home_button_seller);


        if(currentUser != null){
            initializedHome(currentUser);
        }

    }

    public void initializedHome(User currentUser){

        if(this.currentUser == null){
            this.currentUser = currentUser;
        }

        home_search.setVisibility(View.VISIBLE);
        home_distibutor.setVisibility(View.VISIBLE);
        home_supplier.setVisibility(View.VISIBLE);

        if(currentUser.getRole() == GlobalString.DISTRIBUTOR || currentUser.getRole() == GlobalString.CUSTOMER){
            home_distibutor.setEnabled(false);
        }
        else if(currentUser.getRole() == GlobalString.SUPPLIER){
            home_supplier.setEnabled(false);
        }
    }

}
