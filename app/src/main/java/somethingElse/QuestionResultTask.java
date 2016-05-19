package somethingElse;

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
 * Created by Ivo on 19-5-2016.
 */
public class QuestionResultTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {
    Context context;
    ProgressDialog progressDialog;
    Callback delegate;

    public QuestionResultTask(Callback delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {

        String assignmentid = params[0];
        String questionid = params[1];
        String questionresultid = params[2];
        String answerid = params[3];
        String answeredcorrect = params[4];
        String iscomplete = params[5];
        String totaltimespent = params[6];
        String method = params[7];
        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        HashMap<String, String> response = new HashMap<>();

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
                    .appendQueryParameter("totaltimespent", totaltimespent);


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
            Log.d("SERVERRESPONSE", serverResponse);

            JSONObject JSONResult = new JSONObject(serverResponse);
            String generalResponse = JSONResult.getString("generalResponse");
            String responseCode = String.valueOf(JSONResult.getInt("responseCode"));
            response.put("generalResponse", generalResponse);
            response.put("responseCode", responseCode);

            if(method.equals("get")) {
                JSONArray questionResults = JSONResult.getJSONArray("results");
                for (int i = 0; i < questionResults.length(); i++) {
                    JSONObject result = questionResults.getJSONObject(i);
                    String correct = String.valueOf(result.getInt("correct"));
                    String questionId = String.valueOf(result.getInt("questionid"));
                    String answerText = result.getString("answerText");

                    HashMap<String, String> resultInfo = new HashMap<>();
                    resultInfo.put("correct", correct);
                    resultInfo.put("answerText",answerText);

                    results.put(""+questionId,resultInfo);
                }
            }

        } catch (IOException e) {
            response.put("generalResponse", "Server connection failed");
            response.put("responseCode", "300");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        results.put("response", response);
        return results;
    }

    protected void onPostExecute (HashMap<String, HashMap<String, String>> results){
        progressDialog.dismiss();
        int duration = Toast.LENGTH_LONG;
        CharSequence alert = results.get("response").get("generalResponse");
        Toast toast = Toast.makeText(context, alert, duration);
        toast.show();
        delegate.asyncDone(results);
    }
}