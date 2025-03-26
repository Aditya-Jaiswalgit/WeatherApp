package com.weatherapp.android.Domains;

public class Hourly {
    private String hour;
    private int temp;
//    private String condition; // Weather condition text (e.g., "Clear", "Sunny")
    private String iconUrl;   // URL for the weather icon

    public Hourly(String hour, int temp, String iconUrl) {
        this.hour = hour;
        this.temp = temp;
//        this.condition = condition;
        this.iconUrl = iconUrl;
    }

    public String getHour() {
        return hour;
    }

    public int getTemp() {
        return temp;
    }
//
//    public String getCondition() {
//        return condition;
//    }

    public String getIconUrl() {
        return iconUrl;
    }
}