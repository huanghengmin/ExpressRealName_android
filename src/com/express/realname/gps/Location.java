package com.express.realname.gps;

/**
 * Created by Administrator on 15-11-13.
 */
public class Location {
    private String longitude;
    private String latitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
