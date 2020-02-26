package com.example.helpme;

import android.graphics.Picture;
import android.net.Uri;
import android.widget.ImageView;

import java.util.ArrayList;

public class Call {
    public String customerNumber;
    public String pic;
    public String workerCall;
    public boolean done = false;
    //massage
    public Call(String customerNumber, String pic) {
        this.customerNumber = customerNumber;
        this.pic = pic;
    }

    public void Response(String workerName){
        this.workerCall = workerName;
        //now raise pop-up with worker name on customer-main activity
        this.done = true;
    }

}


