package com.example.helpme;

import java.util.ArrayList;
import java.util.Collections;

public class PlaceFactory {

    private ArrayList<Place> arrayList=new ArrayList<>();
    private static int index = -1;

    public PlaceFactory(ArrayList<Place> arrayList) {
        this.arrayList = arrayList;
    }

    public PlaceFactory() {
        this.arrayList = new ArrayList<Place>();
    }

    public void addPlace(Place p){
        arrayList.add(p);
    }

    public void sortPlaces(){
        Collections.sort(arrayList);
    }

    public Place getPlace(){
        index++;
        return arrayList.get(index);
    }

    public ArrayList<String> returnNames(){
        ArrayList<String> places = new ArrayList<>();
        for (Place p : arrayList){
            places.add(p.getName());
        }
        return places;
    }

}
