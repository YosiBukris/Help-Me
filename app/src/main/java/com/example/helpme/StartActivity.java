package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
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

public class StartActivity extends AppCompatActivity {
    private Button customerBtn;
    private Button workerBtn;
    private Button aboutBtn;
    public static PlaceFactory places;
    private FirebaseAuth mFireBaseAuth;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferencePlaces;
    private boolean userExists=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initPlaces();
        startIntents();


    }

    private void initPlaces() {
        places = new PlaceFactory();
        places.addPlace(new Place("Renuar",12345));
        places.addPlace(new Place("Castro",234234));
        places.addPlace(new Place("Zara",17556));
        places.addPlace(new Place("Bershka",78978));
        places.addPlace(new Place("Billabong",8954));
        places.addPlace(new Place("Adidas",99654));
        places.addPlace(new Place("Nike",8654));
        places.sortPlaces();
    }

    private void initViews(){
        customerBtn=(Button)findViewById(R.id.customerBTN);
        workerBtn=(Button)findViewById(R.id.workerBTN);
        aboutBtn=(Button)findViewById(R.id.aboutBTN);

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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("cellPhone");
        mDatabaseReferencePlaces= FirebaseDatabase.getInstance().getReference("places");
    }
//    private void addPlacesToDatabase(){
//        mDatabaseReferencePlaces.
//    }

    private void userExists(){
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    for (DataSnapshot mDataSnapshot1 : dataSnapshot.getChildren()) {
                        if (mDataSnapshot1.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            userExists = true;
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
        initFirebase();
        ///---------------------active this after we finish----- this method check if user exists----------
        userExists();
//        addPlacesToDatabase();

    }
}
