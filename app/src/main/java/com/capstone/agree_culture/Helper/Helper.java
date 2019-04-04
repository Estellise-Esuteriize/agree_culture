package com.capstone.agree_culture.Helper;

import com.capstone.agree_culture.Model.Messages;
import com.capstone.agree_culture.Model.Orders;
import com.capstone.agree_culture.Model.User;
import com.capstone.agree_culture.R;

import java.util.ArrayList;
import java.util.List;

public class Helper {


    public static Boolean isFirestoreSettingsInitialize = false;

    public static User currentUser = null;

    public static List<Orders> newOrder = null;

    public static List<Messages> newMessage = null;


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

    public static void addNewMessages(Messages messages){

        if(newMessage == null){
            newMessage = new ArrayList<>();
        }

        newMessage.add(messages);
    }


    public static void addNewOrders(Orders order){

        if(newOrder == null){
            newOrder = new ArrayList<>();
        }

        newOrder.add(order);

    }


    public static int orderStatusColors(String status){

        if(status.equals(Orders.PENDING)){
            return R.color.order_pending;
        }
        else if(status.equals(Orders.ORDER)){
            return R.color.order_ordered;
        }
        else if(status.equals(Orders.DELIVERY)){
            return R.color.order_delivery;
        }
        else if(status.equals(Orders.COMPLETED)){
            return R.color.order_success;
        }
        else if(status.equals(Orders.CANCELED)){
            return R.color.order_canceled;
        }

        return R.color.order_canceled;

    }


}
