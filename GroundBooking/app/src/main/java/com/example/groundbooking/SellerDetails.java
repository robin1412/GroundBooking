package com.example.groundbooking;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.Exclude;

public class SellerDetails {

    private String groundName,userId;
    private String dayPrice,nightPrice;
    private String dayTimeStart,nightTimeEnd;
    private String profilePicLink;
    private String mkey;

    public SellerDetails() {
        //empty constructor needed
    }

    public SellerDetails(String groundName,String dayTimeStart,String nightTimeEnd,String dayPrice,String nightPrice,String userId,String profilePicLink) {
        this.dayPrice = dayPrice;
        this.profilePicLink= profilePicLink;
        this.dayTimeStart = dayTimeStart;
        this.nightPrice = nightPrice;
        this.nightTimeEnd = nightTimeEnd;
        this.groundName = groundName;
        this.userId = userId;
    }

    public String getDayPrice() {
        return this.dayPrice;
    }
    public void setDayPrice(String dayPrice) {
        this.dayPrice = dayPrice;
    }

    public String getDayTimeStart() {
        return this.dayTimeStart;
    }
    public void setDayTimeStart(String dayTimeStart) {
        this.dayTimeStart = dayTimeStart;
    }

    public String getNightPrice() { return this.nightPrice; }
    public void setNightPrice(String nightPrice) {
        this.nightPrice = nightPrice;
    }

    public String getProfilePicLink() { return this.profilePicLink; }
    public void setProfilePicLink(String profilePicLink) {
        this.profilePicLink = profilePicLink;
    }

    public String getNightTimeEnd() {
        return this.nightTimeEnd;
    }
    public void setNightTimeEnd(String nightTimeEnd) {
        this.nightTimeEnd = nightTimeEnd;
    }

    public String getGroundName() {
        return this.groundName;
    }
    public void setGroundName(String groundName) {
        this.groundName = groundName;
    }

    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public String getKey(){
        return mkey;
    }

    @Exclude
    public void setKey(String key){
        mkey = key;
    }

}
