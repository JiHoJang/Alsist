package com.example.alsist;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Networkcon {

    static String getASlist() {
        String ASlist="";
        URL url = null;
        HttpURLConnection urlConnection = null;
        int TIMEOUT_VALUE = 2000;
        try {
            url = new URL("http://ec2-52-90-187-218.compute-1.amazonaws.com/AScenter.php");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line = null;
            while((line = bufferedReader.readLine())!=null) {
                ASlist += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return ASlist;

    }

    // SHOP들의 위치를 받아오는 함수
    static String getshoplist() {
        String shoplist="";
        URL url = null;
        HttpURLConnection urlConnection = null;
        int TIMEOUT_VALUE = 2000;
        try {
            url = new URL("http://ec2-52-90-187-218.compute-1.amazonaws.com/marker.php");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);

            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));

            String line = null;
            while((line = bufreader.readLine())!=null) {
                shoplist += line;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            urlConnection.disconnect();
        }
        return shoplist;
    }

    // 지하철 역 위치를 받아오는 함수
    static String getsubwaylist() {
        String subwaylist="";
        URL url = null;
        HttpURLConnection urlConnection = null;
        int TIMEOUT_VALUE = 2000;
        try {

            url = new URL("https://api.wmata.com/Rail.svc/json/jStations");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("api_key","8903a3b03c12473e93a89a4d5ea0a0e3");

            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));

            String line = null;

            while((line = bufreader.readLine())!=null) {
                subwaylist += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return subwaylist;
    }
    // 해당하는 샵의 정보를 받아오는 함수
    static String getshopinfo(int code) {
        String shopinfo = "";
        URL url = null;
        HttpURLConnection urlConnection = null;
        int TIMEOUT_VALUE = 1000;
        try {
            url = new URL("http://ec2-52-90-187-218.compute-1.amazonaws.com/shop.php?shopcode=" + Integer.toString(code));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);

            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));

            String line = null;
            while((line = bufreader.readLine())!=null) {
                shopinfo += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return shopinfo;
    }
    static String getnexttrains() {
        String nexttrains = "";
        URL url = null;
        HttpURLConnection urlConnection = null;
        int TIMEOUT_VALUE = 1000;
        try {

            url = new URL("https://api.wmata.com/StationPrediction.svc/json/GetPrediction/All");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);
            urlConnection.setRequestMethod("GET");

            urlConnection.setRequestProperty("api_key","8903a3b03c12473e93a89a4d5ea0a0e3");

            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));

            String line = null;

            while((line = bufreader.readLine())!=null) {
                nexttrains += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return nexttrains;
    }
    static int[] getminute(String[] from, String[] to) {
        int[] minutenum={0, 0, 0, 0};
        HttpURLConnection urlConnection = null;
        String line = null;
        int TIMEOUT_VALUE = 1000;
        for(int k = 0 ; k < 4 ; k++) {
            if (from[k] == null) {
                minutenum[k] = k;
            } else {
                try {
                    URL url = null;
                    String stringminutes = "";
                    url = new URL("https://api.wmata.com/Rail.svc/json/jSrcStationToDstStationInfo?FromStationCode=" + from[k]
                            + "&ToStationCode=" + to[k]
                            + "&api_key=8903a3b03c12473e93a89a4d5ea0a0e3");

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE);
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);

                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    line = null;
                    while ((line = bufreader.readLine()) != null) {
                        stringminutes += line;
                    }

                    JSONObject json = new JSONObject(stringminutes);

                    JSONObject json2 = json.getJSONArray("StationToStationInfos").getJSONObject(0);

                    minutenum[k] = json2.getInt("RailTime");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            }
        }
        return minutenum;
    }
    static int[] getleft(String[] from, String[] to) {
        int[] leftnum = {0, 0, 0, 0};
        HttpURLConnection urlConnection = null;
        int TIMEOUT_VALUE = 1000;
        for(int k = 0; k < 4; k++) {
            if (from[k] == null) {
                leftnum[k] = 0;
            } else {
                try {
                    URL url = null;
                    String stringleft = "";
                    url = new URL("https://api.wmata.com/Rail.svc/json/jPath?FromStationCode=" + from[k]
                            + "&ToStationCode=" + to[k]
                            + "&api_key=8903a3b03c12473e93a89a4d5ea0a0e3");

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE);
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);

                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                    String line = null;

                    while ((line = bufreader.readLine()) != null) {
                        stringleft += line;
                    }
                    JSONObject json = new JSONObject(stringleft);
                    JSONArray jleft = json.getJSONArray("Path");
                    leftnum[k] = jleft.length() - 1;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            }
        }
        return leftnum;
    }
}
