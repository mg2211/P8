package serverRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.svilen.p8.Callback;

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
public class AssignmentTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    private final Callback delegate;
    private final ProgressDialog progressDialog;
    private final Context context;

    public AssignmentTask(Callback delegate, Context context){
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }
    public void executeTask(String method, String studentId, String assignmentLibId, String from, String to, String assignmentId){
        this.execute(method, studentId, assignmentLibId, from, to, assignmentId);
    }

    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {

        String method = params[0];
        String studentId = params[1];
        String assignmentLibId = params[2];
        String from = params[3];
        String to = params[4];
        String id = params[5];
        String generalResponse = null;
        int responseCode = 0;

        HashMap<String, HashMap<String, String>> results = new HashMap<>();

        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/assignment.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("studentId", studentId)
                    .appendQueryParameter("assignmentlibraryid",assignmentLibId)
                    .appendQueryParameter("method",method)
                    .appendQueryParameter("from",from)
                    .appendQueryParameter("to",to)
                    .appendQueryParameter("id",id);

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
            //convert to JSON object
            Log.d("response",response);

            JSONObject JSONResult = new JSONObject(response);
            generalResponse = JSONResult.getString("generalResponse");
            responseCode = JSONResult.getInt("responseCode");
            if(method.equals("get")) {
                JSONArray assignments = JSONResult.getJSONArray("assignments");
                for (int i = 0; i < assignments.length(); i++) {
                    JSONObject specificAssignment = assignments.getJSONObject(i);
                    String assLibId = String.valueOf(specificAssignment.getInt("assignmentlibraryid"));
                    String assignmentId = String.valueOf(specificAssignment.getInt("id"));
                    String assignmentName = specificAssignment.getString("assignmentName");
                    String textId = specificAssignment.getString("textId");
                    String assignmentFrom = specificAssignment.getString("from");
                    String assignmentTo = specificAssignment.getString("to");
                    String assignmentStudentId = specificAssignment.getString("studentId");
                    String isComplete = specificAssignment.getString("isComplete");
                    String timeSpent = specificAssignment.getString("timeSpent");

                    HashMap<String, String> assignmentInfo = new HashMap<>();
                    assignmentInfo.put("assignmentlibraryid", assLibId);
                    assignmentInfo.put("assignmentid", assignmentId);
                    assignmentInfo.put("assignmentLibName", assignmentName);
                    assignmentInfo.put("textId", textId);
                    assignmentInfo.put("availableFrom", assignmentFrom);
                    assignmentInfo.put("availableTo", assignmentTo);
                    assignmentInfo.put("studentId", assignmentStudentId);
                    assignmentInfo.put("isComplete", isComplete);
                    assignmentInfo.put("timeSpent",timeSpent);

                    results.put("AssignmentId: " + assignmentId, assignmentInfo);
                }
            }


        } catch (IOException e) {
            responseCode = 300;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> response = new HashMap<>();
        response.put("generalResponse", generalResponse);
        response.put("responseCode", String.valueOf(responseCode));
        results.put("response", response);

        return results;
    }
    protected void onPostExecute(HashMap<String, HashMap<String, String>> results) {
        String generalResponse = results.get("response").get("generalResponse");
        String responseCode = results.get("response").get("responseCode");
        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            //everything Okay
            results.remove("response");
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) == 200) {
            //Something went wrong database side
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
        } else if (Integer.parseInt(responseCode) == 300) {
            //Server connection error
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Server connection failed - Please try again later";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
    }

}