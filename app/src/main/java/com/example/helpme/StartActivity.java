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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

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
    public static StartActivity mainActivity;
    public static Boolean isVisible = false;
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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
        mainActivity = this;
        registerWithNotificationHubs();
        FirebaseService.createChannelAndHandleNotifications(getApplicationContext());
        startIntents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity, notificationMessage, Toast.LENGTH_LONG).show();
                TextView helloText = (TextView) findViewById(R.id.aboutBTN);
                helloText.setText(notificationMessage);
            }
        });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                Toast.makeText(this,"This device is not supported by Google Play Services.",Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
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
        //aboutBtn=(Button)findViewById(R.id.aboutBTN);
        places = new PlaceFactory();
    }

    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
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
//        aboutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(StartActivity.this,About.class);
//                startActivity(intent);
//            }
//        });
    }

    private void initFirebase(){
        mFireBaseAuth= FirebaseAuth.getInstance();
        mDatabaseReferenceAuth = FirebaseDatabase.getInstance().getReference("cellPhone");
        mDatabaseReferencePlaces = FirebaseDatabase.getInstance().getReference("places");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void updateDataBase(){
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
        isVisible = true;
        ///---------------------active this after we finish----- this method check if user exists----------
        userExists();
//        addPlacesToDatabase();

    }
}
