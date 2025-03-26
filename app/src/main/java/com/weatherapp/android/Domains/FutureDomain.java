package com.weatherapp.android.Domains;

public class FutureDomain {
    private String day;
    private String picPath;
    private String status;
    private int highTemp;
    private int lowTemp;

    public FutureDomain(String day, String status, String picPath, int highTemp, int lowTemp) {
        this.day = day;
        this.status = status;
        this.picPath = picPath;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(int highTemp) {
        this.highTemp = highTemp;
    }

    public int getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(int lowTemp) {
        this.lowTemp = lowTemp;
    }
}
