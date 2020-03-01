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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class ListPlacesActivity extends AppCompatActivity {
    private final String CUSTOMER_NAME="customerName";
    private final String NAME_OF_PLACE="nameOfPlace";
    private final String PHONE_NUM="PhoneNum";
    private ListView listView;
    private String nameOfChosePlace;
    private boolean isChoosePlace=false;
    private TextView nameEditText;
    private ImageView confirmBtn;
    private String completeNum;

    private Map<Integer,String> placeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        initViews();
        createListViews();
        choosePlace();
        connectAndStartListPlacesActivity();
    }

    private void initViews(){
        confirmBtn=(ImageView)findViewById(R.id.confirmBtn);
        nameEditText=(EditText)findViewById(R.id.customerEditName);
        listView=(ListView)findViewById(R.id.listView);
        //completeNum = getIntent().getStringExtra(PHONE_NUM);
        createListViews();
    }

    private void createListViews() {
        if(StartActivity.places!=null) {
            ArrayAdapter arrayAdapter =
                    new ArrayAdapter(this, android.R.layout.simple_list_item_1, StartActivity.places.returnNames());
            listView.setAdapter(arrayAdapter);
        }
    }

    //choose place
    private void choosePlace() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearBackgroundItems();
                nameOfChosePlace = listView.getItemAtPosition(position).toString();
                view.setBackgroundColor(Color.CYAN);
                isChoosePlace=true;
            }
        });
    }

    //clear all places that sign before
    private void clearBackgroundItems(){
        for(View v:listView.getTouchables()){
            v.setBackgroundColor(Color.WHITE);
        }
    }

    private void connectAndStartListPlacesActivity(){
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isChoosePlace) {
                    Intent intent = new Intent(ListPlacesActivity.this, CustomerMain.class);
                    intent.putExtra(CUSTOMER_NAME, nameEditText.getText().toString());
                    intent.putExtra(NAME_OF_PLACE, nameOfChosePlace);
                    //intent.putExtra(PHONE_NUM,completeNum);
                    startActivity(intent);
                    }else{
                        Toast.makeText(ListPlacesActivity.this,
                                "You must choose one place", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public void onBackPressed() {
        //no action
    }
}
