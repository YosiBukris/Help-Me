package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class StartActivity extends AppCompatActivity {
    private final String IS_CONNECTED="isConnected";
    public static final String CHANNEL_ID = "simplified_coding";
    private static final String CHANNEL_NAME = "Simplified Coding";
    private static final String CHANNEL_DESC = "Simplified Coding Notifications";
    private Button customerBtn;
    private Button workerBtn;
    private Button aboutBtn;
    public static PlaceFactory places;
    private ProgressBar progressBar;
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
        initFirebase();
        initViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel  = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        startInitPlaces();
        startIntents();
    }

    private void startInitPlaces(){
        initPlaces(new StartActivity.DataStatus() {
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
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        customerBtn=(Button)findViewById(R.id.customerBTN);
        workerBtn=(Button)findViewById(R.id.workerBTN);
        aboutBtn=(Button)findViewById(R.id.aboutBTN);
        places = new PlaceFactory();
    }

    private void startIntents(){
        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userExists();
            }
        });
        workerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, WorkerLogIn.class);
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
        Log.d("im here", "initFirebase: ");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }


    private void userExists(){
        mDatabaseReferenceAuth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (CustomerLogIn.completeNum != null) {
                    for (DataSnapshot mDataSnapshot1 : dataSnapshot.getChildren()) {
                        if (mDataSnapshot1.getValue().toString().equals(CustomerLogIn.completeNum)) {
                            userExists = true;
                        }
                    }
                }
                if(!userExists) {
                    Intent intent = new Intent(StartActivity.this, CustomerLogIn.class);
                    startActivity(intent);
                }else if(userExists){
                    Toast.makeText(getApplicationContext(),
                            "User exists...login succeeded", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(StartActivity.this, ListPlacesActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

}
