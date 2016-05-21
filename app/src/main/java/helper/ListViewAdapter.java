package helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class ListViewAdapter extends SimpleAdapter {
    private ArrayList<Integer> colors = new ArrayList<>();

    public ListViewAdapter(Context context, List<Map<String, String>> items, String[] from, int[] to, ArrayList<Integer> colors) {
        super(context, items, android.R.layout.simple_list_item_2, from, to);
        this.colors = colors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(colors.get(position));
        return view;
    }
}