package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class ListPlacesActivity extends AppCompatActivity {
    private final String CUSTOMER_NAME="customerName";
    private final String NAME_OF_PLACE="nameOfPlace";
    private ListView listView;
    private String nameOfChosePlace;
    private boolean isChoosePlace=false;
    private TextView nameEditText;
    public static PlaceFactory places;
    private Button confirmBtn;
    public static boolean userInTheSystem=false;
    public interface DataStatus{
        void DataIsLoaded(ArrayList<WorkPlace> places);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        init();
        startInitPlaces();
        choosePlace();
        connectAndStartListPlacesActivity();
    }

    private void init(){
        userInTheSystem=true;
        confirmBtn=(Button) findViewById(R.id.loginConfirmBtn);
        nameEditText=(EditText)findViewById(R.id.customerEditName);
        listView=(ListView)findViewById(R.id.listView);
        places = new PlaceFactory();
    }

    //choose place
    private void choosePlace() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearBackgroundItems();
                nameOfChosePlace = listView.getItemAtPosition(position).toString();
                view.setBackgroundColor(Color.YELLOW);
                isChoosePlace=true;
            }
        });
    }

    //clear all places that sign before
    private void clearBackgroundItems(){
        for(View v:listView.getTouchables()){
            v.setBackgroundColor(00000000);
        }
    }

    private void connectAndStartListPlacesActivity(){
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isChoosePlace && !nameEditText.getText().toString().equals("")) {
                        Intent intent = new Intent(ListPlacesActivity.this, CustomerMain.class);
                        intent.putExtra(CUSTOMER_NAME, nameEditText.getText().toString());
                        intent.putExtra(NAME_OF_PLACE, nameOfChosePlace);
                        startActivity(intent);
                    }else if(nameEditText.getText().toString().equals("")){
                        nameEditText.setError("Help us to find you, please enter your name");
                        nameEditText.requestFocus();
                    }else if(isChoosePlace==false){
                        Toast.makeText(ListPlacesActivity.this,
                                "You must choose one place", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void startInitPlaces(){
        initPlaces(new ListPlacesActivity.DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<WorkPlace> places) {
                ListPlacesActivity.places.setArrayList(places);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    private void initPlaces(final DataStatus dataStatus) {
        StartActivity.mDatabaseReferencePlaces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<WorkPlace> temp = new ArrayList<>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    WorkPlace p = d.getValue(WorkPlace.class);
                    temp.add(p);
                }
                dataStatus.DataIsLoaded(temp);
                ArrayAdapter arrayAdapter =
                        new ArrayAdapter(ListPlacesActivity.this, android.R.layout.simple_list_item_1, ListPlacesActivity.places.returnNames());
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(ListPlacesActivity.this, StartActivity.class);
//        startActivity(intent);
//        finish();
    }
}
