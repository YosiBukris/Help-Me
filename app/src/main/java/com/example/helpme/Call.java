package com.example.helpme;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class Call implements Serializable {
    private String customerMail;
    private String pic;
    private String workerCall;
    private String token;
    private String callUid;

    //massage
    public Call(String customerMail, String pic,String token,String callUid) {
        this.customerMail = customerMail;
        this.pic = pic;
        this.token=token;
        this.callUid=callUid;
    }

    public String getCustomerMail() {
        return customerMail;
    }

    public void setCustomerMail(String customerMail) {
        this.customerMail = customerMail;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCallUid() {
        return callUid;
    }

    public void setCallUid(String callUid) {
        this.callUid = callUid;
    }


    @NonNull
    @Override
    public String toString() {
        return customerMail;
    }
}


