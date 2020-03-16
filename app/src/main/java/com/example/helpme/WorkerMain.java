package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WorkerMain extends AppCompatActivity {
    private final String WORK_PLACE="WorkPlaceName";
    private final String WORK_PLACE_CALLS = "workPlaceCalls";
    private final String EMPLOYEE="nameOfEmployee";
    private final String EMPLOYEES="employees";
    private final String CALLS="calls";
    private final String UPLOADS = "Uploads";
    private final String PLACES="places";
    private final String CUSTOMER_MAIL="customerMail";
    private final String PIC="pic";
    private final String TOKEN="token";
    public final String UID="callUid";
    public final String TITLE = "New Call";
    public final String BODY = "There is a new call in ";
    private final String HI="Hi ";
    private final String STORE="Store: ";
    private TextView name;
    private TextView place;
    private ImageView image;
    private WorkPlace workPlace;
    private ListView callsList;
    private StorageReference listRef;
    private DatabaseReference callsRef;
    private ArrayList<Call> calls;
    private Call call;
    private Button confirm;
    private Button cancel;
    private ImageView imagePopup;
    private int counter=0;
    private ArrayAdapter arrayAdapter;
    private String placeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_main);
        initView();
        createListView();
        findCustomerPhoneClickOnList();//check what is clicked and get the cellphone number of customer
        FirebaseMessaging.getInstance().subscribeToTopic(CALLS);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    String token = task.getResult().getToken();
                    saveToken(token);
                }
                else
                {}
            }
        });
    }

    private void saveToken(String token) {
        String mail = StartActivity.mFireBaseAuth.getCurrentUser().getEmail();
        Employee e = new Employee(mail,token);
        StartActivity.mDatabaseReferencePlaces.child(workPlace.getName()).child(EMPLOYEES)
                .child(StartActivity.mFireBaseAuth.getCurrentUser().getUid()).setValue(e).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){}
                            else
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
    }



    private void initView() {
        name=findViewById(R.id.worker_name);
        place=findViewById(R.id.place_name);
        callsList=findViewById(R.id.workMainlistView);
        name.setText(getIntent().getStringExtra(EMPLOYEE));
        placeID = getIntent().getStringExtra(WORK_PLACE);

        //found right place
        for(WorkPlace p : WorkerLogIn.places_worker.getArrayList()){
            if (p.getCode().equals(placeID))
                workPlace = p;
        }
        if(workPlace!=null)
            place.setText(STORE + workPlace.getName());

    }

    private void createListView(){
        if(workPlace!=null) {
            callsRef = StartActivity.mDatabaseReferencePlaces.child(workPlace.getName()).child(UPLOADS);
            callsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    calls = new ArrayList<>();
                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        String retrieveUid = dataSnapshot.child(post.getKey()).child(UID).getValue().toString();
                        String retrieveCustomerMail = dataSnapshot.child(post.getKey()).child(CUSTOMER_MAIL).getValue().toString();
                        String retrievePic = dataSnapshot.child(post.getKey()).child(PIC).getValue().toString();
                        String retrieveToken = dataSnapshot.child(post.getKey()).child(TOKEN).getValue().toString();
                        if (!dataSnapshot.child(retrieveToken).exists()) {
                            calls.add(new Call(retrieveCustomerMail, retrievePic,retrieveToken,retrieveUid));
                        } else {
                            calls.remove(new Call(retrieveCustomerMail, retrievePic,retrieveToken,retrieveUid));
                            calls.add(new Call(retrieveCustomerMail, retrievePic,retrieveToken,retrieveUid));
                        }
                    }
                    //create adapter
                     arrayAdapter =
                            new ArrayAdapter(WorkerMain.this, android.R.layout.simple_list_item_1, calls);
                    //add to listView
                    callsList.setAdapter(arrayAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }

    private void findCustomerPhoneClickOnList(){
        callsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                call=(Call)callsList.getItemAtPosition(i);
                createPopUp();
            }
        });

    }

    private void createPopUp() {
        final Dialog myDialog = new Dialog(WorkerMain.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.popup_custom_dialog);

        confirm = (Button)myDialog.findViewById(R.id.confirm);
        cancel = (Button)myDialog.findViewById(R.id.cancel);
        imagePopup=myDialog.findViewById(R.id.image_popup);
        getImageFromStorage(imagePopup);//view the photo in imageview

        confirm.setEnabled(true);
        cancel.setEnabled(true);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.messageToCustomer, Toast.LENGTH_LONG).show();
                sendAlertToCustomer(TITLE,BODY + workPlace.getName());
                removeCallFromDatabaseAndStorage(call);
                calls.remove(call);//remove from list maybe we dont need this
                myDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });


        myDialog.show();

    }

    private void sendAlertToCustomer(String title , String body) {
            MyFirebaseMessagingService.setTitle("New Call");
            MyFirebaseMessagingService.setBody("There is a new call in " + workPlace.getName());
            String token = call.getToken();
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://fcm.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create()).build();
            Api api = retrofit.create(Api.class);
            api.sendNotification(new Sender(new Data(call.getCallUid(),
                    R.drawable.helpmeicon ,body,title, StartActivity.mFireBaseAuth.getCurrentUser().getUid()),token)).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==200)
                        Log.d("ifsuccess", "onResponse: success");
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

                }
            });

    }

    private void getImageFromStorage(ImageView image) {
        Picasso.with(WorkerMain.this).load(call.getPic()).into(image);
    }

    private void removeCallFromDatabaseAndStorage(Call c){
       callsRef.child(call.getToken()).removeValue();
       StartActivity.storageRef.child(workPlace.getName()).child(WORK_PLACE_CALLS).child(call.getToken()).delete();

    }

    private void logOut(){
        final FirebaseUser user = StartActivity.mFireBaseAuth.getCurrentUser();
        final DatabaseReference dbUsers = FirebaseDatabase.getInstance()
                .getReference().child(workPlace.getName()).child(EMPLOYEES);
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
        StartActivity.mFireBaseAuth.signOut();
    }


    @Override
    public void onBackPressed() {
        counter++;
        Toast.makeText(this, R.string.logOutMessage, Toast.LENGTH_SHORT).show();
        if(counter==2){
            super.onBackPressed();
            logOut();
        }
    }

}
