package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import static java.lang.Thread.sleep;

public class StartActivity extends AppCompatActivity {
    private final String CELL_PHONE = "cellPhone";
    public final String PLACES = "places";
    public static final String CHANNEL_ID = "simplified_coding";
    private static final String CHANNEL_NAME = "Simplified Coding";
    private static final String CHANNEL_DESC = "Simplified Coding Notifications";
    private Button customerBtn;
    private Button workerBtn;
    public static FirebaseAuth mFireBaseAuth;
    public static DatabaseReference mDatabaseReferenceAuth;
    public static DatabaseReference mDatabaseReferencePlaces;
    public static FirebaseStorage storage;
    public static StorageReference storageRef;
    private boolean userExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirebase();
        initViews();
        initNotification();
        startIntents();
    }

    private void initViews() {
        customerBtn = (Button) findViewById(R.id.customerBTN);
        workerBtn = (Button) findViewById(R.id.workerBTN);
    }

    private void initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void startIntents() {
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
    }

    private void initFirebase() {
        mFireBaseAuth = FirebaseAuth.getInstance();
        mDatabaseReferenceAuth = FirebaseDatabase.getInstance().getReference(CELL_PHONE);
        mDatabaseReferencePlaces = FirebaseDatabase.getInstance().getReference(PLACES);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }


    private void userExists() {

        if (!ListPlacesActivity.userInTheSystem) {
            Intent intent = new Intent(StartActivity.this, CustomerLogIn.class);
            startActivity(intent);
        } else{
            Toast.makeText(getApplicationContext(),
                    "User exists...login succeeded", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(StartActivity.this, ListPlacesActivity.class);
            startActivity(intent);
        }
    }



}
