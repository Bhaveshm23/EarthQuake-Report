package com.example.android.quakereport;

import static com.example.android.quakereport.R.id.location_offset;
import static com.example.android.quakereport.R.id.location_primary;


/**
 * Created by BHAVESH MOTIRAMANI on 22-12-2017.
 */

public class Earthquake {

    //Magnitude

    private double mMagnitude;

    //city

    private String mLocation;

    //date


    private long mTimeInMilliseconds;

    private String mUrl;

    //Constructor
    public Earthquake(double magnitude, String location, long timeInMilliseconds,String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl=url;
    }

    public long getmTimeInMilliseconds()
    {

        return mTimeInMilliseconds;
    }


    public double getMagnitude()
    {
        return mMagnitude;
    }

    public String getLocation()
    {
        return mLocation;
    }

    public String getUrl()
    {
        return mUrl;
    }



}
