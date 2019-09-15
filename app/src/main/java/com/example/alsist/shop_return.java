package com.example.alsist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class shop_return extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_return);
        ImageButton returnbutton = (ImageButton) findViewById(R.id.return1);
        returnbutton.setOnClickListener(this);
        String name = getIntent().getStringExtra("name1") + " Rent Shop";
        TextView textView = (TextView) findViewById(R.id.shop);
        textView.setText(name);
    }

    @Override
    public void onClick(View v) {
        ((MyApplication) shop_return.this.getApplication()).setUserRent(false);
        MapsActivity.battery.setVisibility(View.INVISIBLE);
        Toast.makeText(shop_return.this, "Success to Return!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

