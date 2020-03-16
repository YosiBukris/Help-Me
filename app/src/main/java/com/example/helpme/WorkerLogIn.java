package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WorkerLogIn extends AppCompatActivity {
    private final String WORK_PLACE="WorkPlaceName";
    private final String EMPLOYEE="nameOfEmployee";
    private EditText workerMail;
    private EditText placeCode;
    private Button connectEmp;
    private Button connectMan;
    private String empMail;
    private String placeID;
    private ProgressBar pbWorker;
    public static PlaceFactory places_worker;
    private ArrayList<WorkPlace> workPlaces;
    private boolean isFindPlace =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_log_in);
        initViews();
        initPlaces();
        logIn();
    }

    private void initViews() {
        pbWorker = (ProgressBar) findViewById(R.id.workerLogIn_pb);
        workerMail= (EditText) findViewById(R.id.workerEditMail);
        placeCode = (EditText) findViewById(R.id.workplaceCode);
        connectEmp = (Button) findViewById(R.id.workerConnectBTN);
        connectMan = (Button) findViewById(R.id.managerConnectBTN);
        places_worker = new PlaceFactory();
        workPlaces = new ArrayList<>();
        pbWorker.setVisibility(View.INVISIBLE);
    }

    private void initPlaces() {
        FirebaseDatabase.getInstance().getReference("places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot d : dataSnapshot.getChildren()){
                    WorkPlace p = d.getValue(WorkPlace.class);
                    workPlaces.add(p);
                }
                places_worker.setArrayList(workPlaces);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void logIn() {
        connectEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbWorker.setVisibility(View.VISIBLE);
                empMail = workerMail.getText().toString().trim();
                placeID = placeCode.getText().toString().trim();
                if (!empMail.equals("") && !placeID.equals("")) {
                    StartActivity.mFireBaseAuth.signInWithEmailAndPassword(empMail, placeID)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(WorkerLogIn.this, WorkerMain.class);
                                        intent.putExtra(WORK_PLACE, placeID);
                                        intent.putExtra(EMPLOYEE, empMail);
                                        startActivity(intent);
                                    } else
                                        Toast.makeText(WorkerLogIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else if (empMail.equals("")) {
                    workerMail.setError("Invalid email");
                    workerMail.requestFocus();
                }else if(placeID.equals("")){
                    placeCode.setError("Invalid workplace code");
                    placeCode.requestFocus();
                }
                pbWorker.setVisibility(View.INVISIBLE);
            }
        });

        connectMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empMail = workerMail.getText().toString();
                placeID = placeCode.getText().toString();
                if (!empMail.equals("") && !placeID.equals("")) {
                    for (WorkPlace p : places_worker.getArrayList()) {
                        if (p.getCode().equals(placeID)) {
                            isFindPlace=true;
                            if (p.getManager() != null) {
                                if (empMail.equals(p.getManager().getId())) {
                                    Toast.makeText(getApplicationContext(),
                                            "login succeeded", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(WorkerLogIn.this, ManagerPage.class);
                                    intent.putExtra(WORK_PLACE, p.getName());
                                    intent.putExtra(EMPLOYEE, p.getManager().getName());
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                    if(!isFindPlace){
                        Toast.makeText(getApplicationContext(),
                                "Email or workplace code is wrong", Toast.LENGTH_LONG).show();
                    }
                }
                if (empMail.equals("")) {
                    workerMail.setError("Invalid email");
                    workerMail.requestFocus();
                }
                if(placeID.equals("")){
                    placeCode.setError("Invalid workplace code");
                    placeCode.requestFocus();
                }
            }
        });
    }
}
