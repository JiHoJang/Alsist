package com.example.alsist;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;


public class My extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        TextView idtext = (TextView) findViewById(R.id.id);
        TextView pwtext = (TextView) findViewById(R.id.pw);
        idtext.setText(getIntent().getStringExtra("ID"));
        pwtext.setText(getIntent().getStringExtra("PW"));

    }
}
