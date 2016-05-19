package somethingElse;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by Ivo on 19-5-2016.
 */
public class QuestionTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    Callback delegate;
    private final Context context;
    ProgressDialog progressDialog;

    public QuestionTask(Callback delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    public void executeTask(String method, String questionId, String textId, String answers, String questionContent) {
        this.execute(method, questionId, questionContent, answers, textId);
    }

    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {
        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        String generalResponse;
        int responseCode;
        String method = params[0];
        String questionId = params[1];
        String questionContent = params[2];
        String answers = params[3];
        String textId = params[4];

        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/questions.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

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
            //catch server response
            InputStream in = new BufferedInputStream(connection.getInputStream());

            String response = IOUtils.toString(in, "UTF-8");

            JSONObject JSONResult = new JSONObject(response);
            generalResponse = JSONResult.getString("generalResponse");
            responseCode = JSONResult.getInt("responseCode");
            if(method.equals("get")) {
                JSONArray questions = JSONResult.getJSONArray("questions");
                for (int i = 0; i < questions.length(); i++) {
                    HashMap<String, String> questionInfo = new HashMap<>();

                    JSONObject question = questions.getJSONObject(i);
                    String id = question.getString("id");
                    questionInfo.put("questionContent", question.getString("content"));
                    questionInfo.put("textId", question.getString("textId"));
                    questionInfo.put("questionId", question.getString("id"));
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
                    results.put("Question" + id, questionInfo);
                }
            }
            HashMap<String, String> serverResponse = new HashMap<>();
            serverResponse.put("generalResponse",generalResponse);
            serverResponse.put("responseCode", String.valueOf(responseCode));
            results.put("response",serverResponse);

        } catch(IOException e){
            //server error
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    protected void onPostExecute(HashMap<String, HashMap<String, String>> results) {
        Log.d("results",results.toString());
        progressDialog.dismiss();
        delegate.asyncDone(results);
    }
}
