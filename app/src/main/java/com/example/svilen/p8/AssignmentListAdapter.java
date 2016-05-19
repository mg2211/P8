package com.example.svilen.p8;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

class AssignmentListAdapter extends BaseAdapter {
    private static List<Map<String,String>> assignments;

    private final LayoutInflater inflator;

    public AssignmentListAdapter(Context context, List<Map<String, String>> data) {
        assignments = data;
        inflator = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.assignment_list_item, null);
            holder = new ViewHolder();
            holder.student = (TextView) convertView.findViewById(R.id.tvStudent);
            holder.from = (TextView) convertView.findViewById(R.id.tvFrom);
            holder.to = (TextView) convertView.findViewById(R.id.tvTo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int color;
        if(assignments.get(position).get("isComplete").equals("0")){
            color = Color.BLACK;
        } else {
            color = Color.LTGRAY;
        }

        Long from = Long.parseLong(assignments.get(position).get("availableFrom"));
        Long to = Long.parseLong(assignments.get(position).get("availableTo"));

        Date fromDate = new Date(from*1000);
        Date toDate = new Date(to*1000);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String formattedFromDate = dateFormatter.format(fromDate);
        String formattedToDate = dateFormatter.format(toDate);

        holder.student.setText(assignments.get(position).get("Name"));
        holder.student.setTextColor(color);
        holder.from.setText(formattedFromDate);
        holder.to.setText(formattedToDate);

        return convertView;
    }
    @Override
    public int getCount() {
        return assignments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView student;
        TextView complete;
        TextView from;
        TextView to;
        //etc
    }
}