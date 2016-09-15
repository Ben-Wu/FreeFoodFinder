package ca.benwu.freefoodfinder.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ben Wu on 2016-09-13.
 */
public class InfoSession {
    public String employer;
    public String date;
    public String day;
    @SerializedName("start_time")
    public String startTime;
    @SerializedName("end_time")
    public String endTime;
    public String description;
    public String website;
    public SessionLocation building;
}
