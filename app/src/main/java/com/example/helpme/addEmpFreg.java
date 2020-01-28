package com.example.helpme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addEmpFreg extends Fragment {
    private final String DELETE = "delete";
    private final String ADD = "add";
    private String name;
    private String id;
    private String phone;
    private TextView title;
    private EditText nameTXT;
    private EditText IDTXT;
    private EditText phoneTXT;
    private Button addEmpBTN;
    private Button delEmpBTN;
    private Button delClick;
    private Button addClick;
    private Employee newEmp;
    private WorkPlace place;
    private String action;
    private ManagerPage thisMannagerPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_emp_freg, container, false);
        title = v.findViewById(R.id.TitleFregLog);
        nameTXT = v.findViewById(R.id.nameNewEmp);
        IDTXT = v.findViewById(R.id.idNewEmp);
        phoneTXT = v.findViewById(R.id.phoneNewEmp);
        addEmpBTN = v.findViewById(R.id.commitAddEmp);
        delEmpBTN = v.findViewById(R.id.commitDelEmp);
        addClick = v.findViewById(R.id.AddBTNFreg);
        delClick = v.findViewById(R.id.DelBTNFreg);
        initViews();
        initClicks();
        return v;
    }

    private void initViews() {
        nameTXT.setVisibility(View.INVISIBLE);
        IDTXT.setVisibility((View.INVISIBLE));
        phoneTXT.setVisibility(View.INVISIBLE);
        addEmpBTN.setVisibility(View.INVISIBLE);
        delEmpBTN.setVisibility(View.INVISIBLE);
    }

    private void initClicks() {
        addClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ADD;
                checkClick();
            }
        });
        delClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = DELETE;
                checkClick();
            }
        });
        addEmpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    name = nameTXT.getText().toString();
                    id = IDTXT.getText().toString();
                    phone = phoneTXT.getText().toString();
                    newEmp = new Employee(name, id, phone);
                    if (newEmp != null) {
                        StartActivity.mDatabaseReferencePlaces.child(place.getName()).child("employees").child(id).setValue(newEmp);
                        StartActivity.mDatabaseReferencePlaces.child(place.getName()).child("employees").child(id).child("isConnected").setValue(false);
                        StartActivity.mDatabaseReferencePlaces.child(place.getName()).child("numOfWorkers").setValue(place.getNumOfWorkers() + 1);
                        thisMannagerPage.successAdd();
                    }
                    nameTXT.setText("");
                    IDTXT.setText("");
                    phoneTXT.setText("");
                }
            }
        });
        delEmpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = IDTXT.getText().toString();
                phone = phoneTXT.getText().toString();
                if (place.getEmployees() != null) {
                    for (Employee e : place.getEmployees()) {
                        if (e != null) {
                            if (e.getId().equals(id)) {
                                StartActivity.mDatabaseReferencePlaces.child(place.getName()).child("employees").child(id).removeValue();
                                StartActivity.mDatabaseReferencePlaces.child(place.getName()).child("numOfWorkers").setValue(place.getNumOfWorkers() - 1);
                                thisMannagerPage.successDell();
                                return;
                            }
                        }
                    }
                }
                thisMannagerPage.faileDell();
            }
        });
    }

    private void checkClick(){
        if (this.action.equals(ADD)){
            nameTXT.setVisibility(View.VISIBLE);
            IDTXT.setVisibility((View.VISIBLE));
            phoneTXT.setVisibility(View.VISIBLE);
            addEmpBTN.setVisibility(View.VISIBLE);
            delEmpBTN.setVisibility(View.INVISIBLE);
            nameTXT.setText("");
            IDTXT.setText("");
            phoneTXT.setText("");
            title.setText("Add Employee");
        }
        if(this.action.equals(DELETE)){
            nameTXT.setVisibility(View.INVISIBLE);
            IDTXT.setVisibility((View.VISIBLE));
            phoneTXT.setVisibility(View.VISIBLE);
            addEmpBTN.setVisibility(View.INVISIBLE);
            delEmpBTN.setVisibility(View.VISIBLE);
            nameTXT.setText("");
            IDTXT.setText("");
            phoneTXT.setText("");
            title.setText("Delete Employee");
        }
    }

    private boolean checkValidation() {
        if (phoneTXT.getText().toString().length() < 10){
            thisMannagerPage.failedAdd();
            return false;
        }
        if(place.getEmployees()!=null) {
            for (Employee e : this.place.getEmployees()) {
                if (e.getId().equals(IDTXT.getText().toString())) {
                    thisMannagerPage.failedAddId();
                    return false;
                }
                if (e.getPhone().equals("+972" + phoneTXT.getText().toString().substring(1))) {
                    thisMannagerPage.failedAddPhone();
                    return false;
                }
            }
        }
        return true;
    }

    public void setPlace(WorkPlace place) {
        this.place = place;
    }

    public Employee getNewEmp() {
        return newEmp;
    }

    public void setThisMannagerPage(ManagerPage thisPage) {
        this.thisMannagerPage = thisPage;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
