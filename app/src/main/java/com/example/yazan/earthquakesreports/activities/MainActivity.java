package com.example.yazan.earthquakesreports.activities;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yazan.earthquakesreports.EarthquakeLoader;
import com.example.yazan.earthquakesreports.R;
import com.example.yazan.earthquakesreports.adapters.EarthquakeAdapter;
import com.example.yazan.earthquakesreports.data.Earthquake;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {


    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * EARTHQUAKE LOADER ID
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /**
     * earthquake URL data from the USGS website
     */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Adapter for the list of earthquakes
     */
    private EarthquakeAdapter mAdapter;

    View mLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST : Earthquack Activity onCreate called ...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicatorView = findViewById(R.id.loading_indicator);

        //find and attach the empty state TextView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_state);

        //find the RecyclerView within the main activity
        RecyclerView earthquakeRecyclerView = (RecyclerView) findViewById(R.id.list);


        //fill adapter with empty ArrayList
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        //setup earthquake RecyclerView
        earthquakeRecyclerView.setAdapter(mAdapter);
        earthquakeRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                Log.d(LOG_TAG," TEST : onChanged() Called...");
                super.onChanged();
                checkAdapterIsEmpty();

            }
        });

        //setup item click listener for earthquake RecyclerView
        mAdapter.setOnItemClickListener(new EarthquakeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Earthquake earthquakeItem) {

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(earthquakeItem.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);

            }
        });


        // Get ConnectivityManager object to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Get LoaderManager object
            LoaderManager loaderManager = getLoaderManager();

            Log.i(LOG_TAG, "TEST : initLoader() called ...");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        } else {
            // hide loading indicator , because no network connection
            checkAdapterIsEmpty();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get ConnectivityManager object to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null){
            Toast.makeText(this,"NO INTERNET CONNECTION",Toast.LENGTH_LONG).show();
        }
    }


    /**
 * Check if the adapter is empty OR NOT
 * */
    private void checkAdapterIsEmpty() {
        //set " No Internet " text
        mEmptyStateTextView.setText(R.string.no_internet);

        //check if the adapter empty OR not
        if (mAdapter.getItemCount() == 0) {
            mLoadingIndicatorView.setVisibility(View.GONE);
            mEmptyStateTextView.setText("                 " +
                    "PLEASE ! \n CONNECT THE INTERNET \n AND RESTART THE APP" );
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "TEST : onCreateLoader() called ...");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String maxMagnitude = sharedPrefs.getString(
                getString(R.string.settings_max_magnitude_key),
                getString(R.string.settings_max_magnitude_default));

        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));


// create Uri object from given USGS url
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //APPEND QUERY to the Uri.Builder object
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("maxmagnitude", maxMagnitude);
        uriBuilder.appendQueryParameter("minmagnitude", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

// create loader to load new earthquake data
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakeList) {
        Log.d(LOG_TAG, "TEST : onLoadFinished() called ...");


        // Hide loading indicator because the data has been loaded
        mLoadingIndicatorView.setVisibility(View.GONE);


        if (earthquakeList != null && !earthquakeList.isEmpty()) {
            mAdapter.swap(earthquakeList);
        }else {
            Log.d("null","null null");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
