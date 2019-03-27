package com.capstone.agree_culture.Model;

import java.util.Date;

public class Messages {


    private String collectionId;

    private String fromUserUidRef;
    private String fromUserNumber;

    private String toUserUidRef;
    private String toUserNumber;

    private Date createdAt;
    private Date updatedAt;

    Messages(){

    }

    Messages(String fromUserUidRef, String fromUserNumber, String toUserUidRef, String toUserNumber, Date createdAt, Date updatedAt){

        this.fromUserUidRef = fromUserUidRef;
        this.fromUserNumber = fromUserNumber;
        this.toUserUidRef = toUserUidRef;
        this.toUserNumber = toUserNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public String getFromUserUidRef() {
        return fromUserUidRef;
    }

    public void setFromUserUidRef(String fromUserUidRef) {
        this.fromUserUidRef = fromUserUidRef;
    }

    public String getFromUserNumber() {
        return fromUserNumber;
    }

    public void setFromUserNumber(String fromUserNumber) {
        this.fromUserNumber = fromUserNumber;
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

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
