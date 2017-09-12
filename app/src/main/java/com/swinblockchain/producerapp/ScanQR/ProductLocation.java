package com.swinblockchain.producerapp.ScanQR;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * ProductLocation holds information about each location a product has been through
 */
public class ProductLocation implements Serializable {

    String productName;
    String batchID;
    String date;
    String regBy;
    String location;

    public ProductLocation (String productName, String batchID, String date, String regBy, String location) {
        this.productName = productName;
        this.batchID = batchID;
        this.date = date;
        this.regBy = regBy;
        this.location = location;
    }

    // Getters & Setters

    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRegBy() {
        return regBy;
    }

    public void setRegBy(String regBy) {
        this.regBy = regBy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProductName() {

        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


}
