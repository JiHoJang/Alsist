package com.example.alsist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class subwaydapter extends BaseAdapter {

    private ArrayList<listview_subway_item> listviewitemlist = new ArrayList<listview_subway_item>();

    public subwaydapter() {
    }
    @Override
    public int getCount() {
        return listviewitemlist.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_subway, parent, false);
        }

        TextView toTextView = (TextView) convertView.findViewById(R.id.to);
        TextView lcTextView = (TextView) convertView.findViewById(R.id.linecode);
        TextView numTextView = (TextView) convertView.findViewById(R.id.stationnum);
        TextView minTextView = (TextView) convertView.findViewById(R.id.min);

        listview_subway_item listViewItem = listviewitemlist.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        toTextView.setText(listViewItem.getTo());
        lcTextView.setText(listViewItem.getLinecode());
        numTextView.setText(listViewItem.getStationnum());
        minTextView.setText(listViewItem.getMin());
        return convertView;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Object getItem(int position) {
        return listviewitemlist.get(position);
    }
    public void addItem(String to, String lc, String num, String min) {
        listview_subway_item item = new listview_subway_item();

        item.setto(to);
        item.setLinecode(lc);
        item.setStationnum(num);
        item.setMin(min);

        listviewitemlist.add(item);
    }
}
