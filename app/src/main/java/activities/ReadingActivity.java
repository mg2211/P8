package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.SystemClock;

import callback.*;
import helper.*;
import serverRequests.*;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svilen.p8.R;
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

import javax.microedition.khronos.opengles.GL10;

public class ReadingActivity extends AppCompatActivity  {


    /** A list for storing questions*/
    List<Map<String, String>> questionList = new ArrayList<>();

    /** A string containing the question text itself */
    String specificQuestionContent1;

    /** A string containing a text for a multiple choose answer */
    String answerText1;

    /**  A button used to pause an assignment*/
    Button bPause;

    /**  A button used to indicate that the user has finished an assignment*/
    Button bFinish;

    /**  Context*/
    Context context = this;

    UserInfo userinfo;

    HashMap<String, String> user;

    String studentId; //????

    /**  A string containing the textId of a text*/
    String textId;

    /**  A textView where the name of the text will be displayed*/
    TextView tvTextName;

    /** A button used to submit a choosen answer to a question */
    Button bDialogSubmit;

    /**  A textView displaying the name of the assignment*/
    TextView tvAssignmentName;

    /**  A string containing the name of the assignment*/
    String assignmentName;

    /**  Chronometer used to time how long it takes to read a given text*/
    Chronometer chronometer;

    /**  A long used for calculation when an assignment is paused*/
    long timeWhenStopped = 0;



    /**  A textview displaying the content of a question*/
    TextView tvQuestionToStudent;

    /**  A view used for creating dialogs*/
    View layout;

    /**  A string containing the text of a choosen answer*/
    String answerChoosen;

    /**  A string containing questionId of a question, otherwise contains a string "empty"*/
    String specificQuestionId = "empty";

    /**  A list containing all questionId belonging to a given text*/
    ArrayList<String> mylist = new ArrayList<>();

    /**  A HashSet to remove all possible duplicates of questionId*/
    Set<String> set = new HashSet<>();

    /**  */
    /**  An arraylist containing id of all choosen answers*/
    ArrayList<String> loggedIdAnswers = new ArrayList<>();

    /**  An integer containing the number of questions*/
    int noOfQuestions;

    /**  An integer containing information on how many times bDialogButton has been clicked*/
    int clickCount = 0;

    /**  A string containing the id of the given assignment*/
    String assignmentId;
/**  */

    /**  A string containing the answerId of the choosen answer*/
    String answerIdtoChosenAnswer;

    /**  A string containing the id of a question, retrieved from the last index of an array */
    String lastElement;

    /**  An integer that will change from 0 to 1 if an multiple choose has been picked*/
    int answerChosenListener = 0;

    /**  An integer containing seconds*/
    int seconds = 0;

    /**  mPagination created from the pagination class*/
    Pagination mPagination;

    CharSequence mText;

    int mCurrentIndex = 0;

    /**  A textView containing the content of a text */
    TextView tvContent;

    /**  A string containing the content of a text*/
    String textContent22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        ////???
        userinfo = new UserInfo(context);
        user = userinfo.getUser();
        studentId = user.get("studentId");
        Log.d("StudentId:  ", studentId);

        bFinish = (Button) findViewById(R.id.bFinish);
        bPause = (Button) findViewById(R.id.bPause);
        tvAssignmentName = (TextView) findViewById(R.id.tvAssignmentName);
        tvTextName = (TextView) findViewById(R.id.tvTextName2);
        chronometer = (Chronometer) findViewById(R.id.chronometer);




        /**  An intent used to get information from the Student activity*/
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

        /**  Sets the base for the chronometer*/
        chronometer.setBase(SystemClock.elapsedRealtime());

        /**  Starts the chronometer*/
        chronometer.start();


        /** Calling the getText1() method to retrieve information about a text */
         textContent22 = getText1().get("text0").get("textcontent");
        String textname = getText1().get("text0").get("textname");
        tvTextName.setText(textname);


        pager();

        /**  A button clickk listener, that will pause the chronometer and create an alert dialog*/
    bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**  A long containing time for how long it was paused*/
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();

                Log.d(String.valueOf(timeWhenStopped), "9090");
                chronometer.stop();



                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("Assignment paused. Hit resume when ready to start again")
                        .setTitle(assignmentName + " paused.")
                        .setPositiveButton("Resume", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                /** Sets a new base for the chronometer  */
                                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);

