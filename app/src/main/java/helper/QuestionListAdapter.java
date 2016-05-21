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

public class QuestionListAdapter extends BaseAdapter {
    private static List<Map<String,String>> questions;

    private final LayoutInflater inflator;

    public QuestionListAdapter(Context context, List<Map<String, String>> data) {
        questions = data;
        inflator = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.question_list_item, null);
            holder = new ViewHolder();
            holder.question = (TextView) convertView.findViewById(R.id.tvQuestion);
            holder.answer = (TextView) convertView.findViewById(R.id.tvAnswer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int color;
        if(questions.get(position).get("correct").equals("0")){
            color = Color.RED;
        } else {
            color = Color.GREEN;
        }

        holder.question.setText(questions.get(position).get("questionContent"));
        holder.answer.setText(questions.get(position).get("answer"));
        holder.answer.setTextColor(color);
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

    static class ViewHolder {
        TextView question;
        TextView answer;
    }
}