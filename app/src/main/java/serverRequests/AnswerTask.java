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
public class AnswerTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    private final Context context;
    private final Callback delegate;
    private final ProgressDialog progressDialog;

    public AnswerTask (Callback delegate, Context context){
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

        String questionid = params[0];
        String answertext = params[1];
        String id = params[2];
        String generalResponse = null;
        int responseCode = 0;

        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        Log.d("5555 ", "5555 ");

        try {

            URL url = new URL("http://emilsiegenfeldt.dk/p8/answers.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("questionid", questionid)
                    .appendQueryParameter("answertext", answertext)
                    .appendQueryParameter("id", id);

            String query = builder.build().getEncodedQuery();
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            connection.connect();

            //Catch server response
            InputStream in = new BufferedInputStream(connection.getInputStream());

            String response = IOUtils.toString(in, "UTF-8"); //convert to readable string

            Log.d("3434", response);
            //convert to JSON object
            JSONObject JSONResult = new JSONObject(response);
            generalResponse = JSONResult.getString("generalResponse");
            responseCode = JSONResult.getInt("responseCode");

            JSONArray answer = JSONResult.getJSONArray("answer");
            for (int i = 0; i< answer.length(); i++){

                JSONObject specificAnswer = answer.getJSONObject(i);
                String answerId = specificAnswer.getString("id");

                HashMap<String, String> answerInfo = new HashMap<>();

                answerInfo.put("id", answerId);

                results.put("AnswerId",  answerInfo);

                Log.d("1212",answerId );
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> response = new HashMap<>();
        response.put("generalResponse", generalResponse);
        response.put("responseCode", String.valueOf(responseCode));
        results.put("response", response);
        return results;
    }

    protected void onPostExecute (HashMap<String, HashMap<String, String>> results){
        String generalResponse = results.get("response").get("generalResponse");
        String responseCode = results.get("response").get("responseCode");

        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            /** if 100 - all is fine, delegate */
            /*Sending results back to caller activity via the Callback interface*/
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) == 101) {
            /** if 101 - response is result of CREATE, UPDATE or DELETE method. Toast a message and delegate */
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            /*Sending results back to caller activity via the Callback interface*/
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) > 101) {
            /** if > 101 - Something went wrong. Toast a message */
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Response code " + responseCode + ", " + "Message: " + generalResponse, duration);
            toast.show();
        } else {
            /** if no response - Something went wrong. Toast a message */
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Server connection failed - Please try again later";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
    }
}
