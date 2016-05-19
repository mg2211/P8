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
public class AssignmentLibTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    private final Context context;
    private final Callback delegate;
    private final ProgressDialog progressDialog;

    public AssignmentLibTask(Callback delegate, Context context){
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }
    public void executeTask(String method, String teacherId, String assignmentId, String assignmentName, String textId){
        this.execute(method, teacherId, assignmentId, assignmentName, textId);
    }
    @Override
    protected HashMap <String, HashMap<String, String>> doInBackground(String... params) {
        String method = params[0];
        String teacherId = params[1];
        String assignmentId = params[2];
        String assignmentName = params[3];
        String textId = params[4];

        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        HashMap<String, String> response = new HashMap<>();
        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/assignments.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("method", method)
                    .appendQueryParameter("assignmentLibId",assignmentId)
                    .appendQueryParameter("teacherId", teacherId)
                    .appendQueryParameter("assignmentLibName", assignmentName)
                    .appendQueryParameter("textId",textId);

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

            Log.d("serverresponse",serverResponse);

            JSONObject JSONResult = new JSONObject(serverResponse);
            String generalResponse = JSONResult.getString("generalResponse");
            String responseCode = String.valueOf(JSONResult.getInt("responseCode"));

            response.put("generalResponse", generalResponse);
            response.put("responseCode", responseCode);

            if(method.equals("get")){
                JSONArray assignments = JSONResult.getJSONArray("assignments");
                for (int i = 0; i < assignments.length(); i++) {
                    JSONObject specificAssignment = assignments.getJSONObject(i);

                    HashMap<String, String> assignmentInfo = new HashMap<>();
                    assignmentInfo.put("id",specificAssignment.getString("id"));
                    assignmentInfo.put("name",specificAssignment.getString("name"));
                    assignmentInfo.put("textId", specificAssignment.getString("text"));
                    assignmentInfo.put("teacherId",specificAssignment.getString("teacherId"));
                    assignmentInfo.put("assignedStudents",specificAssignment.getString("assignedStudents"));
                    assignmentInfo.put("assignmentIds", specificAssignment.getString("assignmentIds"));
                    assignmentInfo.put("isComplete",specificAssignment.getString("isComplete"));
                    assignmentInfo.put("assignmentTimes",specificAssignment.getString("assignmentTimes"));

                    results.put("Assignment id" + specificAssignment.getString("id"), assignmentInfo);
                }
            }
            if(method.equals("create")){
                response.put("insertedId",JSONResult.getString("insertedId"));
            }

        } catch (IOException e){
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