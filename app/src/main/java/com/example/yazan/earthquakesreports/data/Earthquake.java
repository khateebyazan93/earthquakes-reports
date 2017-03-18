package com.example.yazan.earthquakesreports.data;

/**
 * Created by yazan on 2/4/17.
 */

public class Earthquake {

    /**
     * Magnitude of Earthquake
     */
    private double mMagnitude;

    /**
     * Location  of Earthquake
     */
    private String mLocation;
    /**
     * Time of Earthquake
     */
    private long mTimeInMilliseconds;
    /**
     * Url for more details about Earthquake
     */
    private String mUrl;

    /**
     * Construct a new {@link Earthquake} object .
     *
     * @param magnitude is magnitude(size) of the earthquake
     * @param location is the location where the Earthquake happened
     * @param timeInMilliseconds is the time in milliseconds when the earthquake happened
     * @param url is the website URL to find more detailS about earthquake*/
    public Earthquake(double magnitude, String location, long timeInMilliseconds, String url){
        this.mMagnitude = magnitude;
        this.mLocation = location;
        this.mTimeInMilliseconds = timeInMilliseconds;
        this.mUrl = url;

    }


    public double getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }


    public String getUrl() {
        return mUrl;
    }





}
