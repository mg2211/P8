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
    EditText etTextContent;
    TextView twTextName;
    String textname = "TestText";
    Context context = this;
    ListView textListView;
    List<Map<String, String>> textList = new ArrayList<>();
    SimpleAdapter textAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        tryButton = (Button) findViewById(R.id.tryButton);
        textListView = (ListView) findViewById(R.id.lwTextOver);

textAdapter = new SimpleAdapter(this,
                textList,
                android.R.layout.simple_list_item_2,
                new String [] {"textname", "textcontent"},
                new int[] {android.R.id.text1, android.R.id.text2}); //hvaða textar eru þetta?
        textListView.setAdapter(textAdapter);


        new TextTask(new TextCallback() {
            @Override
            public void textListDone(HashMap<String, HashMap<String, String>> texts) {
                for (Map.Entry<String, HashMap<String, String>> text : texts.entrySet()){

                    Map<String, String> textInfo = new HashMap<>();
                    String textName = text.getValue().get("textname");
                    textInfo.put("textname", textName);
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

                        for (Map.Entry<String, HashMap<String, String>> textId : texts.entrySet()){
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








    }}























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




