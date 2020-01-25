package com.example.helpme;

import android.graphics.Picture;
import android.widget.ImageView;

public class Call {
    private String customerNumber;
    private ImageView pic;
    private String workerCall;
    private boolean done = false;

    public Call(String customerNumber, ImageView pic) {
        this.customerNumber = customerNumber;
        this.pic = pic;
    }

    public void Response(String workerName){
        this.workerCall = workerName;
        //now raise pop-up with worker name on customer-main activity
        this.done = true;
    }
}


