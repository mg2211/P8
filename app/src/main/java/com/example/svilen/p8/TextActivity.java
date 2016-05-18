package com.example.svilen.p8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextActivity extends AppCompatActivity {

    private EditText etContent;

    private Context context = this;
    private ListView lvTexts;
    private List<Map<String, String>> textList = new ArrayList<>();
    private ArrayList<Integer> colors = new ArrayList<>();
    private ListViewAdapter textAdapter;
    private ListView lvQuestions;
    private List<Map<String, String>> questionList = new ArrayList<>();
    private SimpleAdapter questionAdapter;
    private Button bAddText;
    private Button bDelete;
    private Button bSave;
    private Button bAddQuestion;
    private EditText etTextName;
    private TextView tvComplexity;
    private EditText etSearch;
    private String textContent;
    private String textId;
    private String textName;
    private boolean newText;
    private boolean changed;
    private double lix;
    private boolean clear;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);


        bSave = (Button) findViewById(R.id.bSave);
        lvTexts = (ListView) findViewById(R.id.lvTexts);
        etContent = (EditText) findViewById(R.id.etContent);
        bAddText = (Button) findViewById(R.id.bAddText);
        bDelete = (Button) findViewById(R.id.bDelete);
        etTextName = (EditText) findViewById(R.id.etTextname);
        tvComplexity = (TextView) findViewById(R.id.tvComplexity);
        etSearch = (EditText) findViewById(R.id.etSearch);
        bDelete.setEnabled(false);
        bAddQuestion = (Button) findViewById(R.id.bAddQuestion);

        lvQuestions = (ListView) findViewById(R.id.lvQuestions);
        questionAdapter = new SimpleAdapter(this, questionList,
                android.R.layout.simple_list_item_1,
                new String[]{"Question"},
                new int[]{android.R.id.text1});
        lvQuestions.setAdapter(questionAdapter);

        lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                questionDialog(position);
            }
        });
        bAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionDialog(-1);
            }
        });

        setNewText(true);
        setChanged(false);
        getTexts();
        setContentPane(-1);
        textAdapter = new ListViewAdapter(this, textList, android.R.layout.simple_list_item_2, new String[]{"textname", "complexity"}, new int[]{android.R.id.text1, android.R.id.text2}, colors);

        lvTexts.setAdapter(textAdapter);
        lvTexts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
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
                                   new QuestionTask(new QuestionCallback() {
                                       @Override
                                       public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {

                                       }
                                   }, context).executeTask("delete", "", textId, "", "");
                               }
                            } if (clear) {
                                setContentPane(position);

                            }
                        }
                    });

                } else {
                    setContentPane(position);
                }
            }
        });


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
                                    new QuestionTask(new QuestionCallback() {
                                        @Override
                                        public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {

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

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteText();
            }
        });

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

    private void calculate() {
        String inputText = etContent.getText().toString();
        String cleanText;
        int P = StringUtils.countMatches(inputText, ".");
        P = P + StringUtils.countMatches(inputText, "?");
        P = P + StringUtils.countMatches(inputText, "!");
        P = P + StringUtils.countMatches(inputText, ":");
        P = P + StringUtils.countMatches(inputText, ";");
        cleanText = inputText.replaceAll("/./", "");
        cleanText = cleanText.replaceAll("/?/", "");
        cleanText = cleanText.replaceAll("/!/", "");
        cleanText = cleanText.replaceAll("/:/", "");
        cleanText = cleanText.replaceAll("/;/", "");
        String[] words = cleanText.split("\\s+");
        int O = words.length;
        if (O > 0 && P > 0) {
            int L = 0;
            for (int i = 0; i < words.length; i++) {
                if (words[i].length() > 6) {
                    L++;
                }
            }
            lix = (O / P) + (L * 100 / O);
            tvComplexity.setText(String.valueOf(lix));
        } else {
            tvComplexity.setText("");
        }
    }

    private void getTexts() {
        new TextTask(new TextCallback() {
            @Override
            public void TextCallBack(HashMap<String, HashMap<String, String>> results) {
                results.remove("response");
                textList.clear();
                colors.clear();
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
                textAdapter.notifyDataSetChanged();
            }
        },context).executeTask("get","","","",0);
    }

    private boolean createText() {

        if (!etTextName.getText().toString().equals("") && !etContent.getText().toString().equals("")) {

            new TextTask(new TextCallback() {
                @Override
                public void TextCallBack(HashMap<String, HashMap<String, String>> results) {
                   String id = results.get("response").get("insertedId");
                    if (questionList.size() > 0) {
                        for (int i = 0; i < questionList.size(); i++) {
                            String questionId = questionList.get(i).get("id");
                            new QuestionTask(new QuestionCallback() {
                                @Override
                                public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {
                                }
                            }, context).executeTask("update", questionId, id, questionList.get(i).get("answers"), questionList.get(i).get("Question"));
                        }
                    }
                }
            },context).executeTask("create", "", etTextName.getText().toString(), etContent.getText().toString(), lix);
            getTexts();
            return true;
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }

    }

    private boolean updateText() {
        if (!etTextName.getText().toString().equals("") && !etContent.getText().toString().equals("")) {

            new TextTask(new TextCallback() {
                @Override
                public void TextCallBack(HashMap<String, HashMap<String, String>> results) {
                    if (questionList.size() > 0) {
                        for (int i = 0; i < questionList.size(); i++) {
                            String questionId = questionList.get(i).get("id");
                            new QuestionTask(new QuestionCallback() {
                                @Override
                                public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {
                                }
                            }, context).executeTask("update", questionId, textId, questionList.get(i).get("answers"), questionList.get(i).get("Question"));
                        }
                    }
                }
            },context).executeTask("update", textId, etTextName.getText().toString(), etContent.getText().toString(), lix);
            getTexts();
            return true;
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill in all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
            return false;
        }

    }

    private void deleteText() {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete the text " + textName)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new TextTask(new TextCallback() {
                            @Override
                            public void TextCallBack(HashMap<String, HashMap<String, String>> results) {
                                new QuestionTask(new QuestionCallback() {//delete questions
                                    @Override
                                    public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {
                                    }
                                }, context).executeTask("delete", "", textId, "", ""); //delete the questions after the text is deleted
                            }
                        },context).executeTask("delete", textId, "", "", 0); //delete the text

                        getTexts();
                        setContentPane(-1);
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    private void setNewText(boolean value) {
        newText = value;
    }

    private void setChanged(boolean value) {
        changed = value;
    }

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

    //@param position - the position from the listview - pass -1 for new text
    private void setContentPane(int position) {
        if (position >= 0) {
            Map<String, String> textData = textList.get(position);
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

    private void getQuestions(String textId) {
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
                }
                questionAdapter.notifyDataSetChanged();
            }
        }, context).executeTask("get", "", textId, "", "");
    }

    private int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    private View addAnswerToDialog(int childNo, String id) {
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
                        new QuestionTask(new QuestionCallback() {
                            @Override
                            public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {

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
                                new QuestionTask(new QuestionCallback() {
                                    @Override
                                    public void QuestionTaskDone(HashMap<String, HashMap<String, String>> results) {
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





























