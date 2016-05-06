package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadingActivity extends AppCompatActivity  {

    List<Map<String, String>> assignmentList = new ArrayList<>();
    List<Map<String, String>> textList = new ArrayList<>();

    Button bLogout;
    Button bStart;
    Button bPause;
    Button bFinish;
    Context context = this;
    UserInfo userinfo;
    HashMap<String, String> user;
    String studentId;
    String textId;
    TextView tvTextName2;
    TextView tvTextId;
    TextView tvAssignmentName;
    String textName;
    String assignmentName;
    EditText etTextContent;
    Chronometer chronometer;
    long timeWhenStopped = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        studentId = user.get("studentId");
        Log.d("StudentId:  ", studentId);

        bLogout = (Button) findViewById(R.id.bLogOutStReading);
        bFinish = (Button) findViewById(R.id.bFinish);
        bPause = (Button) findViewById(R.id.bPause);
        tvAssignmentName = (TextView) findViewById(R.id.tvTextName1);
        tvTextName2 = (TextView) findViewById(R.id.tvTextName2);
        tvTextId = (TextView) findViewById(R.id.tvTextId1);
        etTextContent = (EditText) findViewById(R.id.etTextContent1);
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b!=null)
        {
            textId =(String) b.get("textId");
            tvTextId.setText(textId);
            assignmentName = (String) b.get("assignmentName");
            tvAssignmentName.setText(assignmentName);
        }
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        getText();

       bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userinfo = new UserInfo(getApplicationContext());
                userinfo.logOut();
            }
        });





        bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();

                final Long time = chronometer.getBase();
                Log.d("logged time: ", String.valueOf(time));
                chronometer.stop();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("Assignment paused. Hit resume when ready to start again")
                        .setTitle(assignmentName + " paused.")
                        .setPositiveButton("Resume", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                                chronometer.start();

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

        });



        bFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
            }
        });

    }




    public void getText(){
        new TextTask(new TextCallback() {
            @Override
            public void TextCallBack(HashMap<String, HashMap<String, String>> results) {

                //remove other hashmaps in results var to avoid the first returning null

                // remove progressDial if possible
                for (Map.Entry<String, HashMap<String, String>> text : results.entrySet()) {
                    Map<String, String> textInfo = new HashMap<>();
                    String textContent = text.getValue().get("textcontent");
                    textName = text.getValue().get("textname");

                    textInfo.put("textcontent", textContent);
                    textInfo.put("textname", textName);
                    // assignmentList.add(textInfo);
                    Log.d("TEXTTASK", String.valueOf(textInfo));

                    etTextContent.setText(textContent);
                    tvTextName2.setText(textName);




                }
            }
        }, context).executeTask("get", textId, "", "", 0);
    }

 /*   @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.bStart:
                getText();

                Log.d("hello", "start clicked");

                break;

            case R.id.bPause:

                Log.d("hello", "pause clicked");
                break;

            case R.id.bFinish:

                break;

            case R.id.bLogOutStReading:

                UserInfo userinfo = new UserInfo(getApplicationContext());
                userinfo.logOut();

                break;

        }

    }*/
}
