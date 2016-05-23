package helper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.svilen.p8.R;


import java.util.List;
import java.util.Map;

/**
 * QuestionListAdapter - used in the assignmentActivity for showing detailed statistics
 */
public class QuestionListAdapter extends BaseAdapter {

    /*The data being passed to the listview*/
    private static List<Map<String,String>> questions;
    /*Creating a LayoutInflater*/
    private final LayoutInflater inflater;

    /**
     * Constructor
     * @param context - the caller activity's context
     * @param data - the data being rendered
     */

    public QuestionListAdapter(Context context, List<Map<String, String>> data) {
        questions = data;
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
            convertView = inflater.inflate(R.layout.question_list_item, null);
            holder = new ViewHolder();
            holder.question = (TextView) convertView.findViewById(R.id.tvQuestion);
            holder.answer = (TextView) convertView.findViewById(R.id.tvAnswer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /*The color resource to colour the answer with depending on the corectness*/
        int color;
        if(questions.get(position).get("correct").equals("0")){
            color = Color.RED;
        } else {
            color = Color.GREEN;
        }

        /*Setting the texts and color of various UI elements*/
        holder.question.setText(questions.get(position).get("questionContent"));
        holder.answer.setText(questions.get(position).get("answer"));
        holder.answer.setTextColor(color);
        /*Returns the View*/
        return convertView;
    }
    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*ViewHolder class*/
    static class ViewHolder {
        TextView question;
        TextView answer;
    }
}