package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class CustomerLogIn extends AppCompatActivity {
    private final String TAG="CustomerLogIn";
    private final String PHONE_NUM="PhoneNum";
    private Button send_verification_Btn;
    private Button verify_code_Btn;
    private EditText cellPhoneNumber;
    private Spinner spinner;
    private ProgressBar progress_bar_phone_num;
    private String mVerificationId;
    public static String completeNum;
    private EditText mCodeText;
    private int whatToDo=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_log_in);
        initViews();
        initSpinner();
        logIn();
    }

    private void initViews() {
        send_verification_Btn=(Button)findViewById(R.id.send_verification_Btn);
        verify_code_Btn=(Button)findViewById(R.id.verify_code_Btn);
        verify_code_Btn.setEnabled(false);
        verify_code_Btn.setVisibility(View.INVISIBLE);
        spinner=(Spinner)findViewById(R.id.spinner);
        cellPhoneNumber=(EditText)findViewById(R.id.phone_num);
        progress_bar_phone_num=(ProgressBar)findViewById(R.id.progress_bar_phone_num);
        progress_bar_phone_num.setVisibility(View.INVISIBLE);
        mCodeText=(EditText)findViewById(R.id.codeText);
        mCodeText.setVisibility(View.INVISIBLE);
    }

    private void initSpinner() {
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,CountryData.countryNames));
    }

    private void createCellPhoneNumber() {
        String code= CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
        String number=cellPhoneNumber.getText().toString().trim();
        if(number.isEmpty() || number.length() < 10){
            cellPhoneNumber.setError("Valid number is required");
            cellPhoneNumber.requestFocus();
            return;
        }

        if(number.charAt(0)=='0'){
            number=number.substring(1);
        }
        completeNum="+" + code + number;
    }

    private void logIn() {
        send_verification_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCellPhoneNumber();
                sendVerificationCode(completeNum);
                send_verification_Btn.setEnabled(false);
                send_verification_Btn.setVisibility(View.INVISIBLE);
                verify_code_Btn.setEnabled(true);
                verify_code_Btn.setVisibility(View.VISIBLE);

            }
        });
        verify_code_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=mCodeText.getText().toString().trim();
                if(code.isEmpty() || code.length() <6){
                    mCodeText.setError("Enter code ...");
                    mCodeText.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });


    }

    private void verifyCode(String code){
        PhoneAuthCredential mPhoneAuthCredential=PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredential(mPhoneAuthCredential);
    }

    private void sendVerificationCode(String number){
        progress_bar_phone_num.setVisibility(View.VISIBLE);
        Log.d(TAG, "createCellPhoneNumber: " +completeNum);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        number,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        CustomerLogIn.this,               // Activity (for callback binding)
                        mCallBacks);        // OnVerificationStateChangedCallbacks/
        mCodeText.setVisibility(View.VISIBLE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks=
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code !=null){
                mCodeText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(CustomerLogIn.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };


    private void addNumberToDatabase(){
        StartActivity.mDatabaseReferenceAuth.push().setValue(completeNum);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        StartActivity.mFireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            addNumberToDatabase();
                            Toast.makeText(CustomerLogIn.this,
                                    "Code verification and login succeeded", Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(CustomerLogIn.this,ListPlacesActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }


    private void userExists(){
        StartActivity.mDatabaseReferenceAuth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    boolean foundUser = false;
                    for (DataSnapshot mDataSnapshot1 : dataSnapshot.getChildren()) {
                        if (mDataSnapshot1.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            foundUser = true;
                        }
                    }
                    if (foundUser) {
                        completeNum = StartActivity.mFireBaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(getApplicationContext(),
                                "User exists...login succeeded", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CustomerLogIn.this, ListPlacesActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        userExists();

    }
}