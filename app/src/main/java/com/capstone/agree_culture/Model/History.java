package com.capstone.agree_culture.Model;

import java.util.Date;

public class History {

    private String collectionId;

    private String ownerUidRef;
    private String buyerUidRef;

    private String productName;
    private String productQuantity;
    private String productPrice;

    private String status;

    private Date createdAt;
    private Date updatedAt;


    public final String ACCEPTED = "Accepted";
    public final String DECLINED = "Declined";



    History(){

    }

    History(String ownerUidRef, String buyerUidRef, String productName, String productQuantity, String productPrice, String status){
        this.ownerUidRef = ownerUidRef;
        this.buyerUidRef = buyerUidRef;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.status = status;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
