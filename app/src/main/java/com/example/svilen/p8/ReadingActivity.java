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
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ReadingActivity extends AppCompatActivity  {

    List<Map<String, String>> questionList = new ArrayList<>();
    String specificQuestionContent1;
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
    View layout;
    int i;
    String answerChoosen;
    String specificQuestionId;
    ArrayList<String> mylist = new ArrayList<String>();
    Set<String> set = new HashSet<String>();
    List<Integer> correctOrNot = new ArrayList<Integer>();    //Set<String> set = new HashSet<String>();
    ArrayList<String> loggedIdAnswers = new ArrayList<String>();
    ArrayList<String> correctAnswer = new ArrayList<String>();
    int noOfQuestions;
    int clickCount = 0;
    String assignmentId;
    String answerIdtoChosenAnswer;
    String lastElement;

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

        Intent intent = getIntent(); // recieving intent from student activity
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            textId =(String) b.get("textId");
            tvTextId.setText(textId);
            assignmentName = (String) b.get("assignmentName");
            tvAssignmentName.setText(assignmentName);
            assignmentId = (String) b.get("id");
        }

        chronometer.setBase(SystemClock.elapsedRealtime()); // sets the base for the clock
        chronometer.start(); // starts the clock
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
                   String specificQuestionContent = question.getValue().get("questionContent");
                    specificQuestionId = question.getValue().get("questionId");
                    String specificQuestionAnswers = question.getValue().get("answers");
                    questionInfo.put("Question", specificQuestionContent);
                    questionInfo.put("id", specificQuestionId);
                    questionInfo.put("answers", specificQuestionAnswers);
                    questionList.add(questionInfo);

                    createArrays();

                    set = new HashSet<String>(mylist); // puts array with questionId into a set, removing all duplicates
                    Log.d("setset: ", set.toString());
                     noOfQuestions = set.size(); // counts how many questions to calculate grade
                    Log.d("number of questions: ", String.valueOf(noOfQuestions));

                    questionContent = specificQuestionContent;
                }
                for (String s: set){
                    System.out.println(s);

                    getAnswers(s); // running getAnswer based on questionId from HashSet


                    Log.d("2222", set.toString());
                }


            }
        }, context).executeTask("get", "", textId, "", "");

    }

    public void createArrays () {

        for (int row = 0; row < 1; row++) {

            mylist.add(specificQuestionId); // adds questionId from getQuestion() to an array
            Log.d("1111", mylist.toString());

        }
    }

    public void addRadioButtons() { //creates dynamic radio button depdending on how many anwswers to a question


        for (int row = 0; row < 1; row++) {
            RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radiogroup);
            rg.setOrientation(LinearLayout.HORIZONTAL);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.MATCH_PARENT);

            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId((row * 2));
            rdbtn.setText(answerText1);
            rg.addView(rdbtn, layoutParams);

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                              public void onCheckedChanged(RadioGroup rg, int checkedId) {
                                                  for (int i = 0; i < rg.getChildCount(); i++) {
                                                      RadioButton btn = (RadioButton) rg.getChildAt(i);

                                                        if (btn.getId() == checkedId) {

                                                            answerChoosen = (String) btn.getText();

                                                          return;
                                                      }


                                                  }
                                              }

                                          }
            );



        }

    }

    public HashMap<String, HashMap<String, String>> getAnswerId(){ // used to retrieve answerId based on questionId and answertext while freezing everything else

    try {
      return  new AnswerTask(new AnswerCallback() {
            @Override
            public void answerdone(HashMap<String, HashMap<String, String>> results) {

            }
        }, context).execute(lastElement, answerChoosen, "").get(30, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    } catch (TimeoutException e) {
        e.printStackTrace();
    }

    return null;
}

    public void getText(){ // get text for student to read
        new TextTask(new TextCallback() {
            @Override
            public void TextCallBack(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");

                //remove other hashmaps in results var to avoid the first returning null

                // remove progressDial if possible
                for (Map.Entry<String, HashMap<String, String>> text : results.entrySet()) {
                    Map<String, String> textInfo = new HashMap<>();
                    String textContent = text.getValue().get("textcontent");
                    textName = text.getValue().get("textname");

                    textInfo.put("textcontent", textContent);
                    textInfo.put("textname", textName);
                    // assignmentList.add(textInfo);

                    etTextContent.setText(textContent);
                    tvTextName2.setText(textName);




                }
            }
        }, context).executeTask("get", textId, "", "", 0);
    }

    public void getAnswers(String s) { // running getAnswer based on questionId from HashSet
        new QuestionTask(new QuestionCallback() {
            @Override
            public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                questionList.clear();
                for (Map.Entry<String, HashMap<String, String>> question : results.entrySet()) {
                    Map<String, String> questionInfo = new HashMap<>();
                    specificQuestionContent1 = question.getValue().get("questionContent");
                    String specificQuestionId1 = question.getValue().get("questionId");
                    String specificQuestionAnswers1 = question.getValue().get("answers");
                    questionInfo.put("Question", specificQuestionContent1);
                    questionInfo.put("id", specificQuestionId1);
                    questionInfo.put("answers", specificQuestionAnswers1);
                    questionList.add(questionInfo);

                    createDialog();

                    String answers[] = specificQuestionAnswers1.split("#"); //splits the string into useful pieces
                    for ( i = 0; i < answers.length; i++) {

                        String answer[] = answers[i].split(";");
                        answerText1 = answer[1];
                       String answerId = answer[0];
                        isCorrrect1 = answer[2];

                        if(isCorrrect1.equals("1")){
                            Log.d("This is the ", "correct answer");


                            loggedIdAnswers.add(answerId); // saves the correct answerIds in a list
                            Log.d("CorrectAnswerIds", loggedIdAnswers.toString());

                        }else{
                            Log.d("This is not the correct", "answer");
                        }

                        addRadioButtons();
                    }
                }
            }
        }, context).executeTask("get", s, "", "", "");
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

                clickCount= clickCount+1; // count number of clicks which will be used to match number of question to create the last alertdialog

                List<String> list = new ArrayList<String>(set);

                for (int x = 0; x<clickCount; x++){ // breaks the HashSet and removes the last index which represents the questionId being answered

                    if(list!=null) {

                        lastElement = Iterables.getLast(list); // the last index on the list, is the first questionid being answered
                        list.remove(lastElement);

                        Log.d("questionId ", lastElement);
                        Log.d("Remaining questionIds", String.valueOf(list));
                    }

                }

                answerIdtoChosenAnswer = getAnswerId().get("AnswerId").get("id");
                Log.d("Choosen answerId", answerIdtoChosenAnswer);

                if(loggedIdAnswers.contains(answerIdtoChosenAnswer)){ //checks if the answer submitted matches a answer in the correct answer array
                    Log.d("YOU HAVE ANSWERED ", "CORRECT!");
                    int isCorrectAnswer = 1;
                    correctOrNot.add(isCorrectAnswer);
                    correctAnswer.add(String.valueOf(isCorrectAnswer));


                    new QuestionResultTask(new QuestionResultCallback() {
                        @Override
                        public void questresultdone(HashMap<String, HashMap<String, String>> questresult) {

                        }
                    }, context).execute(assignmentId, lastElement, "", answerIdtoChosenAnswer, "1", "1");

                }else {
                    Log.d("YOU HAVE ANSWERED ", "INCORRECT!");
                    int inCorrectAnswer = 0;
                    correctOrNot.add(inCorrectAnswer);
                    new QuestionResultTask(new QuestionResultCallback() {
                        @Override
                        public void questresultdone(HashMap<String, HashMap<String, String>> questresult) {

                        }
                    }, context).execute(assignmentId, lastElement, "", answerIdtoChosenAnswer, "0", "1");                }
                Log.d("STUDENTANSWER: ", correctOrNot.toString());

                dialog.dismiss();

                if (clickCount==noOfQuestions){ // creates the last alertdialog after all questions have been answered
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