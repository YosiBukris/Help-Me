package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.w3c.dom.Text;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomerMain extends AppCompatActivity {
    private final String HI="Hi ";
    private final String PLACE="Place: ";
    private final String CUSTOMER_NAME="customerName";
    private final String NAME_OF_PLACE="nameOfPlace";
    private final String PHONE_NUM="PhoneNum";
    private Button cameraBtn;
    private TextView sendBtn;
    private ImageView returnPhoto;
    private TextView name;
    private TextView place;
    private String intentName;
    private String intentPlace;
    private WorkPlace currentPlace;
    private Uri mImapPic;
    private boolean photoExists=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        initViews();
        getNameAndStoreFromCustomerMain();
        clickToTakeAPhoto();
        sendAlertToWorker();

    }

    private void initClientService() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        Log.d(TAG, token);
                        Toast.makeText(CustomerMain.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initViews(){
        cameraBtn =(Button)findViewById(R.id.cameraBTN);
        sendBtn   =(TextView)findViewById(R.id.sendBTN);
        returnPhoto=(ImageView)findViewById(R.id.photo_image_view);
        name=(TextView)findViewById(R.id.name_textView);
        place=(TextView)findViewById(R.id.place_name_textView);
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
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });
    }

    private void sendAlertToWorker() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(photoExists) {
                        currentPlace.addCall(CustomerLogIn.completeNum,returnPhoto, mImapPic);//here we send alert to employees
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
        if(data!=null) {
            super.onActivityResult(requestCode, resultCode, data);
            Bitmap bitmap=(Bitmap)data.getExtras().get("data");
            returnPhoto.setImageBitmap(bitmap);
            photoExists = true;
            mImapPic = data.getData();

            Log.d(TAG, "onActivityResult: " + mImapPic);
        }else{
            Intent intent=new Intent(this,CustomerMain.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
