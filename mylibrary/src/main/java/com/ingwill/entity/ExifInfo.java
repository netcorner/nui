package com.ingwill.entity;

import com.ingwill.db.Entity;

import java.util.Date;

/**
 * Created by shijiufeng on 2017/8/27.
 */

public class ExifInfo  extends Entity {
    private double Latitude;
    private double Longitude;
    private Date AddTime;

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public Date getAddTime() {
        return AddTime;
    }

    public void setAddTime(Date addTime) {
        AddTime = addTime;
    }
}
