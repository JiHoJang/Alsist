package com.example.alsist;

public class listview_alsist {
    private String codeStr;
    private String batStr;
    private String number;
    private String distance;
    public void setcode(String title) {
        codeStr = title ;
    }
    public void setbattery(String desc) {
        batStr = desc ;
    }
    public void setnumber(String num) {
        number = num ;
    }
    public void setdistance(String dist) {
        distance = dist ;
    }
    public String getnumber() {
        return this.number ;
    }
    public String getcode() {
        return this.codeStr ;
    }
    public String getbattery() {
        return this.batStr ;
    }
    public String getdistance() {
        return this.distance ;
    }

}