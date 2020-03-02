package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class FirstActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView backGround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        startApp();
    }

    private void startApp() {
        backGround=(ImageView)findViewById(R.id.picGround);
        progressBar=(ProgressBar)findViewById(R.id.first_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        initFirebase();
        openMainPage();
    }

    private void initFirebase() {
        StartActivity.mFireBaseAuth= FirebaseAuth.getInstance();
        StartActivity.mDatabaseReferenceAuth = FirebaseDatabase.getInstance().getReference("cellPhone");
        StartActivity.mDatabaseReferencePlaces = FirebaseDatabase.getInstance().getReference("places");
        StartActivity.storage = FirebaseStorage.getInstance();
        StartActivity.storageRef = StartActivity.storage.getReference();
    }

    private void openMainPage() {
        progressBar.setVisibility(View.INVISIBLE);
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        backGround.startAnimation(logoAnim);
        if(StartActivity.mFireBaseAuth!=null && StartActivity.mDatabaseReferenceAuth!=null &&
                StartActivity.mDatabaseReferencePlaces !=null && StartActivity.storage !=null &&
                StartActivity.storageRef!=null) {
            Intent intent = new Intent(FirstActivity.this, StartActivity.class);
            startActivity(intent);
        }
    }

}
