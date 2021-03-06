package com.cmpe277.shelter.helper;

import android.graphics.Bitmap;

import java.util.UUID;

/**
 * Model for proeprty image.
 * Created by rishi on 5/6/16.
 */
public class PropertyImage {
    private UUID mId;
    private Bitmap mImageBitMap;
    private String mImagePath;
    private String imageName;
    private int imageResourceId;

    public PropertyImage(){
        mId = UUID.randomUUID();
        imageResourceId = 0;
    }

    public UUID getId(){
        return mId;
    }
    public Bitmap getImageBitMap() {
        return mImageBitMap;
    }

    public void setImageBitMap(Bitmap mImageBitMap) {
        this.mImageBitMap = mImageBitMap;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
