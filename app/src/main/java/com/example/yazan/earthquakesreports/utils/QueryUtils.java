package com.example.yazan.earthquakesreports.utils;

/**
 * Created by yazan on 2/5/17.
 */

import android.text.TextUtils;
import android.util.Log;

import com.example.yazan.earthquakesreports.data.Earthquake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link QueryUtils} class contains static helper methods related to requesting and receiving earthquake data from USGS website
 */
public final class QueryUtils {

    /**
     * Tag for log message
     */
    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * private constructor no need to construct new {@link QueryUtils} object
     */
    private QueryUtils() {
    }


    /**
     * Query the USGS dataset and return a list of {@link Earthquake} objects.
     *
     * @param requestUrl is a USGS URL for requesting Earthquakes
     * @return a list of Earthquakes for given URL
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        //create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the given URL and receive JSON String response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem in making the HTTP request ...", e);

        }

        //extract the relevant fields from JSON response and create a list of {@link Earthquake}
        List<Earthquake> earthquakes = extractFeatureFromJSON(jsonResponse);

        //Return the list of {@link Earthquake}s
        return earthquakes;
    }


    /**
     * Helper method for {@link #fetchEarthquakeData(String)}
     * <p>
     * create list of {@link Earthquake} objects that has been built up from
     * parsing given JSON response.
     *
     * @param earthquakeJSON is a String JSON response
     * @return list of {@link Earthquake} objects
     */
    private static List<Earthquake> extractFeatureFromJSON(String earthquakeJSON) {
        Log.i(LOG_TAG, "TEST : extractFeatureFromJson() called ...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //check if the JSON response is empty or null, then exit.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        //create empty ArrayList
        List<Earthquake> earthquakeList = new ArrayList<>();

        try {
            //create JSON Object from JSON response string
            JSONObject baseJSONResponse = new JSONObject(earthquakeJSON); // throw JSONException

            // extract JSONArray associated with key called "features"
            // which represents a list of features (or earthquakes).
            JSONArray earthquakeArray = baseJSONResponse.getJSONArray("features");

            //for each Earthquake in earthquakeArray , create an {@link Earthquake} object
            for (int i = 0; i < earthquakeArray.length(); i++) {
                // get single Earthquake at position i within the list of earthquakeArray(earthquakes)
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that earthquake.
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // Extract the value for the key called "mag"
                double magnitude = properties.getDouble("mag");

                // Extract the value for the key called "place"
                String location = properties.getString("place");

                // Extract the value for the key called "time"
                long time = properties.getLong("time");

                // Extract the value for the key called "url"
                String url = properties.getString("url");

                // create new {@link Earthquake} object with the magnitude, location, time,
                // and url from the JSON response.
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);

                // Add the new {@link Earthquake} to the list of earthquakes.
                earthquakeList.add(earthquake);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results ...", e);
        }


        return earthquakeList;
    }


    /**
     * Helper method for {@link #fetchEarthquakeData(String)}
     * <p>
     * return new URL object from the given String URL
     *
     * @param stringUrl is the given url string
     * @return new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Problem with building the URL object", e);
        }
        return url;
    }

    /**
     * Helper method for  {@link #fetchEarthquakeData(String)}
     * <p>
     * Make an HTTP request to the given URL and return a String as the response .
     *
     * @param url is the given URL object
     * @return JSON String as response
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = null;

        //if the URL is null , then exit
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            //setup HTTP request
            urlConnection = (HttpURLConnection) url.openConnection(); //throw IOException
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //check if request was successful , then read the input stream
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem in retrieving the earthquake JSON results ...", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close(); //throws IOException
            }
        }

        return jsonResponse;

    }

    /**
     * Helper method for {@link #makeHttpRequest(URL)}
     * <p>
     * Convert the {@link InputStream} object into String contain whole JSON
     * response from the server.
     *
     * @return JSON response String from InputStream object
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        //check if the inputStream has the response data
        if (inputStream != null) {

            //create character streams from JSON InputStream response
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //read line of JSON response from BufferedReader
            String line = bufferedReader.readLine(); //throws IOException

            //check if the line  not null , then append to StringBuilder
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();

            }

        }
        //return JSON response String
        return output.toString();
    }


}
