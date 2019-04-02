package com.capstone.agree_culture.Helper;

import com.capstone.agree_culture.Model.Messages;
import com.capstone.agree_culture.Model.Orders;
import com.capstone.agree_culture.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Helper {


    public static Boolean isFirestoreSettingsInitialize = false;

    public static User currentUser = null;

    public static List<Orders> newOrders;

    public static List<Messages> newMessage;


    public static String userProductType(String user_product_type){

        if(user_product_type.equals(GlobalString.CUSTOMER)){
            return GlobalString.DISTRIBUTOR;
        }
        else if(user_product_type.equals(GlobalString.DISTRIBUTOR)){
            return GlobalString.SUPPLIER;
        }
        else if(user_product_type.equals(GlobalString.SUPPLIER)){
            return GlobalString.DISTRIBUTOR;
        }

        return null;

    }



}
