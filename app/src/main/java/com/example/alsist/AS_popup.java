package com.example.alsist;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class AS_popup extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_as_popup);
        String telephonenum = getIntent().getStringExtra("telephonenum");
        String name = getIntent().getStringExtra("ASname");
        TextView textView1 = (TextView) findViewById(R.id.name);
        textView1.setText(name);
        TextView textView2 = (TextView) findViewById(R.id.TelNumber);
        textView2.setText(telephonenum);

        ImageButton call = (ImageButton) findViewById(R.id.imageButton2);
        call.setOnClickListener(this);
        ImageButton sendSit = (ImageButton) findViewById(R.id.imageButton5);
        sendSit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton2:
                final EditText telNum = (EditText) findViewById(R.id.TelNumber);
                dialContactPhone(telNum.getText().toString());
                break;

            case R.id.imageButton5:
                LatLng latLng = ((MyApplication) AS_popup.this.getApplication()).getUserLocation();
                // insert functions for sending user information
                break;

            default:
                break;
        }
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }
}
