package com.capstone.agree_culture.Model;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class Cart {


    private String collectionId;

    private String productName;
    private int productQuantity;
    private double productPrice;

    private String productUidRef;
    private String ownerUidRef;

    private String buyerUidRef;
    private String buyerStatus;

    private Date createdAt;
    private Date updatedAt;

    public static final String cart = "Cart";
    public static final String purchase = "Purchase";


    public Cart(){

    }

    public Cart(String productName, int productQuantity, double productPrice, String productUidRef, String ownerUidRef, String buyerUidRef){
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productUidRef = productUidRef;
        this.ownerUidRef = ownerUidRef;
        this.buyerUidRef = buyerUidRef;
        this.buyerStatus = cart;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getProductUidRef() {
        return productUidRef;
    }

    public void setProductUidRef(String productUidRef) {
        this.productUidRef = productUidRef;
    }

    public String getOwnerUidRef() {
        return ownerUidRef;
    }

    public void setOwnerUidRef(String ownerUidRef) {
        this.ownerUidRef = ownerUidRef;
    }

    public String getBuyerUidRef() {
        return buyerUidRef;
    }

    public void setBuyerUidRef(String buyerUidRef) {
        this.buyerUidRef = buyerUidRef;
    }

    public String getBuyerStatus() {
        return buyerStatus;
    }

    public void setBuyerStatus(String buyerStatus) {
        this.buyerStatus = buyerStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
