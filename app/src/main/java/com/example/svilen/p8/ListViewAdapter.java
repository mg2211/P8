package com.example.svilen.p8;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caroline on 17-04-2016.
 */
public class ListViewAdapter extends SimpleAdapter {
    ArrayList<Integer> colors = new ArrayList<>();

    public ListViewAdapter(Context context, List<Map<String, String>> items, int resource, String[] from, int[] to, ArrayList<Integer> colors) {
        super(context, items, resource, from, to);
        this.colors = colors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(colors.get(position));
        return view;
    }
}