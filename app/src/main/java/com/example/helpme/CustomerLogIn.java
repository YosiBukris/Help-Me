package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class CustomerLogIn extends AppCompatActivity {
    private Button logIn;
    private TextView createUserTextView;
    private EditText mailOfCustomer;
    private EditText password;
    private ProgressBar pbCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_log_in);
        initViews();
        logIn();
        createUser();
    }

    private void initViews() {
        mailOfCustomer=findViewById(R.id.mail_of_customer);
        password=findViewById(R.id.password);
        logIn = findViewById(R.id.login_btn);
        pbCustomer = findViewById(R.id.customerLogIn_pb);
        createUserTextView = (TextView) findViewById(R.id.create_user);
        pbCustomer.setVisibility(View.INVISIBLE);
    }

    private void logIn() {
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void userExists() {
        pbCustomer.setVisibility(View.VISIBLE);
        String mail=mailOfCustomer.getText().toString().trim();
        String cus_password=password.getText().toString();
        if(mail.isEmpty()){
            mailOfCustomer.setError("Invalid email");
            mailOfCustomer.requestFocus();
        }else if(cus_password.isEmpty()){
            password.setError("Invalid password");
            password.requestFocus();
        }else{
            StartActivity.mFireBaseAuth
                    .signInWithEmailAndPassword(mail,cus_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(CustomerLogIn.this, ListPlacesActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(CustomerLogIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        pbCustomer.setVisibility(View.INVISIBLE);
    }
}
