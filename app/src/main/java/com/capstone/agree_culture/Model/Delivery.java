package com.capstone.agree_culture.Model;

public class Delivery {


    private String documentId;

    private String buyerProductUuid;

    private String ownerProductUuid;


    private double destinationLat;
    private double destinationLong;

    private double deliveryLat;
    private double deliveryLong;


    public Delivery(){

    }


    public Delivery(String ownerProductUuid, String buyerProductUuid){
        this.ownerProductUuid = ownerProductUuid;
        this.buyerProductUuid = buyerProductUuid;
    }


    public Delivery(String buyerProductUuid, String ownerProductUuid, double destinationLat, double destinationLong, double deliveryLat, double deliveryLong) {
        this.buyerProductUuid = buyerProductUuid;
        this.ownerProductUuid = ownerProductUuid;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
        this.deliveryLat = deliveryLat;
        this.deliveryLong = deliveryLong;
    }


    public String getStringCollection(){
        return "documentId";
    }

    public String getStringBuyerProductUuid(){
        return "buyerProductUuid";
    }

    public String getStringOwnerProductUuid(){
        return "ownerProductUuid";
    }

    public String getStringDestinationLat(){
        return "destinationLat";
    }

    public String getStringDestinationLong(){
        return "destinationLong";
    }

    public String getStringDeliveryLat(){
        return "deliveryLat";
    }

    public String getStringDeliveryLong(){
        return "deliveryLong";
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBuyerProductUuid() {
        return buyerProductUuid;
    }

    public void setBuyerProductUuid(String buyerProductUuid) {
        this.buyerProductUuid = buyerProductUuid;
    }

    public String getOwnerProductUuid() {
        return ownerProductUuid;
    }

    public void setOwnerProductUuid(String ownerProductUuid) {
        this.ownerProductUuid = ownerProductUuid;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(double destinationLong) {
        this.destinationLong = destinationLong;
    }

    public double getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(double deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public double getDeliveryLong() {
        return deliveryLong;
    }

    public void setDeliveryLong(double deliveryLong) {
        this.deliveryLong = deliveryLong;
    }

}
