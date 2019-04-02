package com.capstone.agree_culture.Model;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Messages {


    private String collectionId;

    private String userUidRef;

    private String toUserUidRef;
    private String toUserNumber;

    private String createdAt;
    private String updatedAt;


    public Messages(){

    }

    public Messages(String userUidRef, String toUserUidRef, String toUserNumber){
        this.userUidRef = userUidRef;
        this.toUserUidRef = toUserUidRef;
        this.toUserNumber = toUserNumber;
        createdAt = FieldValue.serverTimestamp().toString();
        updatedAt = FieldValue.serverTimestamp().toString();
    }


    public String getToUserUidRef() {
        return toUserUidRef;
    }

    public void setToUserUidRef(String toUserUidRef) {
        this.toUserUidRef = toUserUidRef;
    }

    public String getToUserNumber() {
        return toUserNumber;
    }

    public void setToUserNumber(String toUserNumber) {
        this.toUserNumber = toUserNumber;
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

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getUserUidRef() {
        return userUidRef;
    }

    public void setUserUidRef(String userUidRef) {
        this.userUidRef = userUidRef;
    }

}
