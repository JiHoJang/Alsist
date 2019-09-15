package com.example.alsist;

/**
 * Created by mrhjs on 2018-05-13.
 */

public class listview_subway_item {
    private String to;
    private String linecode;
    private String stationnum;
    private String min;
    public void setto(String totemp) {
        to = totemp;
    }
    public void setLinecode(String lc) {
        linecode = lc;
    }
    public void setStationnum(String num) {
        stationnum = num;
    }
    public void setMin(String mintemp) {
        min = mintemp;
    }
    public String getTo() {
        return this.to;
    }
    public String getLinecode() {
        return this.linecode;
    }
    public String getStationnum() {
        return this.stationnum;
    }
    public String getMin() {
        return this.min;
    }
}
