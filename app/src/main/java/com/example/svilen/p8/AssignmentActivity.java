package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentActivity extends AppCompatActivity {

    Button bGetText;
    SimpleAdapter textAdapter;
    List<Map<String, String>> textList = new ArrayList<>();
    Context context = this;
    ListView lvTextToAss;
    TextView tvTextChosen;
    TextView tvTextId;
    EditText etAssName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        bGetText = (Button) findViewById(R.id.bGetText);
        tvTextChosen = (TextView) findViewById(R.id.tvTextChosen);
        tvTextId = (TextView) findViewById(R.id.tvTextId);
        etAssName = (EditText) findViewById(R.id.etAssName);

        bGetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.assignment_dialog, null);

                lvTextToAss = (ListView) layout.findViewById(R.id.lvTextToAss);
                builder.setView(layout);

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                textAdapter = new SimpleAdapter(context,
                        textList,
                        android.R.layout.simple_list_item_1,
                        new String[]{"textname"},
                        new int[]{android.R.id.text1}); //text1 = the text within the listView
                lvTextToAss.setAdapter(textAdapter);
                lvTextToAss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Map<String, String> textData = textList.get(position);
                        String textName = textData.get("textname");
                        String textId = textData.get("id");
                        tvTextChosen.setText(textName);
                        tvTextId.setText(textId);

                    }
                });

                new TextTask(new TextCallback() {
                    @Override
                    public void textListDone(HashMap<String, HashMap<String, String>> texts) {
                        for (Map.Entry<String, HashMap<String, String>> text : texts.entrySet()) {

                            Map<String, String> textInfo = new HashMap<>();
                            String textId = text.getValue().get("id");
                            String textName = text.getValue().get("textname");
                            String textContent = text.getValue().get("textcontent");
                            String textBook = text.getValue().get("textbook");
                            String complexity = text.getValue().get("complexity");
                            textInfo.put("textname", textName);
                            textInfo.put("textcontent", textContent);
                            textInfo.put("textbook", textBook);
                            textInfo.put("complexity", complexity);
                            textInfo.put("id", textId);
                            textList.add(textInfo);
                            Log.d("TEXTTASK", "CHECK");
                        }
                        textAdapter.notifyDataSetChanged();
                    }
                }, context).execute(""); //Nothing within "" to get every text - see php script*/

            }
        });








    }
}
