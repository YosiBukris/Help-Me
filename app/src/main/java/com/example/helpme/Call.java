package com.example.helpme;

import android.graphics.Picture;

public class Call {
    private String customerName;
    private Picture pic;
    private String workerCall;
    private boolean done = false;

    public Call(String customerName, Picture pic) {
        this.customerName = customerName;
        this.pic = pic;
    }

    public void Response(String workerName){
        this.workerCall = workerName;
        //now raise pop-up with worker name on customer-main activity
        this.done = true;
    }
}


