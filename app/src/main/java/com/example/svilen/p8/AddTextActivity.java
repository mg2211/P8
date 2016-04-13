package com.example.svilen.p8;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    TextView tvId;




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
        tvId = (TextView) findViewById(R.id.tvId);


        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Id = tvId.getText().toString();
                String tContent = etContent.getText().toString();
                String textName = tvTextName.getText().toString();
                new UpdateTextTask(context).execute(Id, tContent, textName);
                Log.d("UPDATE UPDATE", Id);


            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   String dTextName = getText(et)

                String dText = tvTextName.getText().toString();
                String dId = tvId.getText().toString();
                String dContent = String.valueOf(etContent);
                int ble = 4;
                new DeleteTextTask(context).execute(dId);
                Log.d("DELETEDELETE", dId);


            }
        });

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
                String textId = textData.get("id");
                etContent.setText(textContent);
                tvTextName.setText(textName);
                tvId.setText(textId);

            }
        });

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




