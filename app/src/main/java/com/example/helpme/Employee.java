package com.example.helpme;

import android.util.Log;

public class Employee {
    private String name;
    private String id;
    private String phone;
    private boolean isConnected = false;
    private WorkPlace workPlace;

    public Employee(String name, String id, String phone) {
        this.name = name;
        this.id = id;
        this.phone = "";
        createCellPhoneNumber(phone);
    }

    public Employee(){

    }

    private void createCellPhoneNumber(String phone) {
        if(phone!=null) {
            String number = phone.trim();
            if (number.isEmpty() || number.length() < 10) {
                this.phone = "";
            }
            if (number.charAt(0) == '0') {
                number = number.substring(1);
            }
            this.phone = "+972" + number;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone (String num){
        phone +=  num;
    }

    public String getPhone(){
        return phone;
    }

    public String getId() {
        return id;
    }

    public WorkPlace getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(WorkPlace workPlace) {
        this.workPlace = workPlace;
    }

    public void setId(String id) {
        this.id = id;
    }
}
