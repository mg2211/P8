package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextActivity extends AppCompatActivity {

    EditText etContent;

    Context context = this;
    ListView lvTexts;
    List<Map<String, String>> textList = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();
    ListViewAdapter textAdapter;
    Button bAddText;
    Button bDelete;
    Button bSave;
    EditText etTextName;
    TextView tvComplexity;
    EditText etSearch;
    String textContent;
    String textId;
    String textName;
    boolean newText;
    boolean changed;
    double lix;
    boolean clear;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
        bDelete.setEnabled(false);
        setNewText(true);
        setChanged(false);
        getTexts();

        new QuestionTask(new QuestionCallback() {
            @Override
            public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {

            }
        },context).executeTask("get","","","","");

        textAdapter = new ListViewAdapter(this, textList, android.R.layout.simple_list_item_2, new String [] {"textname", "complexity"}, new int[] {android.R.id.text1, android.R.id.text2}, colors);

        lvTexts.setAdapter(textAdapter);
        lvTexts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                view.setBackgroundColor(Color.BLUE);
                Log.d("posistion", String.valueOf(position));
                if(changed == true){

                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if(dialogResponse == true){
                                if(newText == true){
                                    if(createText()){
                                        clear = true;
                                        setChanged(false);
                                        setNewText(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                                if(changed == true){
                                    if(updateText()){
                                        clear = true;
                                        setChanged(false);
                                        setNewText(false);
                                    } else {
                                        clear = false;
                                    }
                                }

                            } else {
                                clear = true;
                            }
                            if(clear == true){
                                setContentPane(position);
                            }
                        }
                    });

                } else {
                    setContentPane(position);
                }
            }
        });


        bAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changed){
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if(dialogResponse == true){
                                if(newText == true){
                                    if(createText()){
                                        clear = true;
                                        setChanged(false);
                                        setNewText(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                                if(changed == true){
                                    if(updateText()){
                                        clear = true;
                                        setChanged(false);
                                        setNewText(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                            } else {
                                clear = true;
                            }
                            if(clear){
                                setContentPane(-1);
                            }
                        }
                    });
                } else {
                    setContentPane(-1);
                }



            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etTextName.getText().toString().equals("") && !etContent.getText().toString().equals("")){
                    if(newText == true){
                        createText();
                        setChanged(false);
                    } else {
                        updateText();
                        setChanged(false);
                    }
                } else {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "Please fill in all required fields";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }
                getTexts();
            }
        });


        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteText();
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
                if (!content.equals("") && !content.equals(textContent)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });
        etTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = etTextName.getText().toString();
                if (!name.equals("") && !name.equals(textName)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                tvComplexity.setText(String.valueOf(lix));
            } else {
                tvComplexity.setText("");
            }
        }
        public void getTexts(){
            new TextTask(new TextCallback() {
                @Override
                public void textListDone(HashMap<String, HashMap<String, String>> texts) {
                    textList.clear();
                    colors.clear();
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
                        textInfo.put("complexity", "Complexity: "+complexity);
                        textInfo.put("id", textId);
                        textList.add(textInfo);
                        double difficulty = Double.parseDouble(complexity);
                        if(difficulty > 0 && difficulty <= 20){
                            colors.add(Color.rgb(156, 204, 101));
                        } else if(difficulty > 20 && difficulty <= 40){
                            colors.add(Color.rgb(255, 235, 69));
                        } else if(difficulty > 40){
                            colors.add(Color.rgb(239, 83, 80));
                        } else {
                            colors.add(Color.TRANSPARENT);
                        }
                    }
                    textAdapter.notifyDataSetChanged();
                }
            }, context).execute(""); //Nothing within "" to get every text - see php script
        }
        public boolean createText(){
            if(!etTextName.getText().toString().equals("") && !etContent.getText().toString().equals("")){
                new TempTextTask(context).executeTask("create","",etTextName.getText().toString(),etContent.getText().toString(), lix);
                getTexts();
                return true;
            } else {
                int duration = Toast.LENGTH_LONG;
                CharSequence alert = "Please fill in all required fields";
                Toast toast = Toast.makeText(context, alert, duration);
                toast.show();
                return false;
            }

        }
        public boolean updateText(){
            if(!etTextName.getText().toString().equals("") && !etContent.getText().toString().equals("")){
                new TempTextTask(context).executeTask("update", textId, etTextName.getText().toString(),etContent.getText().toString(),lix);
                getTexts();
                return true;
            } else {
                int duration = Toast.LENGTH_LONG;
                CharSequence alert = "Please fill in all required fields";
                Toast toast = Toast.makeText(context, alert, duration);
                toast.show();
                return false;
            }

        }
        public void deleteText(){
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete the text " + textName)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new TempTextTask(context).executeTask("delete",textId,"","",0);
                            getTexts();
                            etContent.setText("");
                            etTextName.setText("");
                            calculate();
                            Log.d("textname", textName);
                            Log.d("textId", textId);
                            setNewText(true);
                            setChanged(false);
                            bDelete.setEnabled(false);
                            textId = "";
                            textName = "";

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }
        public void setNewText(boolean value){
            newText = value;
            Log.d("new text value", String.valueOf(newText));
        }
        public void setChanged(boolean value){
            changed = value;
            Log.d("Changed value", String.valueOf(changed));
        }
        public void confirm(final DialogCallback callback){
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirm")
                    .setMessage("You have unsaved changes - Save before continuing?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.dialogResponse(true);
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.dialogResponse(false);

                        }
                    })
                    .show();

        }

        //@param position - the position from the listview - pass -1 for new text
        public void setContentPane(int position){
            if(position >= 0) {
                Map<String, String> textData = textList.get(position);
                textContent = textData.get("textcontent");
                textName = textData.get("textname");
                textId = textData.get("id");
                etContent.setText(textContent);
                etTextName.setText(textName);
                bDelete.setEnabled(true);
                calculate();
                setChanged(false);
                setNewText(false);
                //get question from db and add to listview.
            } else {
                etContent.setText("");
                etTextName.setText("");
                bDelete.setEnabled(false);
                Long time = System.currentTimeMillis()/1000;
                textId = time.toString();
                setChanged(false);
                setNewText(true);
                //clear questions listview.
            }
        }
        public void getQuestions(String textId){
        }
    }


























