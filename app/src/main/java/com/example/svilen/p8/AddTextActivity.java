package com.example.svilen.p8;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTextActivity extends AppCompatActivity {

    Button tryButton;
    EditText etContent;
    String textname = "TestText";
    Context context = this;
    ListView textListView;
    List<Map<String, String>> textList = new ArrayList<>();
    SimpleAdapter textAdapter;
    Button bUpdate;
    Button bCreateText;
    Button bDelete;
    //TextView tvTextName;
    Button bSave;
    EditText tvTextName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        bSave = (Button) findViewById(R.id.bSave);
        tryButton = (Button) findViewById(R.id.tryButton);
        textListView = (ListView) findViewById(R.id.lwTextOver);
        etContent = (EditText) findViewById(R.id.etContent);
        bUpdate = (Button) findViewById(R.id.bUpdate);
        bCreateText = (Button) findViewById(R.id.bCreateText);
        bDelete = (Button) findViewById (R.id.bDelete);
        tvTextName = (EditText) findViewById(R.id.tvTextname);



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

            }
        });



        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   String dTextName = getText(et)

                String dText = tvTextName.getText().toString();
                String dContent = String.valueOf(etContent);
                String ble = "textest";
                new DeleteTextTask(context).execute(dText);
                Log.d("DELETEDELETE", dText);


            }
        });


textAdapter = new SimpleAdapter(this,
                textList,
                android.R.layout.simple_list_item_1,
                new String [] {"textname"},
                new int[] {android.R.id.text1}); //text1 = the text within the listView
        textListView.setAdapter(textAdapter);

        textListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> textData = textList.get(position);
                String textContent = textData.get("textcontent");
                String textName = textData.get("textname");
                etContent.setText(textContent);
                tvTextName.setText(textName);

            }
        });

        new TextTask(new TextCallback() {
            @Override
            public void textListDone(HashMap<String, HashMap<String, String>> texts) {
                for (Map.Entry<String, HashMap<String, String>> text : texts.entrySet()){

                    Map<String, String> textInfo = new HashMap<>();
                    String textName = text.getValue().get("textname");
                    String textContent = text.getValue().get("textcontent");
                    String textBook = text.getValue().get("textbook");
                    String complexity = text.getValue().get("complexity");
                    textInfo.put("textname", textName);
                    textInfo.put("textcontent", textContent);
                    textInfo.put("textbook", textBook);
                    textInfo.put("complexity", complexity);
                    textList.add(textInfo);
                }
                textAdapter.notifyDataSetChanged();
            }
        }, context).execute(""); //Nothing within "" to get every text - see php script





        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new TextTask(new TextCallback() {
                    @Override
                    public void textListDone(HashMap<String, HashMap<String, String>> texts) {

                        for (Map.Entry<String, HashMap<String, String>> textId : texts.entrySet()) {
                            Map<String, String> textInfo = new HashMap<String, String>();
                            String specificTextName = textId.getValue().get("textname");
                            String specificTextContent = textId.getValue().get("textcontent");
                            String specificTextId = textId.getValue().get("id");
                            textInfo.put("id", specificTextId);
                            textInfo.put("textname", specificTextName);
                            textInfo.put("textcontent", specificTextContent);

                            Log.d("brandurbrandurbrandur", textInfo.toString());


                        }
                    }
                }, context).execute(textname);


            }

        });




        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bCreateText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tvTextName.setText("");
                etContent.setText("");
            }
        });



    }


}























/*textAdapter = new SimpleAdapter(this,
                textList,
                android.R.layout.simple_list_item_2,
                new String [] {"textname", "textcontent"},
                new int[] {android.R.id.text1, android.R.id.text2}); //hvaða textar eru þetta?
        textListView.setAdapter(textAdapter);*/

        /*textListView.setOnClickListener(new AdapterView.OnClickListener(){

            @Override
            public void onClick(View v) {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String, String> textData = textList.get(position);
                    String textId = textData.get("id");
                    Intent textIntent = new Intent (context, .class);
                    textIntent.putExtra("id", textId);
                    textIntent.putExtra("textname", textData.get("textname"));
                    startActivity(textIntent);
                }
            }
        });*/




