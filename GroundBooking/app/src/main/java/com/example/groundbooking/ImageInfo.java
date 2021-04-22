package com.example.groundbooking;

import com.google.firebase.database.Exclude;

public class ImageInfo {
    private String mUid,mFilename;
    private String mImageUrl;
    private String mkey;

    public ImageInfo(){
        //empty constructor needed
    }

    public ImageInfo(String uid,String filename, String imageUrl){
        mUid = uid;
        mImageUrl = imageUrl;
        mFilename = filename;
    }

    public String getUid(){
        return mUid;
    }

    public void setUid(String uid){
        mUid = uid;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    public String getFilename(){
        return mFilename;
    }

    public void setFilename(String filename){
        mFilename = filename;
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
