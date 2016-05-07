package com.example.svilen.p8;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;


public class UserAdapter extends BaseAdapter implements Filterable {

    // Declare Used Variables
    private Activity activity;
    private final List<Map<String, String>> allUsers;
    private List<Map<String, String>> filteredUsers;
    private List<Map<String, String>> allFinalUsers;
    private static LayoutInflater inflater = null;

    // UserAdapter Constructor
    public UserAdapter(Activity activity, List<Map<String, String>> users) {

        // Take passed values
        this.activity = activity;
        this.allUsers = users;
        this.filteredUsers = users;
        this.allFinalUsers = users;

        // Layout inflater to call external xml layout ()
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // What is the size of Passed Arraylist Size
    public int getCount() {

        if (filteredUsers.size() <= 0)
            return 1;
        return filteredUsers.size();
    }

    public Object getItem(int position) {
        return filteredUsers.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setListData(List<Map<String, String>> users) {
        this.filteredUsers = users;
    }

    public void getListData(List<Map<String, String>> users, CharSequence constraint) {
        this.filteredUsers = users;
        notifyDataSetChanged();
        getFilter().filter(constraint);
    }

    // Create a holder Class to contain inflated xml file elements
    public static class ViewHolder {

        public TextView username;
        public TextView firstName;
        public TextView lastName;
        public TextView role;
        public TextView userId;
        public TextView classId;
        public TextView password;
        public TextView email;
        public TextView parentEmail;
    }

    // Depends upon data size called for each row , Create each ListView row
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {

            // Inflate user_listview_itemml file for each row (Defined below)
            view = inflater.inflate(R.layout.user_listview_item, null);

            // View Holder Object to contain user_listview_item.xmlile elements
            holder = new ViewHolder();

            holder.username = (TextView) view.findViewById(R.id.clTvUsername);
            holder.firstName = (TextView) view.findViewById(R.id.clTvFirstName);
            holder.lastName = (TextView) view.findViewById(R.id.clTvLastName);
            holder.role = (TextView) view.findViewById(R.id.clTvRole);
            holder.userId = (TextView) view.findViewById(R.id.clTvUserId);
            holder.classId = (TextView) view.findViewById(R.id.clTvClassId);
            holder.password = (TextView) view.findViewById(R.id.clTvPassword);
            holder.email = (TextView) view.findViewById(R.id.clTvEmail);
            holder.parentEmail = (TextView) view.findViewById(R.id.clTvParentEmail);

            holder.userId.setVisibility(View.GONE);
            holder.classId.setVisibility(View.GONE);
            holder.password.setVisibility(View.GONE);
            holder.email.setVisibility(View.GONE);
            holder.parentEmail.setVisibility(View.GONE);

            // Set holder with LayoutInflater
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            if (filteredUsers.size() <= 0) {
                holder.username.setText("No Data to display!");
                holder.firstName.setText("");
                holder.lastName.setText("");
                holder.role.setText("");
                holder.userId.setText("");
                holder.classId.setText("");
                holder.password.setText("");
                holder.email.setText("");
                holder.parentEmail.setText("");
            } else {
                // Set Model values in Holder elements
                holder.username.setText(filteredUsers.get(position).get("username"));
                holder.firstName.setText(filteredUsers.get(position).get("firstName"));
                holder.lastName.setText(filteredUsers.get(position).get("lastName"));
                holder.role.setText(filteredUsers.get(position).get("role"));
                holder.userId.setText(filteredUsers.get(position).get("userId"));
                holder.classId.setText(filteredUsers.get(position).get("classId"));
                holder.password.setText(filteredUsers.get(position).get("password"));
                holder.email.setText(filteredUsers.get(position).get("email"));
                holder.parentEmail.setText(filteredUsers.get(position).get("parentEmail"));
                Log.d("User added to ListView", String.valueOf(filteredUsers.get(position).get("username")));
            }
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    filteredUsers.clear();
                    filteredUsers.addAll((List<Map<String, String>>) results.values);
                    Log.d("", "Filter successful!");
                    for (int i = 0; i < filteredUsers.size(); i++) {
                        Map<String, String> filteredUser = filteredUsers.get(i);
                        for (Map.Entry<String, String> entry : filteredUser.entrySet()) {
                            Log.d("i/cs for FU entry", i + " " + constraint);
                            Log.d("key/value for FU entry", entry.getKey() + " " + entry.getValue());
                        }
                    }
                    notifyDataSetChanged();
                } else {
                    Log.d("", "Nothing to filter!");
                    notifyDataSetInvalidated();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                // if constraint is empty return the original names
                if (constraint.length() == 0) {
                    //filterResults.values = allUsers;
                    filterResults.values = allFinalUsers;
                    //filterResults.count = allUsers.size();
                    filterResults.count = allFinalUsers.size();

                    Log.d("cs size 0, users size ", Integer.toString(allUsers.size()));
                    return filterResults;
                }

                List<Map<String, String>> tempUsers = new ArrayList<>();

                String filterString = constraint.toString().toLowerCase();
                String filterableString;

                for (int i = 0; i < allFinalUsers.size(); i++) {
                    Log.d("users sz at start loop ", Integer.toString(allFinalUsers.size()));
                    Map<String, String> user = allFinalUsers.get(i);
                    loopCurrentEntry: {
                        for (Map.Entry<String, String> entry : user.entrySet()) {
                            if (entry.getValue() != null) {
                                filterableString = entry.getValue();
                                if (filterableString.toLowerCase().contains(filterString)) {
                                    Log.d("Matched Strings", filterString + " and " + filterableString);
                                    Map<String, String> tempMap;
                                    tempMap = allFinalUsers.get(i);
                                    //Log.d("Filter result CA", entry.getKey() + " " + entry.getValue());
                                    //for (Map.Entry<String, String> filterEntry : user.entrySet()) {
                                    //    tempMap.put(filterEntry.getKey(), filterEntry.getValue());
                                    //    Log.d("Added to filterEntry", filterEntry.getKey() + " " + filterEntry.getValue() + " " + i + " " + constraint);
                                    //}
                                    tempUsers.add(tempMap);
                                    i++;
                                    break loopCurrentEntry;
                                }
                            }
                        }
                    }
                }
                Log.d("users sz at end loop ", Integer.toString(allUsers.size()));
                filterResults.values = tempUsers;
                filterResults.count = tempUsers.size();
                for (int i = 0; i < tempUsers.size(); i++) {
                    Map<String, String> tempUser = tempUsers.get(i);
                    for (Map.Entry<String, String> entry : tempUser.entrySet()) {
                        Log.d("i/cs for TU entry", i + " " + constraint);
                        Log.d("key/value for TU entry", entry.getKey() + " " + entry.getValue());                    }
                }
                return filterResults;
            }
        };
    }
}