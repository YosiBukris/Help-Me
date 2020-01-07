package com.example.helpme;

import android.graphics.Picture;

import java.util.Queue;

public class WorkPlace {
    private String name;
    private int code;
    private int numOfWorkers = 0;
    private Queue<Call> workPlaceCalls;

    public WorkPlace(int code, String name, int maxWorkers) {
        this.code = code;
        this.name = name;
    }

    public void addWorker(){
        numOfWorkers++;
    }

    public void removeWorker(){
        numOfWorkers--;
    }

    public void addCall(String customerName, Picture pic){
        workPlaceCalls.add(new Call(customerName,pic));
    }

    public void responseCall(String workerName){
        workPlaceCalls.poll().Response(workerName);
    }

}
