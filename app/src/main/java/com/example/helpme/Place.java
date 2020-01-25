package com.example.helpme;

public class Place implements Comparable {
    private String name;
    private int companyCode;

    public Place(String name, int companyCode) {
        this.name = name;
        this.companyCode = companyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(int companyCode) {
        this.companyCode = companyCode;
    }

    @Override
    public int compareTo(Object place) {
        Place other = (Place) place;
        if (this.name.charAt(0) > other.name.charAt(0))
                return 1;
        else
            return -1;
    }
}
