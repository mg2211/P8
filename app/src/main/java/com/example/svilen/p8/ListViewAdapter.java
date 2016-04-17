package com.example.svilen.p8;

import android.content.Context;
import android.util.Log;
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
    ListViewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        for(int i=0; i<=data.size(); i++){
           Log.d("...", String.valueOf(data.get(i)));
        }
    }


}
