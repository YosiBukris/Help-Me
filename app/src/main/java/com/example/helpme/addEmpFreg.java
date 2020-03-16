package com.example.helpme;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.ValueEventListener;

public class addEmpFreg extends Fragment {
    public final String PLACES="places";
    public final String EMPLOYEES="employees";
    private final String DELETE = "delete";
    private final String ADD = "add";
    private String name;
    private String mail;
    private String phone;
    private TextView title;
    private EditText nameEditText;
    private EditText mailEditText;
    private EditText phoneEditText;
    private Button addEmpBTN;
    private Button delEmpBTN;
    private Button delClick;
    private Button addClick;
    private Employee newEmp;
    private WorkPlace place;
    private String action;
    private ManagerPage thisManagerPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_emp_freg, container, false);
        title = v.findViewById(R.id.TitleFregLog);
        nameEditText = v.findViewById(R.id.nameNewEmp);
        mailEditText = v.findViewById(R.id.emailNewEmp);
        phoneEditText = v.findViewById(R.id.phoneNewEmp);
        addEmpBTN = v.findViewById(R.id.commitAddEmp);
        delEmpBTN = v.findViewById(R.id.commitDelEmp);
        addClick = v.findViewById(R.id.AddBTNFreg);
        delClick = v.findViewById(R.id.DelBTNFreg);
        initViews();
        initClicks();
        return v;
    }

    private void initViews() {
        nameEditText.setVisibility(View.INVISIBLE);
        mailEditText.setVisibility((View.INVISIBLE));
        phoneEditText.setVisibility(View.INVISIBLE);
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
                        name = nameEditText.getText().toString();
                        mail = mailEditText.getText().toString();
                        phone = phoneEditText.getText().toString();
                        if (mail.isEmpty()) {
                            mailEditText.setError("Email required");
                            mailEditText.requestFocus();
                            return;
                        }
                        if (name.isEmpty()) {
                            nameEditText.setError("Name required");
                            nameEditText.requestFocus();
                            return;
                        }
                        newEmp = new Employee(name, mail, phone);
                        if (newEmp != null) {
                            place.addWorker(new Employee(name, mail, phone));
                            StartActivity.mFireBaseAuth.createUserWithEmailAndPassword(mail, place.getCode())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        thisManagerPage.successAdd();
                                    }else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(thisManagerPage, "User exist", Toast.LENGTH_LONG).show();
                                        } else
                                            Toast.makeText(thisManagerPage, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        nameEditText.setText("");
                        mailEditText.setText("");
                        phoneEditText.setText("");
                    }
                }
            }
        });
        delEmpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = mailEditText.getText().toString();
                if (mail.isEmpty()) {
                    mailEditText.setError("Email required");
                    mailEditText.requestFocus();
                    return;
                }
                if (place.getEmployees() != null) {
                    for (Employee e : place.getEmployees()) {
                        if (e != null) {
                            if (e.getId().equals(mail)) {
                                StartActivity.mFireBaseAuth.signInWithEmailAndPassword(mail, thisManagerPage.place.getCode())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    final FirebaseUser user = StartActivity.mFireBaseAuth.getCurrentUser();
                                                    final DatabaseReference dbUsers = FirebaseDatabase.getInstance()
                                                            .getReference(PLACES).child(thisManagerPage.place.getName()).child(EMPLOYEES);
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
                                                                        thisManagerPage.successDell();
                                                                    }
                                                                }
                                                            });
                                                } else
                                                    Toast.makeText(thisManagerPage, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                mailEditText.setText("");
                                return;
                            }

                        }
                    }
                    thisManagerPage.faileDell();
                }
                thisManagerPage.listOfEmployeesIsEmpty();
            }
        });
    }

    private void checkClick(){
        if (this.action.equals(ADD)){
            nameEditText.setVisibility(View.VISIBLE);
            mailEditText.setVisibility((View.VISIBLE));
            phoneEditText.setVisibility(View.VISIBLE);
            addEmpBTN.setVisibility(View.VISIBLE);
            delEmpBTN.setVisibility(View.INVISIBLE);
            nameEditText.setText("");
            mailEditText.setText("");
            phoneEditText.setText("");
            title.setText("Add Employee");
        }
        if(this.action.equals(DELETE)){
            nameEditText.setVisibility(View.INVISIBLE);
            mailEditText.setVisibility((View.VISIBLE));
            phoneEditText.setVisibility(View.INVISIBLE);
            addEmpBTN.setVisibility(View.INVISIBLE);
            delEmpBTN.setVisibility(View.VISIBLE);
            nameEditText.setText("");
            mailEditText.setText("");
            phoneEditText.setText("");
            title.setText("Delete Employee");
        }
    }

    private boolean checkValidation() {
        if (phoneEditText.getText().toString().length() < 10){
            thisManagerPage.failedAdd();
            return false;
        }
        if(place.getEmployees()!=null) {
            for (Employee e : this.place.getEmployees()) {
                if (e.getId().equals(mailEditText.getText().toString())) {
                    thisManagerPage.failedAddId();
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

    public void setThisManagerPage(ManagerPage thisPage) {
        this.thisManagerPage = thisPage;
    }

}