package com.example.svilen.p8;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReadingActivity extends AppCompatActivity  {

    List<Map<String, String>> questionList = new ArrayList<>();

    String specificQuestionContent1;
    String specificQuestionId1;
    String specificQuestionAnswers1;
    String answerId11;
    String answerText1;
    String isCorrrect1;

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
    String specificQuestionContent;
    String specificQuestionAnswers;
    String specificQuestionId;
    String specificAnswerId;
    ArrayList<String> mylist = new ArrayList<String>();
    String s;
    Set<String> set = new HashSet<String>();
    RelativeLayout rl;
    LinearLayout lLayout;
    //Set<String> set = new HashSet<String>();

    // Set<String> mylist = new HashSet<String>();




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



               /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                 layout = inflater.inflate(R.layout.dialog_answering, null);
                tvQuestionToStudent = (TextView)layout.findViewById(R.id.tvQuestionToStudent);
                Button bDialogSubmit = (Button) layout.findViewById(R.id.bDialogSubmit);
                builder.setView(layout);
                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true); //remember to change to false after programming
                dialog.show();


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
                });*/

                getQuestions(textId);



            }
        });

    }







    public void getQuestions(String textId) {
        new QuestionTask(new QuestionCallback() {
            @Override
            public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                questionList.clear();
                for (Map.Entry<String, HashMap<String, String>> question : results.entrySet()) {
                    Map<String, String> questionInfo = new HashMap<>();
                    specificQuestionContent = question.getValue().get("questionContent");
                    specificQuestionId = question.getValue().get("questionId");
                    specificQuestionAnswers = question.getValue().get("answers");
                    questionInfo.put("Question", specificQuestionContent);
                    questionInfo.put("id", specificQuestionId);
                    questionInfo.put("answers", specificQuestionAnswers);
                    questionList.add(questionInfo);







                    String answers[] = specificQuestionAnswers.split("#");
                    for ( i = 0; i < answers.length; i++) {
                        Log.d("answers!: ", answers[i].toString());

                        String answer[] = answers[i].split(";");
                        answerText = answer[1];
                        answerId = answer[0];
                        isCorrrect = answer[2];

                        createArrays(i);

                        Log.d("isCorrect: ",isCorrrect);

                        if(isCorrrect.equals("1")){
                            Log.d("This is the ", "correct answer");
                            correctAnswer = answerText;
                            Log.d("CORRECTOOO", correctAnswer);

                        }else{
                            Log.d("This is not the correct", "answer");
                        }

                        // addRadioButtons(i);
                        //addRB(i);
                        // populateList(i);
                        createArrays(i);

                        Log.d("array: ", mylist.toString());



                    }

                    set = new HashSet<String>(mylist);
                    Log.d("setset: ", set.toString());

//                    tvQuestionToStudent.setText(specificQuestionContent);
                    questionContent = specificQuestionContent;
                }
                for (String s: set){
                    System.out.println(s);

                    getAnswers(s);


                }


            }
        }, context).executeTask("get", "", textId, "", "");

    }

    public void createArrays (int number) {



        for (int row = 0; row < 1; row++) {

            mylist.add(specificQuestionId);

        }
        // Log.d("ARRAY: ", mylist.toString());
    }

    public void addRadioButtons(int number) {


        for (int row = 0; row < 1; row++) {
            RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radiogroup);
            rg.setOrientation(LinearLayout.HORIZONTAL);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);

                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId((row * 2) + number);
                rdbtn.setText(answerText1);
                int radioId = rdbtn.getId();
                rg.addView(rdbtn, layoutParams);
                Log.d("RADIONAME: ", answerText1);
                Log.d("RADIOID: ", String.valueOf(radioId));


        }


           // ((ViewGroup) findViewById(R.id.radiogroup)).removeView(ll);
            //((ViewGroup) findViewById(R.id.radiogroup)).addView(ll);


    }

public void addRadio (int numbers) {

    RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radiogroup);
    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT);

    // add 5 radio buttons to the group
    RadioButton rb;
    for (int i = 0; i < 5; i++){
        rb = new RadioButton(context);
        rb.setText("item set" + i);
        rb.setId(i);
        rg.addView(rb, layoutParams);
    }



}

    /*private void populateList(int number){

        // get reference to radio group in layout
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        // layout params to use when adding each radio button
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        // add 20 radio buttons to the group
        for (int i = 0; i < 1; i++){
            RadioButton newRadioButton = new RadioButton(this);
            String label = "item " + i;
            newRadioButton.setText(label);
            newRadioButton.setId(i);
            radiogroup.addView(newRadioButton, layoutParams);
        }
    }*/


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


    public void getAnswers(String s) {
        new QuestionTask(new QuestionCallback() {
            @Override
            public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                questionList.clear();
                for (Map.Entry<String, HashMap<String, String>> question : results.entrySet()) {
                    Map<String, String> questionInfo = new HashMap<>();
                    specificQuestionContent1 = question.getValue().get("questionContent");
                    specificQuestionId1 = question.getValue().get("questionId");
                    specificQuestionAnswers1 = question.getValue().get("answers");
                    questionInfo.put("Question", specificQuestionContent1);
                    questionInfo.put("id", specificQuestionId1);
                    questionInfo.put("answers", specificQuestionAnswers1);
                    questionList.add(questionInfo);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = getLayoutInflater();
                    layout = inflater.inflate(R.layout.dialog_answering, null);

                    Button bDialogSubmit = (Button) layout.findViewById(R.id.bDialogSubmit);
                    builder.setView(layout);
                    final AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true); //remember to change to false after programming
                    dialog.show();
                    tvQuestionToStudent = (TextView)layout.findViewById(R.id.tvQuestionToStudent);

                    bDialogSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Log.d("TEXTRADIO: ", text1);
                            //Log.d("textRADIOid: ", String.valueOf(answerId1));

                          /* if(text1.equals(correctAnswer)){
                                Log.d("YOU HAVE ANSWERED: ", "CORRECT!");
                            }else {
                                Log.d("YOU HAVE ANSWERED: ", "INCORRECT!");
                            }*/



                        }
                    });
                    tvQuestionToStudent.setText(specificQuestionContent1);


                    String answers[] = specificQuestionAnswers1.split("#");
                    for ( i = 0; i < answers.length; i++) {
                        Log.d("ANSWERS!: ", answers[i].toString());

                        String answer[] = answers[i].split(";");
                        answerText1 = answer[1];
                        answerId = answer[0];
                        isCorrrect = answer[2];

                        addRadioButtons(i);
                        createArrays(i);

                        Log.d("isCorrect: ",isCorrrect);

                        if(isCorrrect.equals("1")){
                            Log.d("This is the ", "correct answer");
                            correctAnswer = answerText;
                            Log.d("CORRECTOOO", correctAnswer);

                        }else{
                            Log.d("This is not the correct", "answer");
                        }


                        //addRB(i);
                        // populateList(i);

                        Log.d("ARRAY: ", mylist.toString());



                    }

                    Log.d("Q2, answer: ", specificQuestionContent1);


                }
            }
        }, context).executeTask("get", s, "", "", "");
    }

}