package com.example.helpme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class WorkPlace implements Comparable {
    private final String UPLOADS = "Uploads";
    private final String WORK_PLACE_CALLS = "workPlaceCalls";
    private String name;
    private String code;
    private int numOfWorkers = 0;
    private ArrayList<Call> uploads;
    private Employee manager;
    private Map<String, Employee> employees;


    public WorkPlace(String name, String code, int maxWorkers, Employee manager, ArrayList<Call> uploads) {
        this.code = code;
        this.name = name;
        this.uploads = uploads;
        this.manager = manager;
        employees = new HashMap<>();
    }

    public WorkPlace(){}


    public void addWorker(Employee e) {
        if (employees == null)
            employees = new HashMap<>();
        employees.put(e.getId(), e);
        numOfWorkers++;
    }

    public void removeWorker(Employee e) {
        employees.remove(e.getId());
        numOfWorkers--;
    }

    public void addCall(final String customerMail, ImageView pic, Uri imageUri, final String token, final String Uid) {
        final StorageReference filePath = StartActivity.storageRef.child(this.name).child(WORK_PLACE_CALLS).child(token);
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("hodd", "onSuccess: " + uri);
                        StartActivity.mDatabaseReferencePlaces.child(name).child(UPLOADS).child(token).setValue(new Call(customerMail, String.valueOf(uri), token, Uid))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Wrokplace", "onComplete: isSeccessful");
                                        CustomerMain.pbSendBtn.setVisibility(View.INVISIBLE);
                                        CustomerMain.cameraAgain.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });
            }
        });
    }



    public String getName() {
        return name;
    }

    public Collection<Employee> getEmployees() {
        if(employees!=null)
            return employees.values();
        else
            return null;
    }

    public void setEmployees(Map<String, Employee> employees) {
        this.employees = employees;
    }


    public Employee getManager() {
        return manager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public int getNumOfWorkers() {
        return numOfWorkers;
    }

    public ArrayList<Call> getWorkPlaceCalls() {
        return uploads;
    }

    public void responseCall(Employee worker){
        //uploads.g().Response(worker.getName());
    }

    @Override
    public int compareTo(Object place) {
        WorkPlace other = (WorkPlace) place;
        if (this.name.charAt(0) > other.name.charAt(0))
            return 1;
        else
            return -1;
    }

}
