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
   // String correctAnswer;
    RadioGroup ll;
    String specificQuestionContent;
    String specificQuestionAnswers;
    String specificQuestionId;
    String specificAnswerId;
    ArrayList<String> mylist = new ArrayList<String>();
    String s;
    Set<String> set = new HashSet<String>();
    List<Integer> correctOrNot = new ArrayList<Integer>();    //Set<String> set = new HashSet<String>();
    ArrayList<String> loggedAnswers = new ArrayList<String>();
    ArrayList<String> correctAnswer = new ArrayList<String>();
    int noOfQuestions;
    int clickCount = 0;








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

                int seconds = 0;

                String chronoText = chronometer.getText().toString();
                String timesplit[] = chronoText.split(":");

                if (timesplit.length == 2) {
                    seconds = Integer.parseInt(timesplit[0]) * 60 // change minutes to sec
                            + Integer.parseInt(timesplit[1]); // secs
                } else if (timesplit.length == 3) {
                    seconds = Integer.parseInt(timesplit[0]) * 60 * 60 // change hours to sec
                            + Integer.parseInt(timesplit[1]) * 60 // change min to sec
                            + Integer.parseInt(timesplit[2]); // secs
                }

                Log.d("TOTAL time: ", String.valueOf(seconds));





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

                        Log.d("array1: ", mylist.toString());



                    }

                    set = new HashSet<String>(mylist); // puts array with questionId into a set, removing all duplicates
                    Log.d("setset: ", set.toString());
                     noOfQuestions = set.size(); // counts how many questions to calculate grade
                    Log.d("number of questions: ", String.valueOf(noOfQuestions));

                    questionContent = specificQuestionContent;
                }
                for (String s: set){
                    System.out.println(s);

                    getAnswers(s); // running getAnswer based on questionId from HashSet


                }


            }
        }, context).executeTask("get", "", textId, "", "");

    }

    public void createArrays (int number) {



        for (int row = 0; row < 1; row++) {

            mylist.add(specificQuestionId); // adds questionId from getQuestion() to an array

        }
    }

    public void addRadioButtons(int number) { //creates dynamic radio button depdending on how many anwswers to a question


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


            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                              public void onCheckedChanged(RadioGroup rg, int checkedId) {
                                                  for (int i = 0; i < rg.getChildCount(); i++) {
                                                      RadioButton btn = (RadioButton) rg.getChildAt(i);

                                                        if (btn.getId() == checkedId) {

                                                          text1 = (String) btn.getText();
                                                          answerId1 = btn.getId();
                                                            Log.d("TEXT1: ", text1);
//                                                            Log.d("RADIOisCorrectList", correctAnswer);
                                                            Log.d("ANSWERID1: ", String.valueOf(answerId1));

                                                          // do something with text
                                                          return;
                                                      }


                                                  }
                                              }

                                          }
            );



        }

    }




    public void getText(){ // get text for student to read
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


    public void getAnswers(String s) { // get's answers to questions
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


                    createDialog();



                    String answers[] = specificQuestionAnswers1.split("#");
                    for ( i = 0; i < answers.length; i++) {
                        Log.d("ANSWERS!: ", answers[i].toString());

                        String answer[] = answers[i].split(";");
                        answerText1 = answer[1];
                        answerId = answer[0];
                        isCorrrect1 = answer[2];



                        Log.d("isCorrect: ",isCorrrect1);
                        if(isCorrrect1.equals("1")){
                            Log.d("This is the ", "correct answer");

                            loggedAnswers.add(answerText1);
//                            Log.d("CORRECTOOO", correctAnswer);
                            Log.d("LOGGEDANSWERS: ", loggedAnswers.toString());

                        }else{
                            Log.d("This is not the correct", "answer");
                        }

                        addRadioButtons(i);
                        createArrays(i);



                        Log.d("ARRAY2: ", mylist.toString());

                    }



                    Log.d("Q2, answer: ", specificQuestionContent1);


                  /*  if(dialog.isShowing()){
                        return;
                    }else{
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

                        builder1.setMessage("You have finished your homework")
                                .setTitle("DONE")
                                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                        AlertDialog dialog1 = builder1.create();
                        dialog1.setCanceledOnTouchOutside(true);
                        dialog1.show();
                    }*/
                }


            }
        }, context).executeTask("get", s, "", "", "");
    }

    public void finalPopUp(){


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("You have finished your homework")
                .setTitle("DONE")
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog1 = builder.create();
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.show();
    }

    public void createDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.dialog_answering, null);

        Button bDialogSubmit = (Button) layout.findViewById(R.id.bDialogSubmit);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); //remember to change to false after programming
        dialog.show();
        tvQuestionToStudent = (TextView)layout.findViewById(R.id.tvQuestionToStudent);

        bDialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickCount= clickCount+1;

                Log.d("TEXT1 in Button: ", text1);
//                            Log.d("CORRECT in Button", correctAnswer);
                if(loggedAnswers.contains(text1)){
                    Log.d("YOU HAVE ANSWERED: ", "CORRECT!");
                    int isCorrectAnswer = 1;
                    correctOrNot.add(isCorrectAnswer);
                    correctAnswer.add(String.valueOf(isCorrectAnswer));
                }else {
                    Log.d("YOU HAVE ANSWERED: ", "INCORRECT!");
                    int inCorrectAnswer = 0;
                    correctOrNot.add(inCorrectAnswer);
                }
                Log.d("STUDENTANSWER: ", correctOrNot.toString());






                dialog.dismiss();

                if (clickCount==noOfQuestions){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage("You have finished your homework")
                            .setTitle("Done")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(ReadingActivity.this, StudentActivity.class);
                                    startActivity(intent);

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    int totalCorrect = correctAnswer.size();
                    double totalGrade= ((double) totalCorrect/ (double) noOfQuestions);
                    Log.d("GRADE  many correct: ", String.valueOf(totalCorrect));
                    Log.d("noOfQuestions: ", String.valueOf(noOfQuestions));
                    Log.d("GRADE% FOR QUESTIONS: ", String.valueOf(totalGrade));
                }

            }
        });
        tvQuestionToStudent.setText(specificQuestionContent1);
    }

}