package com.capstone.agree_culture.Model;

public class PlacesSearch {


    private String documentId;

    private double latitude;
    private double longtitude;


    public PlacesSearch(){

    }

    public PlacesSearch(double latitude, double longtitude){
        this.latitude = latitude;
        this.longtitude = longtitude;
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

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
