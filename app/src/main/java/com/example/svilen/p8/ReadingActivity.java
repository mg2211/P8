package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadingActivity extends AppCompatActivity  {

    /*
    Just some text here...
     */

    List<Map<String, String>> questionList = new ArrayList<>();

    Button bLogout;
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
    TextView tvQuestionToStudent;
    String questionContent;
    String answerText;
    View layout;
    int i;
    String answerId;
    String isCorrrect;
    String text1;
    int answerId1;
    String correctAnswer;
    RadioGroup ll;

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
        ll = new RadioGroup(this);


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

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                 layout = inflater.inflate(R.layout.dialog_answering, null);
                tvQuestionToStudent = (TextView)layout.findViewById(R.id.tvQuestionToStudent);
                Button bDialogSubmit = (Button) layout.findViewById(R.id.bDialogSubmit);
                builder.setView(layout);
                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true); //remember to change to false after programming
                dialog.show();


                getQuestions(textId);




                bDialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       // Log.d("TEXTRADIO: ", text1);
                        //Log.d("textRADIOid: ", String.valueOf(answerId1));

                        if(text1.equals(correctAnswer)){
                            Log.d("YOU HAVE ANSWERED: ", "CORRECT!");
                        }else {
                            Log.d("YOU HAVE ANSWERED: ", "INCORRECT!");
                        }



                    }
                });

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


    public void getQuestions(String textId) {
        new QuestionTask(new QuestionCallback() {
            @Override
            public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                questionList.clear();
                for (Map.Entry<String, HashMap<String, String>> question : results.entrySet()) {
                    Map<String, String> questionInfo = new HashMap<>();
                    String specificQuestionContent = question.getValue().get("questionContent");
                    String specificQuestionId = question.getValue().get("questionId");
                    String specificQuestionAnswers = question.getValue().get("answers");
                    questionInfo.put("Question", specificQuestionContent);
                    questionInfo.put("id", specificQuestionId);
                    questionInfo.put("answers", specificQuestionAnswers);
                    questionList.add(questionInfo);


                    String answers[] = specificQuestionAnswers.split("#");
                    for ( i = 0; i < answers.length; i++) {
                        Log.d("ANSWERS!: ", answers[i].toString());

                        String answer[] = answers[i].split(";");
                        answerText = answer[1];
                         answerId = answer[0];
                        isCorrrect = answer[2];


                        Log.d("isCorrect: ",isCorrrect);

                        if(isCorrrect.equals("1")){
                            Log.d("This is the ", "correct answer");
                            correctAnswer = answerText;
                            Log.d("CORRECTOOO", correctAnswer);

                        }else{
                            Log.d("This is not the correct", "answer");
                        }


                        addRadioButtons(i);
                    }





                    tvQuestionToStudent.setText(specificQuestionContent);
                    questionContent = specificQuestionContent;
                }


            }
        }, context).executeTask("get", "", textId, "", "");
    }

    public void addRadioButtons(int number) {

        for (int row = 0; row < 1; row++) {
            ll.getContext();
            ll.setOrientation(LinearLayout.HORIZONTAL);



            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId((row * 2) + i);
            int id = rdbtn.getId();
            Log.d("RADIO ID: ", String.valueOf(id));
              rdbtn.setText(answerText);

          //  Log.d("RADIOGROUP!!: ", ll.toString());
                ll.addView(rdbtn);

            ll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup rg, int checkedId) {
                    for(int i=0; i<rg.getChildCount(); i++) {
                        RadioButton btn = (RadioButton) rg.getChildAt(i);

                        if(btn.getId() == checkedId) {

                            text1 = (String) btn.getText();
                            answerId1 = btn.getId();

                            // do something with text
                            return;
                        }


                    }
                }

            }
            );





            ((ViewGroup) layout.findViewById(R.id.radiogroup)).removeView(ll);
            ((ViewGroup) layout.findViewById(R.id.radiogroup)).addView(ll);
        }
    }


        }