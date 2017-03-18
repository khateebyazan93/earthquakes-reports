package com.example.yazan.earthquakesreports;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.yazan.earthquakesreports.data.Earthquake;
import com.example.yazan.earthquakesreports.utils.QueryUtils;

import java.util.List;

/**
 * Created by yazan on 2/8/17.
 */

/**
 * Loads a list of earthquakes by using an AsyncTaskLoader to perform the
 * network request to the given URL.
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    /** Tag for log messages */
    private static final String LOG_TAG = EarthquakeLoader.class.getName();


    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link EarthquakeLoader} object.
     *
     * @param context of the activity
     * @param url given url to load the data from
     * */
    public EarthquakeLoader(Context context,String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG , "TEST : onStartLoading() called ...");

        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        Log.d(LOG_TAG , "TEST : loadInBackground() called ...");


        if(mUrl == null){
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Earthquake> earthquakes= QueryUtils.fetchEarthquakeData(mUrl);;
        return earthquakes;
    }
}
