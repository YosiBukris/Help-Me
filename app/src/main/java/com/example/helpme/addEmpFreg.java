package com.example.helpme;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addEmpFreg extends Fragment {
    private final String DELETE = "delete";
    private final String ADD = "add";
    private String name;
    private String mail;
    private String phone;
    private TextView title;
    private EditText nameTXT;
    private EditText MAILTXT;
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
        MAILTXT = v.findViewById(R.id.emailNewEmp);
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
        MAILTXT.setVisibility((View.INVISIBLE));
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
                if (place != null) {
                    if (checkValidation()) {
                        name = nameTXT.getText().toString();
                        mail = MAILTXT.getText().toString();
                        phone = phoneTXT.getText().toString();
                        if (mail.isEmpty()) {
                            MAILTXT.setError("Email required");
                            MAILTXT.requestFocus();
                            return;
                        }
                        if (name.isEmpty()) {
                            nameTXT.setError("Name required");
                            nameTXT.requestFocus();
                            return;
                        }
                        newEmp = new Employee(name, mail, phone);
                        if (newEmp != null) {
                            place.addWorker(new Employee(name, mail, phone));
                            StartActivity.mFireBaseAuth.createUserWithEmailAndPassword(mail, place.getCode()+mail).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                        thisMannagerPage.successAdd();
                                    else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(thisMannagerPage, "User exist", Toast.LENGTH_LONG).show();
                                        } else
                                            Toast.makeText(thisMannagerPage, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        nameTXT.setText("");
                        MAILTXT.setText("");
                        phoneTXT.setText("");
                    }
                }
            }
        });
        delEmpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = MAILTXT.getText().toString();
                phone = phoneTXT.getText().toString();
                if (place.getEmployees() != null) {
                    for (Employee e : place.getEmployees()) {
                        if (e != null) {
                            if (e.getId().equals(mail)) {
                                StartActivity.mFireBaseAuth.signInWithEmailAndPassword(mail, thisMannagerPage.place.getCode())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    final FirebaseUser user = StartActivity.mFireBaseAuth.getCurrentUser();
                                                    final DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("places").child(thisMannagerPage.place.getName()).child("employees");
                                                    dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists()){
                                                                for(DataSnapshot dsUser : dataSnapshot.getChildren()) {
                                                                    Employee employee = dsUser.getValue(Employee.class);
                                                                    if(employee.getId().equals(user.getEmail()))
                                                                        dsUser.getRef().removeValue();
                                                                }
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                    user.delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        thisMannagerPage.successDell();
                                                                    }
                                                                }
                                                            });
                                                } else
                                                    Toast.makeText(thisMannagerPage, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
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
            MAILTXT.setVisibility((View.VISIBLE));
            phoneTXT.setVisibility(View.VISIBLE);
            addEmpBTN.setVisibility(View.VISIBLE);
            delEmpBTN.setVisibility(View.INVISIBLE);
            nameTXT.setText("");
            MAILTXT.setText("");
            phoneTXT.setText("");
            title.setText("Add Employee");
        }
        if(this.action.equals(DELETE)){
            nameTXT.setVisibility(View.INVISIBLE);
            MAILTXT.setVisibility((View.VISIBLE));
            phoneTXT.setVisibility(View.VISIBLE);
            addEmpBTN.setVisibility(View.INVISIBLE);
            delEmpBTN.setVisibility(View.VISIBLE);
            nameTXT.setText("");
            MAILTXT.setText("");
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
                if (e.getId().equals(MAILTXT.getText().toString())) {
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

}