                                /** Starts the chronometer */
                                chronometer.start();

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

        });


        /**  A button that indicates the student has finished his/her assignment*/
        bFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**  Stops the chronometer*/
                chronometer.stop();


                /**  A string containing total time of the chronometer*/
                String chronoText = chronometer.getText().toString();

                /**  An array containing the time from the chronomter split into seconds*/
                String timesplit[] = chronoText.split(":");

                if (timesplit.length == 2) {
                    seconds = Integer.parseInt(timesplit[0]) * 60 // change minutes to sec
                            + Integer.parseInt(timesplit[1]); // secs
                } else if (timesplit.length == 3) {
                    seconds = Integer.parseInt(timesplit[0]) * 60 * 60 // change hours to sec
                            + Integer.parseInt(timesplit[1]) * 60 // change min to sec
                            + Integer.parseInt(timesplit[2]); // secs
                }


                getQuestions(textId);



            }
        });

    }


    public void getQuestions(String textId) {

        /**  Launch a question task to get questions to a given textId */
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

                    /**  Puts an array with questionId into a HashSet, removing all possible duplicates*/
                    set = new HashSet<>(mylist);

                    /**  Counts how many questions there are*/
                     noOfQuestions = set.size();

                }

                /**  If there are no questions to an assignment*/
                if (specificQuestionId.equals("empty")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage("There are no questions to this assignment. You have finished your homework")
                            .setTitle("Done")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    /**  Starts an intent to start the StudentActivity*/
                                    Intent intent = new Intent(ReadingActivity.this, StudentActivity.class);
                                    startActivity(intent);

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    /** A string containning the total number of seconds from the chronometer */
                    String totalSeconds = String.valueOf(seconds);

                    /**  A questionResultTask used to save the results of an assignment, executes on the assignmentID, updates
                     * isComplete from 0 to 1, inserts total time, and uses the update method*/
                    new QuestionResultTask(context).execute(assignmentId, "", "", "", "", "1", totalSeconds,"update", "");

                }

                /**  Creates a string for every index within the HashSet representning the questionId of questions, and then calling the getAnswers()
                 * method based on the given questionId*/
                for (String s: set){
                    System.out.println(s);

                    getAnswers(s);


                }


            }
        }, context).executeTask("get", "", textId, "", "");

    }


    /**  Creates an array with questionIds*/
    public void createArrays () {

        for (int row = 0; row < 1; row++) {

            mylist.add(specificQuestionId); // adds questionId from getQuestion() to an array

        }
    }

    /** Creates dynamic radio buttons depending how many answers are to a given question */
    public void addRadioButtons() {


            RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radiogroup);
            rg.setOrientation(LinearLayout.VERTICAL);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.MATCH_PARENT);

            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setText(answerText1);
            rg.addView(rdbtn, layoutParams);


        /**  An on click listener for the dynamic radio bottons */
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                              public void onCheckedChanged(RadioGroup rg, int checkedId) {
                                                  for (int i = 0; i < rg.getChildCount(); i++) {
                                                      RadioButton btn = (RadioButton) rg.getChildAt(i);



                                                        if (btn.getId() == checkedId) {

                                                            /**  Gets the text of the answer chosen*/
                                                            answerChoosen = (String) btn.getText();
/**  */
                                                            /**  If an answer has been chosen, the integer will change to 1 instead of 0*/
                                                            answerChosenListener = 1; //Have to use this way, to set bdialogSubmit to (un)clickable, as setEnable and setClickable doesn't get called here



                                                          return;
                                                      }


                                                  }
                                              }

                                          }
            );





    }

    /** Used to retrieve answerId based on questionId and answertext while freezing the UI thread else until it is finished or timed out
    * We use it to make sure the that the required information is obtained to set textviews in the UI to avoid a nullpoint exception*/
    public HashMap<String, HashMap<String, String>> getText1(){
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



    /** Used to retrieve answerId based on questionId and answertext while freezing the UI thread */
    public HashMap<String, HashMap<String, String>> getAnswerId(){

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

    //* Launce a questionTask to get answers based on question Id obtained from the set HashSet*/
    public void getAnswers(String s) {
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

                    //* Splits the answers into useful strings, as the answer is returned in a single string with answer, answerId, and isCorrect*/
                    String answers[] = specificQuestionAnswers1.split("#");
                    for (int i = 0; i < answers.length; i++) {

                        String answer[] = answers[i].split(";");
                        answerText1 = answer[1];
                       String answerId = answer[0];
                     String   isCorrrect1 = answer[2];

                        //* If the answer is the correct one, it will save the id of that answer to a list*/
                        if(isCorrrect1.equals("1")){

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


    //* A method used to create dialog, which is called each time getAnswers() is run */
    public void createDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.dialog_answering, null);

        bDialogSubmit = (Button) layout.findViewById(R.id.bDialogSubmit);

        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        tvQuestionToStudent = (TextView)layout.findViewById(R.id.tvQuestionToStudent);



        //* A button click listener for submitting answers */
        bDialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //* Checks if a radio button has been chosen */
                if(answerChosenListener == 1){

                    //* Counts the number of clicks which will be used to match the umber of questions to create the last alert dialog
                    clickCount = clickCount + 1;

                    //*The HashSet set is converted to an array*/
                    List<String> list = new ArrayList<>(set);


                    //* Breaks the array formed by set and removes the last index which represents the questionId being answered */
                    for (int x = 0; x < clickCount; x++) {
                        if (list != null) {

                            //* A string containing the last index of the list, which is the current questionId being answered*/
                            lastElement = Iterables.getLast(list);

                            //*Removes the last index from the array */
                            list.remove(lastElement);


                        }

                    }

                    /** Used to retrieve answerId based on questionId and answertext  */
                    answerIdtoChosenAnswer = getAnswerId().get("AnswerId").get("id");



                    //* Checks if the answerId submitted matches a answerId in the correct answerId array*/
                    if (loggedIdAnswers.contains(answerIdtoChosenAnswer)) {
                        Log.d("YOU HAVE ANSWERED ", "CORRECT!");

                        //* Launch a QuestionResultTask which executes the following parameters: assignmentId, lastElement (questionId), answerIdtoChosenAnswer,
                        // inserts "1"  to answeredCorrect row in questionResult table in database, and updates the isComplete row in Assignment table in
                        // Database from "0" to "1", uses the "insert method"*/
                        new QuestionResultTask(context).execute(assignmentId, lastElement, "", answerIdtoChosenAnswer, "1", "1", "","insert", ""); // remember to change isCompletet to empty when done!!

                    } else {
                        Log.d("YOU HAVE ANSWERED ", "INCORRECT!");



                        //* Launch a QuestionResultTask which executes the following parameters: assignmentId, lastElement (questionId), answerIdtoChosenAnswer,
                        // inserts "0" to answeredCorrect row in questionResult table in database, and updates the isComplete row in Assignment table in
                        // Database from "0" to "1", uses the "insert method"*/
                        new QuestionResultTask(context).execute(assignmentId, lastElement, "", answerIdtoChosenAnswer, "0", "1", "","insert", "");// remember to change isCompletet to empty when done!!

                    }

                    dialog.dismiss();



                    //*  Creates the last dialog once all the questions have been answered */
                    if (clickCount == noOfQuestions) {
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

                        //* A string containing the the string value of seconds */
                        String totalSeconds = String.valueOf(seconds);

                        //* Launce a questionResultTask, which will update isComplete row to "1", and insert the total number of seconds it
                        //  took to finish the assignment */
                        new QuestionResultTask(context).execute(assignmentId, "", "", "", "", "1", totalSeconds,"final", "");
                    }


                    //* Sets the answerChosenListener back to 0*/
                    answerChosenListener = 0;
                } else {

                    Toast.makeText(ReadingActivity.this, "Please select an answer.", Toast.LENGTH_SHORT).show();
                }
        }  });

        //* Sets the tvQuestionToStudent listview to display the content of a given question*/
        tvQuestionToStudent.setText(specificQuestionContent1);
    }

    //* An update method used by the pager*/
    private void update() {
        final CharSequence text = mPagination.get(mCurrentIndex);
        if (text != null) tvContent.setText(text);
    }

    //* A pager used to create pages, to give a book like feeling when reading through a text*/
    public void pager() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1; i++) {
            sb.append(textContent22);
        }
        String book_content = sb.toString();

        Spanned htmlString = Html.fromHtml(book_content);
        mText = TextUtils.concat(htmlString);



        tvContent = (TextView) findViewById(R.id.tvTextContent);
        tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    tvContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    tvContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                mPagination = new Pagination(mText,
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
        ImageButton next = (ImageButton) findViewById(R.id.next1);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex < mPagination.size()) {
                    mCurrentIndex++;
                    update();
                }
            }
        });




    }


    //* A method to disable the back button*/
    @Override
    public void onBackPressed() {
        Log.d("Back button pressed", " -Disabled");
    }
}




