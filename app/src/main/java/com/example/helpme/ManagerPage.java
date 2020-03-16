package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.BoringLayout;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ManagerPage extends AppCompatActivity {
    private final String WORK_PLACE="WorkPlaceName";
    private final String EMPLOYEE="nameOfEmployee";
    private TextView hello;
    private Button updateEmp;
    public WorkPlace place;
    private String intentName;
    private String intentPlace;
    private addEmpFreg addFreg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_page);
        initView();
        addFreg.setPlace(place);
        addFreg.setThisManagerPage(this);
        initClicks();
    }

    public void successAdd(){
        Toast.makeText(getApplicationContext(),
                "Employee added to " + place.getName(), Toast.LENGTH_LONG).show();
    }

    public void successDell(){
        Toast.makeText(getApplicationContext(),
                "Employee deleted from " + place.getName(), Toast.LENGTH_LONG).show();
    }

    public void faileDell(){
        Toast.makeText(getApplicationContext(),
                "Employee not exist in " + place.getName(), Toast.LENGTH_LONG).show();
    }

    public void failedAdd(){
        Toast.makeText(getApplicationContext(),
                "Wrong values", Toast.LENGTH_LONG).show();
    }

    public void failedAddId(){
        Toast.makeText(getApplicationContext(),
                "ID already exists", Toast.LENGTH_LONG).show();
    }

    public void failedAddPhone(){
        Toast.makeText(getApplicationContext(),
                "Phone Number already exists", Toast.LENGTH_LONG).show();
    }

    public void listOfEmployeesIsEmpty(){
        Toast.makeText(getApplicationContext(),
                "List of employees is empty", Toast.LENGTH_LONG).show();
    }

    private void initClicks() {
        updateEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.addEmpFreg,addFreg);
                transaction.commit();
            }
        });
    }

    private void initView() {
        this.hello = (TextView) findViewById(R.id.name_man_TV);
        this.updateEmp = (Button) findViewById(R.id.UpdateEmployees);
        intentName=getIntent().getStringExtra(EMPLOYEE);
        intentPlace=getIntent().getStringExtra(WORK_PLACE);
        this.hello.setText("Hello " + intentName);
        for(WorkPlace p : WorkerLogIn.places_worker.getArrayList()){
            if (p.getName().equals(intentPlace))
                this.place = p;
        }
        addFreg = new addEmpFreg();
    }
}
