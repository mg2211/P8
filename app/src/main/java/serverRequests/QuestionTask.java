package serverRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import callback.Callback;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by ida803f16
 */

/**
 * QuestionTask - Used for Creating, Reading, Updating and Deleting questions
 */
public class QuestionTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    /*context*/
    private final Context context;
    /*Callback interface*/
    private final Callback delegate;
    /*Declaring a Prgressdialog*/
    private final ProgressDialog progressDialog;

    /**
     * Constructor
     * @param delegate the Callback interface
     * @param context - The caller activity
     */
    public QuestionTask(Callback delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
        /*Creating and setting the progressdialog*/
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    /**
     * Helper method
     * @param method - The method to send the server
     * @param questionId - The questionId
     * @param textId - The textId
     * @param answers - The String of answers
     * @param questionContent - The content of the question
     */
    public void executeTask(String method, String questionId, String textId, String answers, String questionContent) {
        /*Calls the doInBackground method*/
        this.execute(method, questionId, questionContent, answers, textId);
    }

    /**
     * doInBackground for calling the server
     * @param params String array of params sent from the executeTask
     * @return HashMap of results
     */
    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {
        /*Initiating results HashMap and response variables*/
        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        String generalResponse;
        int responseCode;
        /*Getting params*/
        String method = params[0];
        String questionId = params[1];
        String questionContent = params[2];
        String answers = params[3];
        String textId = params[4];

        try {
            /*Creating the server call*/
            URL url = new URL("http://emilsiegenfeldt.dk/p8/questions.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            /*Building the URI with POST parameters*/
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("method", method)
                    .appendQueryParameter("textId", textId)
                    .appendQueryParameter("id", questionId)
                    .appendQueryParameter("questionContent", questionContent)
                    .appendQueryParameter("questionId", questionId)
                    .appendQueryParameter("answers", answers);

            String query = builder.build().getEncodedQuery();
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            connection.connect();
            /*catch server response*/
            InputStream in = new BufferedInputStream(connection.getInputStream());

            String response = IOUtils.toString(in, "UTF-8");

            /*Convert to JSONObject*/
            JSONObject JSONResult = new JSONObject(response);
            generalResponse = JSONResult.getString("generalResponse");
            responseCode = JSONResult.getInt("responseCode");
            /*If the method is "get" parse the results*/
            if(method.equals("get")) {
                JSONArray questions = JSONResult.getJSONArray("questions");
                /*Iterate through results*/
                for (int i = 0; i < questions.length(); i++) {
                    HashMap<String, String> questionInfo = new HashMap<>();

                    JSONObject question = questions.getJSONObject(i);
                    String id = question.getString("id");
                    questionInfo.put("questionContent", question.getString("content"));
                    questionInfo.put("textId", question.getString("textId"));
                    questionInfo.put("questionId", question.getString("id"));

                    /*Getting the array of answers from the server and putting them into a string*/
                    JSONArray questionAnswers = question.getJSONArray("answers");
                    String questionAnswersString = "";
                    for(int n = 0; n<questionAnswers.length(); n++){
                        JSONObject answer = questionAnswers.getJSONObject(n);
                        String answerText = answer.getString("answertext");
                        String answerId = answer.getString("id");
                        String isCorrect = answer.getString("iscorrect");
                        String specificAnswer = answerId+";"+answerText+";"+isCorrect;
                        if(n == 0){
                            questionAnswersString =specificAnswer;
                        } else {
                            questionAnswersString = questionAnswersString+"#"+specificAnswer;
                        }
                    }

                    questionInfo.put("answers",questionAnswersString);
                    /*Putting the question info into the results*/
                    results.put("Question" + id, questionInfo);
                }
            }
            /*Creating a seperate HashMap for the response*/
            HashMap<String, String> serverResponse = new HashMap<>();
            serverResponse.put("generalResponse",generalResponse);
            serverResponse.put("responseCode", String.valueOf(responseCode));
            /*Putting the response map into the results*/
            results.put("response",serverResponse);

        } catch(IOException e){
            //server error
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Handles results from doInBackground
     * @param results returned from doInBackground
     */
    protected void onPostExecute(HashMap<String, HashMap<String, String>> results) {
        /*Dismisses progressdialog*/
        progressDialog.dismiss();

        String generalResponse = results.get("response").get("generalResponse");
        String responseCode = results.get("response").get("responseCode");

        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            /*Sending the results map to the caller activity via the Callback interface*/
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) == 101) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            /*Sending the results map to the caller activity via the Callback interface*/
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) > 101) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Response code " + responseCode + ", " + "Message: " + generalResponse, duration);
            toast.show();
        }
}
}
