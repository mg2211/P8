package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
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
    Button bPause;
    Button bFinish;
    Context context = this;
    UserInfo userinfo;
    HashMap<String, String> user;
    String studentId;
    String textId;
    TextView tvTextName2;
    Button bDialogSubmit;

    TextView tvAssignmentName;
    String textName;
    String assignmentName;
    Chronometer chronometer;
    long timeWhenStopped = 0;
    TextView tvQuestionToStudent;
    String questionContent;
    View layout;
    int i;
    String answerChoosen;
    String specificQuestionId;
    ArrayList<String> mylist = new ArrayList<>();
    Set<String> set = new HashSet<>();
    List<Integer> correctOrNot = new ArrayList<>();    //Set<String> set = new HashSet<String>();
    ArrayList<String> loggedIdAnswers = new ArrayList<>();
    ArrayList<String> correctAnswer = new ArrayList<>();
    int noOfQuestions;
    int clickCount = 0;
    String assignmentId;
    String answerIdtoChosenAnswer;
    String lastElement;
    int booleanForButton = 0;
    int seconds = 0;

    Pagination mPagination;
    CharSequence mText;
    int mCurrentIndex = 0;
    TextView tvContent;
    String textContent22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);



        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        studentId = user.get("studentId");
        Log.d("StudentId:  ", studentId);

        bFinish = (Button) findViewById(R.id.bFinish);
        bPause = (Button) findViewById(R.id.bPause);
        tvAssignmentName = (TextView) findViewById(R.id.tvTextName1);
        tvTextName2 = (TextView) findViewById(R.id.tvTextName2);
        chronometer = (Chronometer) findViewById(R.id.chronometer);





        final Intent intent = getIntent(); // recieving intent from student activity
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            textId =(String) b.get("textId");
            assignmentName = (String) b.get("assignmentName");
            tvAssignmentName.setText(assignmentName);
            assignmentId = (String) b.get("id");
            Log.d("7878", assignmentId);
        }

        chronometer.setBase(SystemClock.elapsedRealtime()); // sets the base for the clock
        chronometer.start(); // starts the clock



         textContent22 = getText1().get("text0").get("textcontent");
        Log.d("7979 ", textContent22);


        pager();


    bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();

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

           /*     if(clickCount == 0){

                    String totalSeconds = String.valueOf(seconds);

                    new QuestionResultTask(new Callback() {
                        @Override
                        public void asyncDone(HashMap<String, HashMap<String, String>> questresult) {

                        }
                    }, context).execute(assignmentId, "", "", "", "", "1", totalSeconds,"final");



                Intent intent = new Intent(ReadingActivity.this, StudentActivity.class);
                    startActivity(intent);
                }*/

            }
        });

    }

    public void getQuestions(String textId) {
        new QuestionTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
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

                    set = new HashSet<>(mylist); // puts array with questionId into a set, removing all duplicates
                    Log.d("setset: ", set.toString());
                     noOfQuestions = set.size(); // counts how many questions to calculate grade
                    Log.d("number of questions: ", String.valueOf(noOfQuestions));

                }
                for (String s: set){
                    System.out.println(s);

                    getAnswers(s); // running getAnswer based on questionId from HashSet


                }


            }
        }, context).executeTask("get", "", textId, "", "");

    }

    public void createArrays () {

        for (int row = 0; row < 1; row++) {

            mylist.add(specificQuestionId); // adds questionId from getQuestion() to an array

        }
    }

    public void addRadioButtons() { //creates dynamic radio button depdending on how many anwswers to a question



            RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radiogroup);
            rg.setOrientation(LinearLayout.HORIZONTAL);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.MATCH_PARENT);

            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setText(answerText1);
            rg.addView(rdbtn, layoutParams);
          //  rdbtn.setChecked(true); //checks the last created radiobutton


            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                              public void onCheckedChanged(RadioGroup rg, int checkedId) {
                                                  for (int i = 0; i < rg.getChildCount(); i++) {
                                                      RadioButton btn = (RadioButton) rg.getChildAt(i);



                                                        if (btn.getId() == checkedId) {

                                                            answerChoosen = (String) btn.getText();

                                                            booleanForButton = 1; //Have to use this way, to set bdialogSubmit to (un)clickable, as setEnable and setClickable doesn't get called here
                                                            Log.d("1111Radio", String.valueOf(booleanForButton));



                                                          return;
                                                      }


                                                  }
                                              }

                                          }
            );





    }

    public HashMap<String, HashMap<String, String>> getText1(){ // used to retrieve answerId based on questionId and answertext while freezing everything else
        try {
            return new TextTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> results) {

                }
            },context).execute("get", textId, "", "", "0").get(30,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }



    public HashMap<String, HashMap<String, String>> getAnswerId(){ // used to retrieve answerId based on questionId and answertext while freezing everything else

    try {
      return  new AnswerTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> asyncResults) {

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


    public void getAnswers(String s) { // running getAnswer based on questionId from HashSet
        new QuestionTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
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

        bDialogSubmit = (Button) layout.findViewById(R.id.bDialogSubmit);

        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); //remember to change to false after programming
        dialog.show();
        tvQuestionToStudent = (TextView)layout.findViewById(R.id.tvQuestionToStudent);



        bDialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(booleanForButton == 1){

                    Log.d("1818", "Something chosen");

                    clickCount = clickCount + 1; // count number of clicks which will be used to match number of question to create the last alertdialog

                    List<String> list = new ArrayList<>(set);

                    for (int x = 0; x < clickCount; x++) { // breaks the HashSet and removes the last index which represents the questionId being answered

                        if (list != null) {

                            lastElement = Iterables.getLast(list); // the last index on the list, is the first questionid being answered
                            list.remove(lastElement);

                            Log.d("questionId ", lastElement);
                            Log.d("Remaining questionIds", String.valueOf(list));
                        }

                    }

                    answerIdtoChosenAnswer = getAnswerId().get("AnswerId").get("id");
                    Log.d("Choosen answerId", answerIdtoChosenAnswer);

                    if (loggedIdAnswers.contains(answerIdtoChosenAnswer)) { //checks if the answer submitted matches a answer in the correct answer array
                        Log.d("YOU HAVE ANSWERED ", "CORRECT!");
                        int isCorrectAnswer = 1;
                        correctOrNot.add(isCorrectAnswer);
                        correctAnswer.add(String.valueOf(isCorrectAnswer));


                        new QuestionResultTask(new Callback() {
                            @Override
                            public void asyncDone(HashMap<String, HashMap<String, String>> questresult) {

                            }
                        }, context).execute(assignmentId, lastElement, "", answerIdtoChosenAnswer, "1", "1", "","insert"); // remember to change isCompletet to empty when done!!

                    } else {
                        Log.d("YOU HAVE ANSWERED ", "INCORRECT!");
                        int inCorrectAnswer = 0;
                        correctOrNot.add(inCorrectAnswer);
                        new QuestionResultTask(new Callback() {
                            @Override
                            public void asyncDone(HashMap<String, HashMap<String, String>> questresult) {

                            }
                        }, context).execute(assignmentId, lastElement, "", answerIdtoChosenAnswer, "0", "1", "","insert");// remember to change isCompletet to empty when done!!

                    }
                    Log.d("STUDENTANSWER: ", correctOrNot.toString());

                    dialog.dismiss();

                    if (clickCount == noOfQuestions) { // creates the last alertdialog after all questions have been answered
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setMessage("You have finished your homework")
                                .setTitle("Done")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                        double totalGrade = ((double) totalCorrect / (double) noOfQuestions);
                        Log.d("GRADE  many correct: ", String.valueOf(totalCorrect));
                        Log.d("noOfQuestions: ", String.valueOf(noOfQuestions));
                        Log.d("GRADE% FOR QUESTIONS: ", String.valueOf(totalGrade));

                        String totalSeconds = String.valueOf(seconds);

                        new QuestionResultTask(new Callback() {
                            @Override
                            public void asyncDone(HashMap<String, HashMap<String, String>> questresult) {

                            }
                        }, context).execute(assignmentId, "", "", "", "", "1", totalSeconds,"final");
                    }


                    booleanForButton = 0;
                } else { Log.d("Nothing chosen", "9090");

                    Toast.makeText(ReadingActivity.this, "Please select an answer.", Toast.LENGTH_SHORT).show();
                }
        }  });
        tvQuestionToStudent.setText(specificQuestionContent1);
    }
    private void update() {
        final CharSequence text = mPagination.get(mCurrentIndex);
        if (text != null) tvContent.setText(text);
    }

    public void pager() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1; i++) {
            sb.append(textContent22);
        }
        String book_content = sb.toString();

        Spanned htmlString = Html.fromHtml(book_content);
        mText = TextUtils.concat(htmlString);



        tvContent = (TextView) findViewById(R.id.tvText1212);
        tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    tvContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    tvContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                mPagination = new com.example.svilen.p8.Pagination(mText,
                        tvContent.getWidth(),
                        tvContent.getHeight(),
                        tvContent.getPaint());

                update();
            }
        });


      //  LinearLayout previous = (LinearLayout) findViewById(R.id.menulinear1);
        ImageButton previous = (ImageButton) findViewById(R.id.previous) ;
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex > 0) {
                    mCurrentIndex--;
                    update();
                }
            }
        });

       // LinearLayout next = (LinearLayout) findViewById(R.id.next);
        ImageButton next = (ImageButton) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex < mPagination.size()) {
                    mCurrentIndex++;
                    update();
                }
            }
        });




    }}




