package helper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.svilen.p8.R;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * AssignmentListAdapter - Custom ListAdapter
 * Used for showing assignments in the assignmentDialog in the assignmentActivity
 *
 */
public class AssignmentListAdapter extends BaseAdapter {
    /*Data to be rendered*/
    private static List<Map<String,String>> assignments;

    /*Layout inflater*/
    private final LayoutInflater inflater;

    /**
     * Constructor
     * @param context Caller activity context
     * @param data
     */
    public AssignmentListAdapter(Context context, List<Map<String, String>> data) {
        assignments = data;
        inflater = LayoutInflater.from(context);
    }
    /**
     *
     * @param position position in the listview to be converted
     * @param convertView ConvertView
     * @param parent Parent ListView
     * @return View - the converted listView item
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        /*Creating a new ViewHolder*/
        ViewHolder holder;
        /*If the listItem is new the layout is inflated and the ViewHolder is being populated with the UI elements*/
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.assignment_list_item, null);
            holder = new ViewHolder();
            holder.student = (TextView) convertView.findViewById(R.id.tvStudent);
            holder.from = (TextView) convertView.findViewById(R.id.tvFrom);
            holder.to = (TextView) convertView.findViewById(R.id.tvTo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /*The color to color the name of the student depending on assignment completion*/
        int color;
        if(assignments.get(position).get("isComplete").equals("1")){
            color = Color.GREEN;
        } else {
            color = Color.RED;
        }

        /*Available from and to in unix timestamps*/
        Long from = Long.parseLong(assignments.get(position).get("availableFrom"));
        Long to = Long.parseLong(assignments.get(position).get("availableTo"));

        /*Creating new Date objects with the timestamps converted to milliseconds*/
        Date fromDate = new Date(from*1000);
        Date toDate = new Date(to*1000);

        /*Formatting the date*/
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String formattedFromDate = dateFormatter.format(fromDate);
        String formattedToDate = dateFormatter.format(toDate);

        /*Setting the UI elements*/
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
        TextView from;
        TextView to;
    }
}