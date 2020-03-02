package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.okhttp.ResponseBody;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomerMain extends AppCompatActivity {
    private static final int PERMISSION_CODE=1000;
    private static final int IMAGE_CAPTURE_CODE=1001;
    private final String HI="Hi ";
    private final String PLACE="Place: ";
    private final String CUSTOMER_NAME="customerName";
    private final String NAME_OF_PLACE="nameOfPlace";
    private final String PHONE_NUM="PhoneNum";
    private Button cameraBtn;
    private Button sendBtn;
    private ImageView returnPhoto;
    private TextView name;
    private TextView place;
    private String intentName;
    private String intentPlace;
    private WorkPlace currentPlace;
    private Uri imageUri;
    private List<Employee> employeeList;
    private boolean photoExists=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        initViews();
        getNameAndStoreFromCustomerMain();
        clickToTakeAPhoto();
        loadUsers();
        sendAlertToWorker();
    }

    private void loadUsers() {
        employeeList = new ArrayList<>();
        final DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("places").child(currentPlace.getName()).child("employees");
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dsUser : dataSnapshot.getChildren()) {
                        Employee employee = dsUser.getValue(Employee.class);
                        employee.setUid(dsUser.getKey());
                        employeeList.add(employee);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews(){
        cameraBtn =(Button)findViewById(R.id.cameraBTN);
        sendBtn   =(Button) findViewById(R.id.sendBTN);
        returnPhoto=(ImageView)findViewById(R.id.photo_image_view);
        name=(TextView)findViewById(R.id.name_textView);
        place=(TextView)findViewById(R.id.place_name_textView);
        sendBtn.setVisibility(View.INVISIBLE);
    }

    private void getNameAndStoreFromCustomerMain(){
        intentName=getIntent().getStringExtra(CUSTOMER_NAME);
        intentPlace=getIntent().getStringExtra(NAME_OF_PLACE);
        for(WorkPlace p : StartActivity.places.getArrayList()){
            if(p.getName().equals(intentPlace))
                currentPlace = p;
        }
        name.setText(HI + intentName);
        place.setText(PLACE + intentPlace);
    }

    private void clickToTakeAPhoto(){
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED ||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String[] permission={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,PERMISSION_CODE);
                    }
                    else{
                        openCamera();
                    }
                }
                else{
                    openCamera();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openCamera(){
        ContentValues values =new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From The Camera");
        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,IMAGE_CAPTURE_CODE);
    }

    private void sendAlertToWorker() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(photoExists && employeeList!=null) {
                        currentPlace.addCall(CustomerLogIn.completeNum,returnPhoto, imageUri);
                        String title = "New Call";
                        String body = "New call in " + currentPlace.getName() + " from " + name;
                        for(Employee e : employeeList){
                            String token = e.getToken();
                            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://fcm.googleapis.com/")
                                    .addConverterFactory(GsonConverterFactory.create()).build();
                            Api api = retrofit.create(Api.class);
                            api.sendNotification(new Sender(new Data(StartActivity.mFireBaseAuth.getCurrentUser().getUid(),
                                    R.drawable.helpmeicon ,body,title, e.getUid()),token)).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.code()==200)
                                        Log.d("ifsuccess", "onResponse: success");
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        }
                        Toast.makeText(CustomerMain.this,
                                "Your request has been sent", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(CustomerMain.this,
                                "You must take a photo", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    //func for camera activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK) {
            returnPhoto.setImageURI(imageUri);
            photoExists = true;
            sendBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
