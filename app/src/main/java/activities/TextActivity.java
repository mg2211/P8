package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svilen.p8.R;

import callback.*;
import helper.*;
import serverRequests.*;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextActivity extends AppCompatActivity {
    /*The context for which various dialogs and server request is running from*/
    private final Context context = this;

    /*UI elements*/
    private EditText etContent;
    private Button bDelete;
    private EditText etTextName;
    private TextView tvComplexity;

    /*Listview adapters*/
    private ListViewAdapter textAdapter;
    private SimpleAdapter questionAdapter;

    /*List to populate listviews*/
    private final List<Map<String, String>> textList = new ArrayList<>();
    private final List<Map<String, String>> questionList = new ArrayList<>();

    /*Colors for the listview complexity*/
    private final ArrayList<Integer> colors = new ArrayList<>();

    /*The content of the text*/
    private String textContent;
    /*The id of the text*/
    private String textId;
    /*The name of the text*/
    private String textName;
    /*Boolean for checking if the text is new or an old text is being edited*/
    private boolean newText;
    /*Boolean for checking if the text is changed from the last saved instance of that text*/
    private boolean changed;
    /*Double for storing the complexity of the text*/
    private double lix;
    /*Boolean for checking if the contentpane should be cleared*/
    private boolean clear;

    /**
     * onCreate sets up the ui elements, populates them and describes what to do on click
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        /*Setting the content pane to a new text and getting all texts from DB to populate the text listView*/
        setNewText(true);
        setChanged(false);
        getTexts();
        setContentPane(-1);

        /*Setting up UI elements*/
        Button bSave = (Button) findViewById(R.id.bSave);
        Button bAddQuestion = (Button) findViewById(R.id.bAddQuestion);
        Button bAddText = (Button) findViewById(R.id.bAddText);
        EditText etSearch = (EditText) findViewById(R.id.etSearch);

        etContent = (EditText) findViewById(R.id.etContent);
        bDelete = (Button) findViewById(R.id.bDelete);
        bDelete.setEnabled(false);
        etTextName = (EditText) findViewById(R.id.etTextname);
        tvComplexity = (TextView) findViewById(R.id.tvComplexity);

        ListView lvQuestions = (ListView) findViewById(R.id.lvQuestions);
        ListView lvTexts = (ListView) findViewById(R.id.lvTexts);

        /*Creating an adapter for the questionAdapter*/
        questionAdapter = new SimpleAdapter(this, questionList,
                android.R.layout.simple_list_item_1,
                new String[]{"Question"},
                new int[]{android.R.id.text1});
        /*Setting adapter*/
        lvQuestions.setAdapter(questionAdapter);

        /**
         * Adds an onItemClickListener for the listView with questions
         * Which calls the questionDialog with the parameter of position - The position in the listview
         */
        lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                questionDialog(position);
            }
        });
        /**
         * Adds an onClickListener for the Add Question Button Which calls
         * The questionDialog with Parameter(-1) - A negative number will set the dialog to a new question
         */
        bAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionDialog(-1);
            }
        });

        /*Creating a new Adapter from the custom adapter class ListViewAdapter which takes the color ArrayList for coloring rows based on complexity*/
        textAdapter = new ListViewAdapter(this, textList, new String[]{"textname", "complexity"}, new int[]{android.R.id.text1, android.R.id.text2}, colors);
        /*Setting the adapter*/
        lvTexts.setAdapter(textAdapter);

        /*Setting an onItemClickListener for the lvText*/
        lvTexts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
               /*If the text has changed from it's last saved instance the confirm dialog method is called*/
                if (changed) {
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            /*If the dialog response is positive i.e. that changes should be saved*/
                            if (dialogResponse) {
                                /*If the text is new i.e. not saved in the DB the createText method is called
                                * this method returns true if there are no problems*/
                                if (newText) {
                                    if (createText()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewText(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                                /*If the text has changed the updateText method is called
                                * this method returns true if there are no problems.*/
                                if (changed) {
                                    if (updateText()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewText(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                            /*If the response is negative */
                            } else {
                                clear = true;
                                /*Deleting all questions added to a new text*/
                               if(newText) {
                                   new QuestionTask(new Callback() {
                                       @Override
                                       public void asyncDone(HashMap<String, HashMap<String, String>> results) {

                                       }
                                   }, context).executeTask("delete", "", textId, "", "");
                               }
                            }

                            /*If clear is set to true the content pane is set to the text initially chosen from the LV*/
                            if (clear) {
                                setContentPane(position);

                            }
                        }
                    });
                    /*If a change in the text or name hasn't been detected the content pane is set to the text initially chosen from the LV*/
                } else {
                    setContentPane(position);
                }
            }
        });

        /*Setting an onClickListener for the Add Text button - This clickListener is behaving as the onItemClickListener above*/
        bAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changed) {
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if (dialogResponse) {
                                if (newText) {
                                    if (createText()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewText(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                                if (changed) {
                                    if (updateText()) {
                                        clear = true;
                                        setChanged(false);
                                        setNewText(false);
                                    } else {
                                        clear = false;
                                    }
                                }
                            } else {
                                clear = true;
                                if(newText) {
                                    new QuestionTask(new Callback() {
                                        @Override
                                        public void asyncDone(HashMap<String, HashMap<String, String>> results) {

                                        }
                                    }, context).executeTask("delete", "", textId, "", "");
                                }
                            }
                            if (clear) {
                                setContentPane(-1);
                            }
                        }
                    });
                } else {
                    setContentPane(-1);
                }


            }
        });
        /*Setting onClickListener for the save button*/
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Checking if all information is provided and call either the createText or updateText depending on the changed and newText booleans
                * if some information is missing text or name a toast is shown on the screen*/
                if (!etTextName.getText().toString().equals("") && !etContent.getText().toString().equals("")) {
                    getQuestions(textId);
                    if (newText) {
                        createText();
                        setChanged(false);
                    } else {
                        updateText();
                        setChanged(false);
                    }
                } else {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "Please fill in all required fields";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }
                getTexts();
            }
        });

        /*onClickListener for the delete button - Calls the deleteText method when clicked*/
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteText();
            }
        });
        /*Adding a textChangedListener for the content editText - This is used for detecting changes and setting the changed boolean accordingly */
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*Everytime a text is changed the calculate method is called*/
                calculate();

                String content = etContent.getText().toString();
                if (!content.equals("") && !content.equals(textContent)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });
        /*Adding a textChangedListener for the name editText - This is used for detecting changes and setting the changed boolean accordingly */
        etTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = etTextName.getText().toString();
                if (!name.equals("") && !name.equals(textName)) {
                    setChanged(true);
                } else {
                    setChanged(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*Adding a textChangedListener for the search editText - Used for real time sorting of the listview of texts*/
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Auto generated stub
            }
        });
    }

    /**
     * Calculate method
     * Calculates a text's complexity based on the Lix standard.
     */
    private void calculate() {
        /*Getting the text to be calculated*/
        String inputText = etContent.getText().toString();
        /*Declaring a string var for the clean text*/
        String cleanText;

        /*Int P is the number of punctuation marks in the input text*/
        int P = StringUtils.countMatches(inputText, ".");
        P = P + StringUtils.countMatches(inputText, "?");
        P = P + StringUtils.countMatches(inputText, "!");
        P = P + StringUtils.countMatches(inputText, ":");
        P = P + StringUtils.countMatches(inputText, ";");

        /*Cleaning all punctuation marks from inputText*/
        cleanText = inputText.replaceAll("/./", "");
        cleanText = cleanText.replaceAll("/?/", "");
        cleanText = cleanText.replaceAll("/!/", "");
        cleanText = cleanText.replaceAll("/:/", "");
        cleanText = cleanText.replaceAll("/;/", "");

        /*Splitting the cleanText at every whitespace*/
        String[] words = cleanText.split("\\s+");

        /*Int O - The number of words in the text*/
        int O = words.length;

        /*If O and P larger than 0 the complexity counter is running
        * if the one of these are 0 an exeption will occur due to division by zero*/
        if (O > 0 && P > 0) {
            /*Int L - The number of long words i.e. words longer than 6 characters*/
            int L = 0;
            /*Iterating the words array*/
            for (String word : words) {
                if (word.length() > 6) {
                    L++;
                }
            }
            /*Calculating the Lix complexity */
            lix = (O / P) + (L * 100 / O);

            /*Setting the textView accordingly*/
            tvComplexity.setText(String.valueOf(lix));
        } else {

            /*If there are no words OR punctuation marks in the input text the textView is set to nothing*/
            tvComplexity.setText("");
        }
    }
    /*getTexts method - creating a new TextTask object*/
    private void getTexts() {
        new TextTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                /*Removing the response from the HashMap*/
                results.remove("response");
                /*Clearing the textList and colors*/
                textList.clear();
                colors.clear();
                /*Iterating over the HashMap to add a text to the listView*/
                for (Map.Entry<String, HashMap<String, String>> text : results.entrySet()) {

                    Map<String, String> textInfo = new HashMap<>();
                    String textId = text.getValue().get("id");
                    String textName = text.getValue().get("textname");
                    String textContent = text.getValue().get("textcontent");
                    String textBook = text.getValue().get("textbook");
                    String complexity = text.getValue().get("complexity");
                    textInfo.put("textname", textName);
                    textInfo.put("textcontent", textContent);
                    textInfo.put("textbook", textBook);
                    textInfo.put("complexity", "Complexity: " + complexity);
                    textInfo.put("id", textId);
                    textList.add(textInfo);

                    /*Adding the right color in the colors ArrayList*/
                    double difficulty = Double.parseDouble(complexity);
                    if (difficulty > 0 && difficulty <= 20) {
                        colors.add(Color.rgb(156, 204, 101));
                    } else if (difficulty > 20 && difficulty <= 40) {
                        colors.add(Color.rgb(255, 235, 69));
                    } else if (difficulty > 40) {
                        colors.add(Color.rgb(239, 83, 80));
                    } else {
                        colors.add(Color.TRANSPARENT);
                    }
                }
                /*Letting the LV know that there is new data*/
                textAdapter.notifyDataSetChanged();
            }
        },context).executeTask("get","","","",0);
    }

    /**
     *Called when saving a text and the newText boolean is true
     * @return true if there are no problems
     */
    private boolean createText() {
        /*Checking if all information is typed in*/
        if (!etTextName.getText().toString().equals("") && !etContent.getText().toString().equals("")) {
            /*Creating a new TextTask object*/
            new TextTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                    /*String id - getting the text's id in the database*/
                   String id = results.get("response").get("insertedId");

                    if (questionList.size() > 0) {
                        /*Iterating over the questionList and updating each question's textId to the text's new id via the QuestionTask class*/
                        for (int i = 0; i < questionList.size(); i++) {
                            String questionId = questionList.get(i).get("id");
                            new QuestionTask(new Callback() {
                                @Override
                                public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                                }
                            }, context).executeTask("update", questionId, id, questionList.get(i).get("answers"), questionList.get(i).get("Question"));
                        }
                    }
                }
            },context).executeTask("create", "", etTextName.getText().toString(), etContent.getText().toString(), lix);
            /*updating the textList*/
            getTexts();
            return true;
        } else {
            /*If an entry field is empty a toast is shown and the method returns false*/
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }

    }

    /**
     * Called when saving a text and the newText boolean is false and the changed boolean is true
     * @return boolean true if no problems is encountered
     */
    private boolean updateText() {
        /*Checking if all input fields has text*/
        if (!etTextName.getText().toString().equals("") && !etContent.getText().toString().equals("")) {

            /*Creating a new TextTask*/
            new TextTask(new Callback() {
                @Override
                public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                    /*When the text has finished updating the questionList is iterated to update the questions as well via the QuestionTask*/
                    if (questionList.size() > 0) {
                        for (int i = 0; i < questionList.size(); i++) {
                            String questionId = questionList.get(i).get("id");
                            new QuestionTask(new Callback() {
                                @Override
                                public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                                }
                            }, context).executeTask("update", questionId, textId, questionList.get(i).get("answers"), questionList.get(i).get("Question"));
                        }
                    }
                }
            },context).executeTask("update", textId, etTextName.getText().toString(), etContent.getText().toString(), lix);
            /*Updating the textList*/
            getTexts();
            return true;
        } else {
            /*Showing a toast if some entry fields are empty*/
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }

    }

    /**
     * DeleteText method
     * Called when clicking the delete button in the content pane
     */
    private void deleteText() {
        /*Showing an Alert dialog asking for confirmation*/
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete the text " + textName)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*If the dialog response is positive i.e. confirmed for deletion, a new TextTask is created with the parameters for that text*/
                        new TextTask(new Callback() {
                            @Override
                            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                                /*When the text is deleted, the QuestionTask is called as well to delete corresponding questions*/
                                new QuestionTask(new Callback() {//delete questions
                                    @Override
                                    public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                                    }
                                }, context).executeTask("delete", "", textId, "", ""); //delete the questions after the text is deleted
                            }
                        },context).executeTask("delete", textId, "", "", 0); //delete the text
                        /*Updating the textList and setting the content pane to a new text*/
                        getTexts();
                        setContentPane(-1);
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    /**
     * Method for setting the newText boolean
     * @param value
     */
    private void setNewText(boolean value) {
        newText = value;
    }

    /**
     * Method for setting the changed boolean
     * @param value
     */
    private void setChanged(boolean value) {
        changed = value;
    }

    /**
     * Creating a confirmation dialog
     * @param callback - implementing the DialogCallback interface - used for passing the dialog answer back to its caller
     */
    private void confirm(final DialogCallback callback) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("You have unsaved changes - Save before continuing?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.dialogResponse(true);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.dialogResponse(false);
                    }
                })
                .show();

    }

    /**
     *
     * @param position - the position from the ListView - pass -1 for new text
     */
    private void setContentPane(int position) {
        /*Setting the contentpane to either a new text or a text from the ListView*/
        if (position >= 0) {
            Map<String, String> textData = (Map) textAdapter.getItem(position);
            textContent = textData.get("textcontent");
            textName = textData.get("textname");
            textId = textData.get("id");
            etContent.setText(textContent);
            etTextName.setText(textName);
            bDelete.setEnabled(true);
            lix = 0;
            calculate();
            setChanged(false);
            setNewText(false);
            getQuestions(textId);
        } else {
            etContent.setText("");
            etTextName.setText("");
            bDelete.setEnabled(false);
            Long time = System.currentTimeMillis() / 1000; //setting a temporary unique id for new texts
            textId = time.toString();
            lix = 0;
            setChanged(false);
            setNewText(true);
            getQuestions(textId);
        }
    }

    /**
     * Getting the questions for a specific text
     * @param textId - the textId for which the method should get questions for
     */
    private void getQuestions(String textId) {
        new QuestionTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> results) {
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
                }
                questionAdapter.notifyDataSetChanged();
            }
        }, context).executeTask("get", "", textId, "", "");
    }

    /**
     * Method for converting pixels to density independent pixels
     * @param px - the pixel value to be converted
     * @return
     */
    private int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Adding rows of answers to the questionDialog
     * @param childNo the number of already added childs(answers)+1
     * @param id if a question has more than two answers this parameter is invoked to set the id of the answer to the one in the DB
     * @return a linear layout with a switch and an editText
     */
    private View addAnswerToDialog(int childNo, String id) {
        /*Creating the linear layout for containing the Switch and the editText*/
        LinearLayout answer = new LinearLayout(context);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llParams.setMargins(0, 0, 0, pxToDp(20));
        answer.setOrientation(LinearLayout.HORIZONTAL);
        answer.setTag(R.id.ANSWER_ID_TAG, id);
        answer.setLayoutParams(llParams);

        EditText answerText = new EditText(context);
        answerText.setHint("Answer");
        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        etParams.setMargins(0, 0, pxToDp(20), 0);
        answerText.setLayoutParams(etParams);
        String etTag = "etDialogAnswer" + childNo;
        answerText.setTag(etTag);
        answer.addView(answerText);

        Switch answerSwitch = new Switch(context);
        LinearLayout.LayoutParams swParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        swParams.setMargins(0, 0, pxToDp(20), 0);
        answerSwitch.setTextOff("Wrong");
        answerSwitch.setTextOn("Right");
        answerSwitch.setLayoutParams(swParams);
        String switchTag = "swDialogSwitch" + childNo;
        answerSwitch.setTag(switchTag);
        answer.addView(answerSwitch);

        return answer;
    }

    //@param position - the position from the listview - pass -1 for new question
    private void questionDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_question, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Button bDialogAddAnswer = (Button) layout.findViewById(R.id.bDialogAddAnswer);
        Button bDialogDelete = (Button) layout.findViewById(R.id.bDialogDelete);
        Button bDialogSave = (Button) layout.findViewById(R.id.bDialogSave);
        final EditText etDialogQuestion = (EditText) layout.findViewById(R.id.etDialogQuestion);
        final LinearLayout LLAnswers = (LinearLayout) layout.findViewById(R.id.LLAnswers);
        final EditText etDialogAnswer0 = (EditText) layout.findViewWithTag("etDialogAnswer0");
        final EditText etDialogAnswer1 = (EditText) layout.findViewWithTag("etDialogAnswer1");
        Switch swDialogSwitch0 = (Switch) layout.findViewWithTag("swDialogSwitch0");
        Switch swDialogSwitch1 = (Switch) layout.findViewWithTag("swDialogSwitch1");

        if (position >= 0) {
            bDialogDelete.setEnabled(true);
            etDialogQuestion.setText(questionList.get(position).get("Question"));
            String answerString = questionList.get(position).get("answers");

            String answers[] = answerString.split("#");
            for (int i = 0; i < answers.length; i++) {
                String answer[] = answers[i].split(";");
                String answerText = answer[1];
                String answerId = answer[0];
                boolean answerCorrect;
                answerCorrect = answer[2].equals("1");

                if (i == 0) {
                    etDialogAnswer0.setText(answerText);
                    swDialogSwitch0.setChecked(answerCorrect);
                    LLAnswers.getChildAt(i).setTag(R.id.ANSWER_ID_TAG,answerId);
                } else if (i == 1) {
                    etDialogAnswer1.setText(answerText);
                    swDialogSwitch1.setChecked(answerCorrect);
                    LLAnswers.getChildAt(i).setTag(R.id.ANSWER_ID_TAG,answerId);
                } else {
                    LLAnswers.addView(addAnswerToDialog(i, answer[0]));
                    EditText newRowEt = (EditText) layout.findViewWithTag("etDialogAnswer" + i);
                    Switch newRowSw = (Switch) layout.findViewWithTag("swDialogSwitch" + i);
                    newRowSw.setChecked(answerCorrect);
                    newRowEt.setText(answerText);
                }
            }

        } else {
            //new text - sets the answer rows' id to 0...
            bDialogDelete.setEnabled(false);
            LLAnswers.getChildAt(0).setTag(R.id.ANSWER_ID_TAG, "0");
            LLAnswers.getChildAt(1).setTag(R.id.ANSWER_ID_TAG, "0");
        }

        bDialogSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etDialogAnswer0.getText().toString().equals("") && !etDialogAnswer0.getText().toString().equals("") && !etDialogQuestion.getText().toString().equals("")) {
                    String answerString = "";
                    int correctanswers = 0;
                    int childcount = getChildCount(LLAnswers)-1;

                    for (int i = 0; i <= childcount; i++) {
                        EditText etAnswerText = (EditText) layout.findViewWithTag("etDialogAnswer" + i);
                        Switch swAnswerSwitch = (Switch) layout.findViewWithTag("swDialogSwitch" + i);
                        String answerId = null;
                        String answerCorrect;
                        String answerText;
                        String answer;


                        View answerRow = LLAnswers.getChildAt(i);
                        String tag = (String) answerRow.getTag(R.id.ANSWER_ID_TAG);
                        if(tag != null) {
                            answerId = tag;
                        }


                        if (swAnswerSwitch.isChecked()) {
                            answerCorrect = "1";
                            correctanswers++;
                        } else {
                            answerCorrect = "0";
                        }
                        answerText = etAnswerText.getText().toString();
                        answer = answerId + ";" + answerText + ";" + answerCorrect;


                        if(!answerText.equals("")){
                            if(answerString.equals("")){
                                answerString = answer;
                            } else {
                                answerString = answerString + "#" + answer;
                            }
                        }
                    }

                    if (correctanswers > 1) {
                        int duration = Toast.LENGTH_LONG;
                        CharSequence alert = "Please add only one correct answer";
                        Toast toast = Toast.makeText(context, alert, duration);
                        toast.show();
                    } else if (correctanswers < 1) {
                        int duration = Toast.LENGTH_LONG;
                        CharSequence alert = "Please add one correct answer";
                        Toast toast = Toast.makeText(context, alert, duration);
                        toast.show();
                    } else {
                        String method;
                        String questionId;
                        if (position >= 0) {
                            //update
                            method = "update";
                            questionId = questionList.get(position).get("id");
                        } else {
                            //create
                            method = "create";
                            questionId = "";
                        }
                        new QuestionTask(new Callback() {
                            @Override
                            public void asyncDone(HashMap<String, HashMap<String, String>> results) {

                            }
                        }, context).executeTask(method, questionId, textId, answerString, etDialogQuestion.getText().toString());
                        dialog.dismiss();
                        if(newText){
                            setChanged(true);
                        }
                        getQuestions(textId);
                    }
                } else {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "Please add a question and two answers";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }

            }
        });

        bDialogAddAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LLAnswers.addView(addAnswerToDialog(getChildCount(LLAnswers), "0"));
            }
        });
        bDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to delete the question and all related answers")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogCallback, int which) {
                                new QuestionTask(new Callback() {
                                    @Override
                                    public void asyncDone(HashMap<String, HashMap<String, String>> results) {
                                        dialog.dismiss();
                                        getQuestions(textId);
                                    }
                                }, context).executeTask("delete", questionList.get(position).get("id"), "", "", "");
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private int getChildCount(LinearLayout parent) {
        return parent.getChildCount();
    }
}





























