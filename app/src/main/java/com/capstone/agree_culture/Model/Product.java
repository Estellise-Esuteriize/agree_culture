package com.capstone.agree_culture.Model;

import java.io.Serializable;

public class Product implements Serializable {


    private String collection_id;

    private String user_id;
    private String product_name;
    private Double product_price;
    private Integer product_quantity;
    private Integer product_minimum;
    private String product_status;
    private String user_product_type;

    private User user;

    public static final String USER_PRODUCT_TYPE_DISTRIBUTOR = "Distributor";
    public static final String USER_PRODUCT_TYPE_SUPPLIER = "Supplier";

    public static final String PRODUCT_STATUS_ENABLE = "enabled";
    public static final String PRODUCT_STATUS_DISABLED = "disabled";

    public Product(){

    }

    public Product(String user_id, String product_name, Double product_price, Integer product_quantity, Integer product_minimum, String user_product_type){
        this.user_id = user_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.product_minimum = product_minimum;
        this.user_product_type = user_product_type;
        this.product_status = PRODUCT_STATUS_ENABLE;
    }

    public static boolean productStatus(String status){

        if(status.equals(PRODUCT_STATUS_ENABLE)){
            return true;
        }
        else if(status.equals(PRODUCT_STATUS_DISABLED)){
            return false;
        }

        return false;
    }



    public String toString(){
        return "Name : " + product_name + " , " + "Price : " + product_price;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Double product_price) {
        this.product_price = product_price;
    }

    public Integer getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(Integer product_quantity) {
        this.product_quantity = product_quantity;
    }

    public Integer getProduct_minimum() {
        return product_minimum;
    }

    public void setProduct_minimum(Integer product_minimum) {
        this.product_minimum = product_minimum;
    }

    public String getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(String collection_id) {
        this.collection_id = collection_id;
    }

    public String getUser_product_type() {
        return user_product_type;
    }

    public void setUser_product_type(String user_product_type) {
        this.user_product_type = user_product_type;
    }

    public String getProduct_status() {
        return product_status;
    }

    public void setProduct_status(String product_status) {
        this.product_status = product_status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
