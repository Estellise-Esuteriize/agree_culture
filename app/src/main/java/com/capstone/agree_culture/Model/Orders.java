package com.capstone.agree_culture.Model;

public class Orders {


    private String collectionId;

    /*
    private String productName;
    private int productQuantity;
    private double productPrice;
    */
    private String productUidRef;
    private String productOwnerUidRef;

    private String productBuyerUidRef;
    private String status;

    private String createdAt;
    private String updatedAt;

    public static final String PENDING = "Pending";
    public static final String ORDER = "Ordered";
    public static final String DELIVERY = "Delivery";
    public static final String COMPLETED = "Completed";
    public static final String CANCELED = "Canceled";


    public Orders(){

    }

    public Orders(/*String productName, int productQuantity, double productPrice, */String productUidRef, String productOwnerUidRef, String productBuyerUidRef){
        /*
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        */
        this.productUidRef = productUidRef;
        this.productOwnerUidRef = productOwnerUidRef;
        this.productBuyerUidRef = productBuyerUidRef;
        this.status = PENDING;
    }

    /*
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
    */


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

    public String getProductOwnerUidRef() {
        return productOwnerUidRef;
    }

    public void setProductOwnerUidRef(String productOwnerUidRef) {
        this.productOwnerUidRef = productOwnerUidRef;
    }

    public String getProductBuyerUidRef() {
        return productBuyerUidRef;
    }

    public void setProductBuyerUidRef(String productBuyerUidRef) {
        this.productBuyerUidRef = productBuyerUidRef;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
