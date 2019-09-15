package com.example.alsist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import  java.util.ArrayList;

public class listviewadapter extends ArrayAdapter implements View.OnClickListener {
    public interface ListBtnClickListener {
        void onListBtnClick(int position);
    }
    private ListBtnClickListener listBtnClickListener;
    int resourceId;
    listviewadapter(Context context, int resource, ArrayList<listview_alsist> list, ListBtnClickListener clickListener) {
        super(context, resource, list) ;
        this.resourceId = resource;
        this.listBtnClickListener = clickListener ;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resourceId, parent, false);
        }
        TextView numberTextView = (TextView) convertView.findViewById(R.id.number) ;
        TextView codeTextView = (TextView) convertView.findViewById(R.id.code) ;
        TextView batteryTextView = (TextView) convertView.findViewById(R.id.battery);
        TextView distanceTextView = (TextView) convertView.findViewById(R.id.distance) ;
        ImageButton button1 = (ImageButton) convertView.findViewById(R.id.button1);

        listview_alsist listview_alsist1 = (listview_alsist) getItem(position);

        numberTextView.setText(listview_alsist1.getnumber());
        codeTextView.setText(listview_alsist1.getcode());
        batteryTextView.setText(listview_alsist1.getbattery());
        distanceTextView.setText(listview_alsist1.getdistance());
        button1.setTag(position);
        button1.setOnClickListener(this);
        return convertView;
    }

    public void onClick(View v) {
            if(this.listBtnClickListener != null) {
            this.listBtnClickListener.onListBtnClick((int)v.getTag());
        }
    }

}
