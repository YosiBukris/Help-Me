package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class WorkerMain extends AppCompatActivity {
    private final String WORK_PLACE="WorkPlaceName";
    private final String EMPLOYEE="nameOfEmployee";
    private final String UPLOADS = "Uploads";
    private final String CUSTOMER_NUMBER="customerNumber";
    private final String PIC="pic";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_main);
        initView();
        createListView();
        findCustomerPhoneClickOnList();//check what is clicked and get the cellphone number of customer
    }



    private void initView() {
        image=findViewById(R.id.location_photo);
        name=findViewById(R.id.worker_name);
        place=findViewById(R.id.place_name);
        callsList=findViewById(R.id.workMainlistView);
        name.setText(HI + getIntent().getStringExtra(EMPLOYEE));
        String placeID = getIntent().getStringExtra(WORK_PLACE);

        //found right place
        for(WorkPlace p : StartActivity.places.getArrayList()){
            if (p.getCode().equals(placeID))
                workPlace = p;
        }
        place.setText(STORE + workPlace.getName());
        calls = new ArrayList<>();
    }



    private void createListView(){
        callsRef = StartActivity.mDatabaseReferencePlaces.child(workPlace.getName()).child(UPLOADS);
        callsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot post:dataSnapshot.getChildren()){
                    String retrieveCellphoneNumber = dataSnapshot.child(post.getKey()).child(CUSTOMER_NUMBER).getValue().toString();
                    String retrievePic = dataSnapshot.child(post.getKey()).child(PIC).getValue().toString();
                    if(!dataSnapshot.child(post.getKey()).exists()) {
                        calls.add(new Call(retrieveCellphoneNumber, retrievePic));
                    }else{
                        calls.remove(new Call(retrieveCellphoneNumber,retrievePic));
                        calls.add(new Call(retrieveCellphoneNumber, retrievePic));
                    }
                }

                //create adapter
                ArrayAdapter arrayAdapter=
                        new ArrayAdapter(WorkerMain.this,android.R.layout.simple_list_item_1,calls);
                //add to listView
                callsList.setAdapter(arrayAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
        final Dialog MyDialog = new Dialog(WorkerMain.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.popup_custom_dialog);

        confirm = (Button)MyDialog.findViewById(R.id.confirm);
        cancel = (Button)MyDialog.findViewById(R.id.cancel);
        imagePopup=findViewById(R.id.image_popup);
        getImageFromStorage();//view the photo in imageview

        confirm.setEnabled(true);
        cancel.setEnabled(true);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Hello, I'm Custom Alert Dialog", Toast.LENGTH_LONG).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });

        MyDialog.show();
    }

    private void getImageFromStorage() {
        Picasso.with(WorkerMain.this).load(call.pic).into(image);
    }
}
