package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class WorkerMain extends AppCompatActivity {
    private final String WORK_PLACE="WorkPlaceName";
    private final String EMPLOYEE="nameOfEmployee";
    private final String UPLOADS = "uploads";
    private final String HI="Hi ";
    private final String STORE="Store: ";
    private TextView name;
    private TextView place;
    private WorkPlace workPlace;
    private ListView callsList;
    private StorageReference listRef;
    private DatabaseReference callsRef;
    private ArrayList<Call> calls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_main);
        initView();
    }

    private void initView() {
        name=findViewById(R.id.worker_name);
        place=findViewById(R.id.place_name);
        callsList=findViewById(R.id.workMainlistView);
        name.setText(HI + getIntent().getStringExtra(EMPLOYEE));
        String placeID = getIntent().getStringExtra(WORK_PLACE);
        for(WorkPlace p : StartActivity.places.getArrayList()){
            if (p.getCode().equals(placeID))
                workPlace = p;
        }
        place.setText(STORE + workPlace.getName());
        calls = new ArrayList<>();
        listRef = StartActivity.storageRef.child(workPlace.getName()).child("workPlaceCalls");
        callsRef = StartActivity.mDatabaseReferencePlaces.child(workPlace.getName()).child(UPLOADS);
        callsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Call call = postSnapshot.getValue(Call.class);
                    calls.add(call);
                }
                ArrayAdapter arrayAdapter=
                        new ArrayAdapter(WorkerMain.this,android.R.layout.simple_list_item_1,calls);
                callsList.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
