package com.example.helpme;

import java.util.ArrayList;
import java.util.Collections;

public class PlaceFactory {

    private ArrayList<WorkPlace> arrayList=new ArrayList<>();
    private static int index = -1;

    public PlaceFactory(ArrayList<WorkPlace> arrayList) {
        this.arrayList = arrayList;
    }

    public PlaceFactory() {
        this.arrayList = new ArrayList<WorkPlace>();
    }

    public void addPlace(WorkPlace p){
        arrayList.add(p);
    }

    public void sortPlaces(){
        Collections.sort(arrayList);
    }

    public WorkPlace getPlace(){
        index++;
        return arrayList.get(index);
    }

    public ArrayList<WorkPlace> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<WorkPlace> arrayList) {
        this.arrayList = arrayList;
    }

    public static int getIndex() {
        return index;
    }

    public ArrayList<String> returnNames(){
        ArrayList<String> places = new ArrayList<>();
        for (WorkPlace p : arrayList){
            places.add(p.getName());
        }
        return places;
    }

    public ArrayList<String> returnCodes(){
        ArrayList<String> codes = new ArrayList<>();
        for (WorkPlace p : arrayList){
            codes.add(p.getCode());
        }
        return codes;
    }

}
