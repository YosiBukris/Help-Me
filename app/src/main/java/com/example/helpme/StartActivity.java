package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity {
    private Button customerBtn;
    private Button workerBtn;
    private Button aboutBtn;
    public static PlaceFactory places;
    public static FirebaseAuth mFireBaseAuth;
    public static DatabaseReference mDatabaseReferenceAuth;
    public static DatabaseReference mDatabaseReferencePlaces;
    public static FirebaseStorage storage;
    public static StorageReference storageRef;
    private boolean userExists=false;
    public interface DataStatus{
        void DataIsLoaded(ArrayList<WorkPlace> places);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initFirebase();
        initPlaces(new DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<WorkPlace> places) {
                StartActivity.places.setArrayList(places);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
        startIntents();
    }

    private void initPlaces(final DataStatus dataStatus) {
        mDatabaseReferencePlaces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<WorkPlace> temp = new ArrayList<>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    WorkPlace p = d.getValue(WorkPlace.class);
                    temp.add(p);
                }
                dataStatus.DataIsLoaded(temp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void initViews(){
        customerBtn=(Button)findViewById(R.id.customerBTN);
        workerBtn=(Button)findViewById(R.id.workerBTN);
        aboutBtn=(Button)findViewById(R.id.aboutBTN);
        places = new PlaceFactory();
    }

    private void startIntents(){
        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userExists) {
                    Intent intent = new Intent(StartActivity.this, CustomerLogIn.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "User exists...login succeeded", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(StartActivity.this, ListPlacesActivity.class);
                    startActivity(intent);
                }
            }
        });
        workerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartActivity.this,WorkerLogIn.class);
                startActivity(intent);
            }
        });
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartActivity.this,About.class);
                startActivity(intent);
            }
        });
    }

    private void initFirebase(){
        mFireBaseAuth= FirebaseAuth.getInstance();
        mDatabaseReferenceAuth = FirebaseDatabase.getInstance().getReference("cellPhone");
        mDatabaseReferencePlaces = FirebaseDatabase.getInstance().getReference("places");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void updateDataBase(){
//        WorkPlace Renuar = new WorkPlace("Renuar","12345", 10, new Employee("Yosi", "308464239", "0509808050"));
//        Renuar.addWorker(new Employee("tal","1234","0523976905"));
//        Renuar.addWorker(new Employee("hod","4567", "526327767"));
//        Renuar.addWorker(new Employee("jos","4321", "0509808050"));
//        places.addPlace(Renuar);
//        places.addPlace(new WorkPlace("Castro","23423410",10, new Employee("Yosi", "308464239", "0509808050")));
//        places.addPlace(new WorkPlace("Zara","17556",10, new Employee("Yosi", "308464239", "0509808050")));
//        places.addPlace(new WorkPlace("Bershka","78978",10, new Employee("Yosi", "308464239", "0509808050")));
//        places.addPlace(new WorkPlace("Billabong","8954",10, new Employee("Yosi", "308464239", "0509808050")));
//        places.addPlace(new WorkPlace("Adidas","99654",10, new Employee("Yosi", "308464239", "0509808050")));
//        places.addPlace(new WorkPlace("Nike","8654",10, new Employee("Yosi", "308464239", "0509808050")));
//        places.sortPlaces();
//        mDatabaseReferencePlaces.setValue("Places");
        for(WorkPlace p : places.getArrayList()){
            mDatabaseReferencePlaces.child(p.getName()).setValue(p);
        }
    }

    private void userExists(){
        mDatabaseReferenceAuth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    for (DataSnapshot mDataSnapshot1 : dataSnapshot.getChildren()) {
                        if (mDataSnapshot1.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            userExists = true;
                            CustomerLogIn.completeNum = StartActivity.mFireBaseAuth.getCurrentUser().getPhoneNumber();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ///---------------------active this after we finish----- this method check if user exists----------
        userExists();
//        addPlacesToDatabase();

    }
}
