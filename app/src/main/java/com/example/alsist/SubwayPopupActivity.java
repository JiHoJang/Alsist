package com.example.alsist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class SubwayPopupActivity extends AppCompatActivity {
    JSONArray jArr2 = null; // 지하철의 실시간 목적지가 담긴 json 형태의 배열
    JSONObject json;
    JSONArray jArr;
    String[] from = {"", "", "", ""};
    String[] to = {"", "", "", ""};
    String[] toname = {"", "", "", ""};
    String[] line = {"", "", "", ""};
    String now = "";
    int[] trainleft;
    int[] trainmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subway_popup);
        final Intent intent = getIntent();
        try {
            json = new JSONObject(getIntent().getStringExtra("json"));
            jArr = json.getJSONArray("Stations");
        } catch (Exception e) {
        }
        Thread thread1 = new Thread() {
            public void run() {
                String updatetitle = intent.getStringExtra("markertitle");
                String nexttrains = intent.getStringExtra("nexttrains");
                int value = 0;
                int term = 0;
                try {
                    JSONObject json3 = new JSONObject(nexttrains);
                    jArr2 = json3.getJSONArray("Trains");
                    for(int i = 0 ; i < jArr.length();i++) {
                        JSONObject json1 = jArr.getJSONObject(i);
                        String temp1 = json1.getString("Name");
                        if (updatetitle.equals(temp1)) {
                            value = i;
                            break;
                        }
                    }
                    JSONObject json1 = jArr.getJSONObject(value);
                    for(int j = 0; j <jArr2.length();j++) {
                        JSONObject json2 = jArr2.getJSONObject(j);
                        if((json1.getString("LineCode1").equals(json2.getString("Line"))) && (term < 4)) {
                            from[term] = json1.getString("Code");
                            now = json1.getString("Name");
                            to[term] = json2.getString("LocationCode");
                            toname[term] = json2.getString("DestinationName");
                            line[term] = json2.getString("Line");
                            term++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
        try {
            thread1.join();
        } catch(Exception e) {}
        Thread thread2 = new Thread() {
            public void run() {
                trainmin = Networkcon.getminute(from, to);
                trainleft = Networkcon.getleft(from, to);
            }
        };
        thread2.start();
        try{
            thread2.join();
        } catch (Exception e){}
        TextView textView = (TextView) findViewById(R.id.subname);
        textView.setText(intent.getStringExtra("markertitle") + " Station");
        ListView listView = (ListView) findViewById(R.id.subwaylist);
        subwaydapter adapter = new subwaydapter();

        for(int i = 0 ; i < 4; i++){
            adapter.addItem("To " + toname[i], line[i], Integer.toString(trainleft[i]) + " stations left", Integer.toString(trainmin[i]) + " minutes left");
        }
        listView.setAdapter(adapter);
    }
}

