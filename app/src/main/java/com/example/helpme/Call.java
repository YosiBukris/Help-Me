package com.example.helpme;

import android.graphics.Picture;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Call implements Serializable {
    private String customerNumber;
    private String pic;
    private String workerCall;
    private String token;

    //massage
    public Call(String customerNumber, String pic,String token,String callUid) {
        this.customerNumber = customerNumber;
        this.pic = pic;
        this.token=token;
        this.callUid=callUid;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getWorkerCall() {
        return workerCall;
    }

    public void setWorkerCall(String workerCall) {
        this.workerCall = workerCall;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCallUid(String callUid) {
        this.callUid = callUid;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    private String callUid;
    private boolean done = false;


    public void Response(String workerName){
        this.workerCall = workerName;
        //now raise pop-up with worker name on customer-main activity
        this.done = true;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }
    public String getToken(){
        return this.token;
    }
    public String getCallUid(){
        return this.callUid;
    }

    @NonNull
    @Override
    public String toString() {
        return customerNumber;
    }
}


