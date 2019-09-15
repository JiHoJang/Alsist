package com.example.alsist;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by 장지호 on 2018-06-05.
 */

public class MyApplication extends Application {
    private String UserEmail;
    private String UserName;
    private String UserAlsistNum;
    private boolean UserRent = false;
    private LatLng UserLocation;

    public LatLng getUserLocation() {return UserLocation;}

    public String getUserEmail() {
        return UserEmail;
    }

    public String getUserName() {
        return UserName;
    }

    public String getUserLogNum() {
        return UserAlsistNum;
    }

    public boolean getUserRent() {return UserRent;}

    public void setUserLocation(LatLng UserLocation) {this.UserLocation = UserLocation;}

    public void setUserEmail(String UserEmail) {
        this.UserEmail = UserEmail;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setUserLogNum(String UserLogNum) {
        this.UserAlsistNum = UserLogNum;
    }

    public void setUserRent(boolean UserRent) {this.UserRent = UserRent;}
}
