package com.example.alsist;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class shop_popup extends AppCompatActivity implements listviewadapter.ListBtnClickListener{
    int shopcode = 0;

    JSONArray alsistlist;
    String[] code ={"","","","",""};
    String[] battery ={"","","","",""};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_popup);

        shopcode = getIntent().getIntExtra("shopcode", 0);
        String name = getIntent().getStringExtra("name") + " Rent Shop";
        TextView textView = (TextView) findViewById(R.id.shopname);
        textView.setText(name);
        Thread threadinfo = new Thread() {
            @Override
            public void run() {
                try {
                    JSONObject jsonshop = new JSONObject(Networkcon.getshopinfo(shopcode));
                    alsistlist = jsonshop.getJSONArray("allist");
                } catch (Exception e) {}
            }
        };
        threadinfo.start();
        try {
          threadinfo.join();
        } catch (Exception e) {}

        for (int i = 0 ; i < alsistlist.length(); i++) {
            try {
                code[i] = alsistlist.getJSONObject(i).getString("alsistnum");
                battery[i] = alsistlist.getJSONObject(i).getString("battery");
            } catch (Exception e) {
            }
        }

        ListView listView = (ListView) findViewById(R.id.alsistlist);
        ArrayList<listview_alsist> items = new ArrayList<listview_alsist>();
        for (int i = 0 ; i < alsistlist.length(); i++) {
            double distance = Double.parseDouble(battery[i])/5;
            listview_alsist item;
            item = new listview_alsist();
            item.setcode("Alsist code : " + code[i]);
            item.setnumber(Integer.toString(i+1));
            item.setbattery("Battery : " + battery[i] + "%");
            item.setdistance("Distance : " + Double.toString(distance) + "km");
            items.add(item);
        }
        listviewadapter myadapter = new listviewadapter(this, R.layout.activity_listview_alsist, items, this);
        listView.setAdapter(myadapter);
    }

        @Override
        public void onListBtnClick(int position) {
            ((MyApplication) shop_popup.this.getApplication()).setUserRent(true);
            MapsActivity.battery.setVisibility(View.VISIBLE);
            Toast.makeText(shop_popup.this, "Success to Rent!", Toast.LENGTH_SHORT).show();
            finish();
        }

    Handler handlersinfo = new Handler(Looper.getMainLooper()) {
        // @Override
        public void handleMessage(Message msg1) {
            Bundle bun = msg1.getData();
            String shoplist = bun.getString("shopinfo");
            try {
                JSONObject jsonshop = new JSONObject(shoplist);
                alsistlist = jsonshop.getJSONArray("allist");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
