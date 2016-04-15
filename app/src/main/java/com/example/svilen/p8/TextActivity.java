package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    Button bAddText;
    Button bDelete;
    Button bSave;
    EditText etTextName;
    String tvId;
    TextView tvComplexity;
    EditText etSearch;
    String textContent = "";
    String textId;
    boolean newText = false;
    boolean textChanged = false;
    double lix = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        bSave = (Button) findViewById(R.id.bSave);
        lvTexts = (ListView) findViewById(R.id.lvTexts);
        etContent = (EditText) findViewById(R.id.etContent);
        bAddText = (Button) findViewById(R.id.bAddText);
        bDelete = (Button) findViewById (R.id.bDelete);
        etTextName = (EditText) findViewById(R.id.etTextname);
        tvComplexity = (TextView) findViewById(R.id.tvComplexity);
        etSearch = (EditText) findViewById(R.id.etSearch);

        getTexts();

        bAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textChanged == true){
                    confirmChanges();
                }
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newText == true){
                    new TempTextTask(context).executeTask("create","",etTextName.getText().toString(),etContent.getText().toString(), lix);
                } else {
                    new TempTextTask(context).executeTask("update", textId, etTextName.getText().toString(),etContent.getText().toString(),lix);
                }
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
                                //delete text
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        textAdapter = new SimpleAdapter(this, textList, android.R.layout.simple_list_item_1, new String [] {"textname"}, new int[] {android.R.id.text1}); //text1 = the text within the listView
        lvTexts.setAdapter(textAdapter);

        lvTexts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(textChanged == true) {
                    confirmChanges();
                }

                Map<String, String> textData = textList.get(position);
                textContent = textData.get("textcontent");
                String textName = textData.get("textname");

                etContent.setText(textContent);
                etTextName.setText(textName);
                textId = textData.get("id");
                calculate();
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
                String content = etContent.getText().toString();
                if(!content.equals("") && !content.equals(textContent)) {
                    textChanged = true;
                } else {
                    textChanged = false;
                }
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
                lix = (O / P) + (L * 100 / O);
                tvComplexity.setText("Complexity: " + String.valueOf(lix));
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
        public void confirmChanges(){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("You have unsaved changes. Save before continuing?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //save data from current update

                        if(newText == true){
                           new TempTextTask(context).executeTask("create","",etTextName.getText().toString(),etContent.getText().toString(), lix);
                        } else {
                            new TempTextTask(context).executeTask("update", textId, etTextName.getText().toString(),etContent.getText().toString(),lix);
                        }

                        etContent.setText("");
                        etTextName.setText("");
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Discard changes", "yes");
                        etContent.setText("");
                        etTextName.setText("");
                    }
                })
                .show();
    }
    }


























