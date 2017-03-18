package com.example.yazan.earthquakesreports.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yazan.earthquakesreports.R;
import com.example.yazan.earthquakesreports.activities.MainActivity;
import com.example.yazan.earthquakesreports.data.Earthquake;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yazan on 2/6/17.
 */

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder> {


    /** Tag for log messages */
    private static final String LOG_TAG = EarthquakeAdapter.class.getName();

    private static final String LOCATION_SEPARATOR = "of";

    private List<Earthquake> mEarthquakeList;
    private Context mContext;

    // Define listener
    private static OnItemClickListener mListener;

    // listener interface
    public interface OnItemClickListener {
        void onItemClick(Earthquake earthquakeItem);
    }


    // Define the method that allows the  activity to define the listener
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public static class EarthquakeViewHolder extends RecyclerView.ViewHolder {
       //list item
        public TextView mMagnitudeTextView;
        public TextView mLocationOffsetTextView;
        public TextView mPrimaryLocationTextView;
        public TextView mDateTextView;
        public TextView mTimeTextView;

        /**
         * Construct new {@link EarthquakeViewHolder} object
         * */
        public EarthquakeViewHolder(View itemView) {
            super(itemView);

            //lookup and attach earthquake list item
            mMagnitudeTextView = (TextView) itemView.findViewById(R.id.magnitude);
            mLocationOffsetTextView = (TextView) itemView.findViewById(R.id.location_offset);
            mPrimaryLocationTextView = (TextView) itemView.findViewById(R.id.primary_location);
            mDateTextView = (TextView) itemView.findViewById(R.id.date);
            mTimeTextView = (TextView) itemView.findViewById(R.id.time);

        }

        public void bind(final Earthquake earthquakeItem){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(earthquakeItem);
                }
            });
        }
    }



    /**
     * Construct new {@link EarthquakeAdapter} object with list of  Earthquakes
     *
     * @param earthquakesList is ArrayList of earthquakes
     * @param context         context of app
     */
    public EarthquakeAdapter(Context context, List<Earthquake> earthquakesList) {
        this.mContext = context;
        this.mEarthquakeList = earthquakesList;

    }


    public void clear() {
        int size = this.mEarthquakeList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mEarthquakeList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }


    public void swap(List<Earthquake> earthquakeList){
        mEarthquakeList.clear();
        mEarthquakeList.addAll(earthquakeList);
       notifyDataSetChanged();
    }

    @Override
    public EarthquakeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //get Context
        Context context = parent.getContext();

        //inflate earthquake list item , then passing to the EarthquakeViewHolder
        View earthquakeView = LayoutInflater.from(context).inflate(R.layout.earthquake_list_item, parent, false);
        EarthquakeViewHolder earthquakeViewHolder = new EarthquakeViewHolder(earthquakeView);

        return earthquakeViewHolder;
    }

    @Override
    public void onBindViewHolder(EarthquakeViewHolder holder, int position) {
        Log.i(LOG_TAG, "TEST : onBindViewHolder() called ...");

        //get the correct Earthquake data for the correct click position
        holder.bind(mEarthquakeList.get(position));

        // get the {@link Earthquake} object based on the position
        Earthquake currentEarthquake = mEarthquakeList.get(position);

        // Display the magnitude of the current earthquake
        holder.mMagnitudeTextView.setText(String.valueOf(currentEarthquake.getMagnitude()));

        //get mMagnitudeTextView background
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.mMagnitudeTextView.getBackground();

        // Set the proper background color on the magnitude circle
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);

        //get current magnitude location
        String originalLocation = currentEarthquake.getLocation();
        String locationOffset;
        String primaryLocation;

        //check if the originalLocation contains "of"
        if (originalLocation.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalLocation.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];

        } else {
            locationOffset = "near the";
            primaryLocation = originalLocation;
        }

        //display location Offset and primary Location
        holder.mLocationOffsetTextView.setText(locationOffset);
        holder.mPrimaryLocationTextView.setText(primaryLocation);

        //create Date object from current earthquake unix time in millisecond
        Date date = new Date(currentEarthquake.getTimeInMilliseconds());

        //display formatted date of current earthquake
        String formatDate = formatDate(date);
        holder.mDateTextView.setText(formatDate);

        //display formatted time of current earthquake
        String formatTime = formatTime(date);
        holder.mTimeTextView.setText(formatTime);


    }

    @Override
    public int getItemCount() {
        return mEarthquakeList.size();
    }

    /**
     * Helper method for {@link #bindViewHolder(RecyclerView.ViewHolder, int)}
     * <p>
     * get the proper background color on the magnitude circle
     * depending on earthquake magnitude.
     *
     * @param magnitude is the earthquake magnitude.
     * @return background color magnitude circle
     */
    private int getMagnitudeColor(double magnitude) {

        //hold the color value
        int magnitudeColorResourceId;

        //round the magnitude , then omit floating point
        int magnitudeFloor = (int) Math.floor(magnitude);

        //choose correct color for the proper earthquake magnitude
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(mContext, magnitudeColorResourceId);
    }

    /**
     * Helper method for {@link #bindViewHolder(RecyclerView.ViewHolder, int)}
     * <p>
     * format the  given unix time of current earthquake
     *
     * @param date is Date object in millisecond unix time
     * @return formatted time (i.e. "4:30 PM")
     */
    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(date);


    }

    /**
     * Helper method for {@link #bindViewHolder(RecyclerView.ViewHolder, int)}
     * <p>
     * format the  given unix time of current earthquake
     *
     * @param date is Date object in millisecond unix time
     * @return formatted date (i.e. "2016.07.04")
     */
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(date);


    }



}
