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
import java.util.Iterator;

/**
 * Created by Ivo on 19-5-2016.
 */
public class QuestionResultTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, HashMap<String, String>>>> {
    Context context;
    ProgressDialog progressDialog;

    public QuestionResultTask(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    @Override
    protected HashMap<String, HashMap<String, HashMap<String, String>>> doInBackground(String... params) {

        String assignmentid = params[0];
        String questionid = params[1];
        String questionresultid = params[2];
        String answerid = params[3];
        String answeredcorrect = params[4];
        String iscomplete = params[5];
        String totaltimespent = params[6];
        String method = params[7];
        String assignmentLibraryid = params[8];
        HashMap<String, String> responseMap = new HashMap<>();
        HashMap<String,HashMap<String, HashMap<String, String>>> results = new HashMap<>();
        HashMap<String,HashMap<String, String>> response = new HashMap<>();

        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/questionresults.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("method",method)
                    .appendQueryParameter("assignmentid", assignmentid)
                    .appendQueryParameter("questionid", questionid)
                    .appendQueryParameter("id", questionresultid)
                    .appendQueryParameter("answerid", answerid)
                    .appendQueryParameter("answeredcorrect", answeredcorrect)
                    .appendQueryParameter("iscomplete", iscomplete)
                    .appendQueryParameter("totaltimespent", totaltimespent)
                    .appendQueryParameter("assignmentLibraryId",assignmentLibraryid);


            String query = builder.build().getEncodedQuery();
            OutputStream os = connection.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            connection.connect();

            InputStream in = new BufferedInputStream(connection.getInputStream());

            String serverResponse = IOUtils.toString(in, "UTF-8");

            JSONObject JSONResult = new JSONObject(serverResponse);
            String generalResponse = JSONResult.getString("generalResponse");
            String responseCode = String.valueOf(JSONResult.getInt("responseCode"));
            responseMap.put("generalResponse", generalResponse);
            responseMap.put("responseCode", responseCode);

            if(method.equals("get")) {
                JSONArray assignmentResults = JSONResult.getJSONArray("results");
                for(int i=0; i<assignmentResults.length(); i++){
                   JSONObject assignment = assignmentResults.getJSONObject(i);
                    String assignmentTime = assignment.getString("time");
                    String assignmentId = assignment.getString("id");
                    HashMap<String, String> time = new HashMap<>();
                    time.put("time",assignmentTime);

                    HashMap<String, HashMap<String, String>> assignmentResult = new HashMap<>();

                    assignment.remove("time");
                    assignment.remove("id");


                    for(int n =0; n<assignment.names().length(); n++){
                        String questionId = String.valueOf(assignment.names().get(n));
                        JSONObject questionResult = assignment.getJSONObject(questionId);


                        HashMap<String, String> specificQuestionResult = new HashMap<>();
                        String answerId = questionResult.getString("answerid");
                        String answerCorrect = questionResult.getString("answered correct");
                        String answerContent = questionResult.getString("answercontent");

                        specificQuestionResult.put("answerId",answerId);
                        specificQuestionResult.put("correct",answerCorrect);
                        specificQuestionResult.put("answerContent",answerContent);
                        assignmentResult.put(questionId,specificQuestionResult);
                    }
                    assignmentResult.put("time",time);
                    results.put(assignmentId,assignmentResult);
                }

            }

        } catch (IOException e) {
            responseMap.put("generalResponse", "Server connection failed");
            responseMap.put("responseCode", "300");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.put("response",responseMap);
        results.put("response",response);
        Log.d("results",results.toString());
        return results;
    }

    protected void onPostExecute (HashMap<String, HashMap<String, HashMap<String,String>>> results){
        progressDialog.dismiss();
        HashMap<String, HashMap<String, String>> response = results.get("response");
        int duration = Toast.LENGTH_LONG;
        CharSequence alert = response.get("response").get("generalResponse");
        Toast toast = Toast.makeText(context, alert, duration);
        toast.show();

    }
}