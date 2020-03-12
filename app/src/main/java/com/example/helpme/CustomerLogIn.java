package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class CustomerLogIn extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Button logIn;
    private ProgressBar pb;
    private EditText cellPhoneNumberExists;
    private Spinner spinnerExists;
    private boolean userExists = false;
    private TextView createUserTextView;
    public static String completeNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_log_in);
        initViews();
        initSpinner();
        logIn();
        createUser();
    }

    private void initViews() {
        spinnerExists = (Spinner) findViewById(R.id.spinner_exists);
        logIn = findViewById(R.id.login_btn);
        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setVisibility(View.INVISIBLE);
        cellPhoneNumberExists = (EditText) findViewById(R.id.phone_num_exists);
        createUserTextView = (TextView) findViewById(R.id.create_user);
    }

    private void initSpinner() {
        spinnerExists.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
    }

    private void createCellPhoneNumber() {
        String code = CountryData.countryAreaCodes[spinnerExists.getSelectedItemPosition()];
        String number = cellPhoneNumberExists.getText().toString().trim();
        if (number.isEmpty() || number.length() < 10) {
            cellPhoneNumberExists.setError("Valid number is required");
            cellPhoneNumberExists.requestFocus();
            return;
        }

        if (number.charAt(0) == '0') {
            number = number.substring(1);
        }
        completeNum = "+" + code + number;
    }

    private void logIn() {
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCellPhoneNumber();
                pb.setVisibility(View.VISIBLE);
                userExists();
            }
        });
    }

    private void createUser(){
        createUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerLogIn.this, CreateCustomer.class);
                startActivity(intent);
            }
        });
    }

    private void userExists(){
        StartActivity.mDatabaseReferenceAuth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot mDataSnapshot1 : dataSnapshot.getChildren()) {
                    if (mDataSnapshot1.getValue().toString().equals(completeNum)) {
                        userExists = true;
//                            CustomerLogIn.completeNum = StartActivity.mFireBaseAuth.getCurrentUser().getPhoneNumber();
                    }
                }

                if(userExists) {
                    pb.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(CustomerLogIn.this, ListPlacesActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    pb.setVisibility(View.INVISIBLE);
                    if(cellPhoneNumberExists !=null) {
                        cellPhoneNumberExists.setError("You don't exist in the system, you must register first");
                        cellPhoneNumberExists.requestFocus();
                    }else{
                        cellPhoneNumberExists.setError("Valid number is required");
                        cellPhoneNumberExists.requestFocus();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
