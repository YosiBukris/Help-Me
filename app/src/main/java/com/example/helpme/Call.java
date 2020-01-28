package com.example.helpme;

import android.graphics.Picture;
import android.widget.ImageView;

public class Call {
    public String customerNumber;
    public ImageView pic;
    public String workerCall;
    public boolean done = false;
    //massage
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


