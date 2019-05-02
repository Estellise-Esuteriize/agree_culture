package com.capstone.agree_culture.Model;

public class PlacesSearch {


    private String documentId;

    private double latitude;
    private double longitude;


    public PlacesSearch(){

    }

    public PlacesSearch(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
