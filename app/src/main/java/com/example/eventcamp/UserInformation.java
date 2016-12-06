package com.example.eventcamp;

/**
 * Created by Sunilkumar on 12-11-2016.
 */

public class UserInformation {
    public String Username;
    public String Branch;
    public String Year;

    public UserInformation(){

    }

    public UserInformation(String Username, String Branch, String Year) {
        this.Username = Username;
        this.Branch = Branch;
        this.Year = Year;
    }

    public String getUsername() {return Username;}

    public void setUsername(String username) {Username = username;}

    public String getBranch() {return Branch;}

    public void setBranch(String branch) {Branch = branch;}

    public String getYear() {return Year;}

    public void setYear(String year) {Year = year;}
}