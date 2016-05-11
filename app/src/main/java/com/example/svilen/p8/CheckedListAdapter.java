package com.example.svilen.p8;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by EmilSiegenfeldt on 10/05/16.
 */
public class CheckedListAdapter extends SimpleAdapter {

    private final List<Map<String, String>> data;
    HashMap<Integer, String> checked = new HashMap<>();
    public CheckedListAdapter(Context context, List<Map<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.data = data;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final ListView lv = (ListView) parent;

        checked.put(position,data.get(position).get("assigned"));
        Log.d("checked",checked.toString());
        lv.setItemChecked(position, Boolean.parseBoolean(checked.get(position)));

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Item clicked","pos:"+position);
                Log.d("item Checked", checked.get(position));

                if(lv.isItemChecked(position)){
                    checked.put(position,"false");
                } else {
                    checked.put(position,"true");
                }
            }
        });*/

        return view;
    }

/*    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();

            holder.apkName = (TextView) convertView
                    .findViewById(R.id.textView1);
            holder.ck1 = (CheckBox) convertView
                    .findViewById(R.id.checkBox1);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        PackageInfo packageInfo = (PackageInfo) getItem(position);

        Drawable appIcon = packageManager
                .getApplicationIcon(packageInfo.applicationInfo);
        String appName = packageManager.getApplicationLabel(
                packageInfo.applicationInfo).toString();
        appIcon.setBounds(0, 0, 40, 40);
        holder.apkName.setCompoundDrawables(appIcon, null, null, null);
        holder.apkName.setCompoundDrawablePadding(15);
        holder.apkName.setText(appName);
        holder.ck1.setChecked(false);

        if (itemChecked[position])
            holder.ck1.setChecked(true);
        else
            holder.ck1.setChecked(false);

        holder.ck1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (holder.ck1.isChecked())
                    itemChecked[position] = true;
                else
                    itemChecked[position] = false;
            }
        });

        return convertView;*/

}