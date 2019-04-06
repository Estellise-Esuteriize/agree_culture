package com.capstone.agree_culture.Model;

import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;

public class Product implements Serializable {


    private String collectionId;

    private String userId;
    private String productName;
    private Double productPrice;
    private Integer productQuantity;
    private Integer productMinimum;
    private String productStatus;
    private String productPhoto;
    private String userProductType;

    private User user;

    public static final String USER_PRODUCT_TYPE_DISTRIBUTOR = "Distributor";
    public static final String USER_PRODUCT_TYPE_SUPPLIER = "Supplier";

    public static final String PRODUCT_STATUS_ENABLE = "enabled";
    public static final String PRODUCT_STATUS_DISABLED = "disabled";

    public Product(){

    }

    public Product(String userId, String productName, Double productPrice, Integer productQuantity, Integer productMinimum, String userProductType){
        this.userId = userId;
        this.productName = WordUtils.capitalizeFully(productName);
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.productMinimum = productMinimum;
        this.userProductType = userProductType;
        this.productStatus = PRODUCT_STATUS_ENABLE;
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



    //public String toString(){
    //    return "Name : " + productName + " , " + "Price : " + productPrice;
    //}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Integer getProductMinimum() {
        return productMinimum;
    }

    public void setProductMinimum(Integer productMinimum) {
        this.productMinimum = productMinimum;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getUserProductType() {
        return userProductType;
    }

    public void setUserProductType(String userProductType) {
        this.userProductType = userProductType;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(String productPhoto) {
        this.productPhoto = productPhoto;
    }
}
