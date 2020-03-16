package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class CreateCustomer extends AppCompatActivity {
    private final String TAG="CustomerLogIn";
    private EditText mail;
    private EditText password;
    private EditText confirmPassword;
    private Button signUp_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        initViews();
        signUp();
    }

    private void initViews() {
        mail=findViewById(R.id.mail_of_customer_signUp);
        password=findViewById(R.id.password_signUp);
        confirmPassword=findViewById(R.id.confrim_password_signUp);
        signUp_btn=findViewById(R.id.signUp_btn);
    }

    private void signUp() {
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    private void createUser(){
        String mailCus=mail.getText().toString().trim();
        String password_cus=password.getText().toString();
        String confirmPasswordCus=confirmPassword.getText().toString();

        if(mailCus.isEmpty()){
            mail.setError("Invalid email");
            mail.requestFocus();
        }else if(password_cus.isEmpty()) {
            password.setError("Invalid password");
            password.requestFocus();

        }else if(confirmPasswordCus.isEmpty()) {
            confirmPassword.setError("Invalid password");
            confirmPassword.requestFocus();
        }
        else if(confirmPasswordCus.equals(password_cus)){

            StartActivity.mFireBaseAuth.createUserWithEmailAndPassword(mailCus, password_cus)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(CreateCustomer.this, ListPlacesActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(CreateCustomer.this, "This email is exists", Toast.LENGTH_LONG).show();
                                } else
                                    Toast.makeText(CreateCustomer.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }else{
            Toast.makeText(CreateCustomer.this, "check again your confirm password", Toast.LENGTH_LONG).show();
        }

    }

}