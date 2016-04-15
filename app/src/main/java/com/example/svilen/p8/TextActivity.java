package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextActivity extends AppCompatActivity {

    EditText etContent;
    String textname = "TestText";
    Context context = this;
    ListView lvTexts;
    List<Map<String, String>> textList = new ArrayList<>();
    SimpleAdapter textAdapter;
    Button bUpdate;
    Button bCreateText;
    Button bDelete;
    Button bSave;
    EditText tvTextName;
    String tvId;
    TextView tvComplexity;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        bSave = (Button) findViewById(R.id.bSave);
        lvTexts = (ListView) findViewById(R.id.lvTexts);
        etContent = (EditText) findViewById(R.id.etContent);
        bUpdate = (Button) findViewById(R.id.bUpdate);
        bCreateText = (Button) findViewById(R.id.bCreateText);
        bDelete = (Button) findViewById (R.id.bDelete);
        tvTextName = (EditText) findViewById(R.id.tvTextname);
        tvId = "";
        tvComplexity = (TextView) findViewById(R.id.tvComplexity);
        etSearch = (EditText) findViewById(R.id.etSearch);

        getTexts();


        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Id = tvId;
                String tContent = etContent.getText().toString();
                String textName = tvTextName.getText().toString();
                new UpdateTextTask(context).execute(Id, tContent, textName);
                Log.d("UPDATE UPDATE", Id);

                Intent refresh = new Intent(TextActivity.this, TextActivity.class);
                startActivity(refresh);
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to delete the text " + textname)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("delete confirm", "yes");
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        /*bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   String dTextName = getText(et)

                String dText = tvTextName.getText().toString();
                String dId = tvId.getText().toString();
                String dContent = String.valueOf(etContent);
                int ble = 4;
                new DeleteTextTask(context).execute(dId);
                Log.d("DELETEDELETE", dId);
                Intent refresh = new Intent(TextActivity.this, TextActivity.class);
                startActivity(refresh);

            }
        });*/

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName = tvTextName.getText().toString();
                String textContent = etContent.getText().toString();

                if (!textName.equals("") && !textContent.equals("")){
                    new CreateTextTask(context).execute(textName, textContent);
                }else {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "Please fill all required fields";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }
                Intent refresh = new Intent(TextActivity.this, TextActivity.class);
                startActivity(refresh);
            }
        });






textAdapter = new SimpleAdapter(this,
                textList,
                android.R.layout.simple_list_item_1,
                new String [] {"textname"},
                new int[] {android.R.id.text1}); //text1 = the text within the listView
        lvTexts.setAdapter(textAdapter);

        lvTexts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> textData = textList.get(position);
                String textContent = textData.get("textcontent");
                String textName = textData.get("textname");
                etContent.setText(textContent);
                tvTextName.setText(textName);
                calculate();
            }
        });

        bCreateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // tvTextName.setText("New text");
               // etContent.setText("New text");
            }
        });


        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculate();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Auto generated stub
            }
        });
    }
        public void calculate(){
            String inputText = etContent.getText().toString();
            String cleanText;
            int P = StringUtils.countMatches(inputText, ".");
            P = P + StringUtils.countMatches(inputText, "?");
            P = P + StringUtils.countMatches(inputText, "!");
            P = P + StringUtils.countMatches(inputText, ":");
            P = P + StringUtils.countMatches(inputText, ";");
            cleanText = inputText.replaceAll("/./", "");
            cleanText = cleanText.replaceAll("/?/", "");
            cleanText = cleanText.replaceAll("/!/", "");
            cleanText = cleanText.replaceAll("/:/", "");
            cleanText = cleanText.replaceAll("/;/", "");
            String[] words = cleanText.split("\\s+");
            int O = words.length;
            if (O > 0 && P > 0) {
                int L = 0;
                for (int i = 0; i < words.length; i++) {
                    if (words[i].length() > 6) {
                        L++;
                    }
                }
                Log.d("O", String.valueOf(O));
                Log.d("P", String.valueOf(P));
                Log.d("L", String.valueOf(L));
                double lix = (O / P) + (L * 100 / O);
                tvComplexity.setText("Complexity: " + String.valueOf(lix));
                Log.d("lix:", String.valueOf(lix));
            } else {
                tvComplexity.setText("Complexity: Unable to calculate");
            }
        }
        public void getTexts(){
            new TextTask(new TextCallback() {
                @Override
                public void textListDone(HashMap<String, HashMap<String, String>> texts) {
                    for (Map.Entry<String, HashMap<String, String>> text : texts.entrySet()){

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
                    }
                    textAdapter.notifyDataSetChanged();
                }
            }, context).execute(""); //Nothing within "" to get every text - see php script
        }

    }


























