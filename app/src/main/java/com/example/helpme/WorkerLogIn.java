package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkerLogIn extends AppCompatActivity {
    private final String WORK_PLACE="WorkPlaceName";
    private final String EMPLOYEE="nameOfEmployee";
    private EditText workerId;
    private EditText placeCode;
    private Button connectEmp;
    private Button connectMan;
    private String empID;
    private String placeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_log_in);
        initViews();
        logIn();
    }

    private void initViews() {
        workerId= (EditText) findViewById(R.id.workerEditID);
        placeCode = (EditText) findViewById(R.id.workplaceCode);
        connectEmp = (Button) findViewById(R.id.workerConnectBTN);
        connectMan = (Button) findViewById(R.id.managerConnectBTN);
    }

    private void logIn() {
        connectEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empID = workerId.getText().toString();
                placeID = placeCode.getText().toString();
                for(WorkPlace p : StartActivity.places.getArrayList()){
                    if (p.getCode().equals(placeID)){
                        for (Employee e : p.getEmployees()){
                            if (e.getId().equals(empID)){
                                Toast.makeText(getApplicationContext(),
                                        "login succeeded", Toast.LENGTH_LONG).show();
                                StartActivity.mDatabaseReferencePlaces.child(p.getName()).child("employees").child(e.getId()).child("isConnected").setValue(true);
                                Intent intent = new Intent(WorkerLogIn.this, WorkerMain.class);
                                intent.putExtra(WORK_PLACE, p.getCode());
                                intent.putExtra(EMPLOYEE, e.getName());
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        });
        connectMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empID = workerId.getText().toString();
                placeID = placeCode.getText().toString();
                for (WorkPlace p : StartActivity.places.getArrayList()) {
                    if (p.getCode().equals(placeID)) {
                        if(p.getManager()!=null) {
                            if (empID.equals(p.getManager().getId())) {
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
            }
        });
    }

}
