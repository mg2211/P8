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
 * AssignmentLibTask
 * Used for Creating, Reading and updating assignment library entries
 */
public class AssignmentLibTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    /*The caller activity*/
    private final Context context;
    /*The Callback interface*/
    private final Callback delegate;
    /*Declaring a progressdialog*/
    private final ProgressDialog progressDialog;

    /**
     * Constructor
     * @param delegate - the Callback interface
     * @param context - caller activity
     */
    public AssignmentLibTask(Callback delegate, Context context){
        this.delegate = delegate;
        this.context = context;
        /*Creating and setting the progressdialog*/
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    /**
     * Helper method
     * @param teacherId - The id of the teacher
     * @param assignmentId - The assignmentLibraryId
     * @param assignmentName - The assignmentLibraryName
     * @param textId - The assignmentLibraryTextId
     */
    public void executeTask(String teacherId, String assignmentId, String assignmentName, String textId){
        this.execute("update", teacherId, assignmentId, assignmentName, textId);
    }

    /**
     * doInBackground - used for handling the actual server call
     * @param params - either sent directly to the method or via the executeTask method
     * @return HashMap with the results
     */
    @Override
    protected HashMap <String, HashMap<String, String>> doInBackground(String... params) {
        /*Getting params*/
        String method = params[0];
        String teacherId = params[1];
        String assignmentId = params[2];
        String assignmentName = params[3];
        String textId = params[4];

        /*Initiating results and response maps*/
        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        HashMap<String, String> response = new HashMap<>();
        try {
            /*Creating the server call*/
            URL url = new URL(ServerAddress.url+"assignmentlibs.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            /*Building the URI with POST parameters*/
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

            /*Catches server response*/
            InputStream in = new BufferedInputStream(connection.getInputStream());

            String serverResponse = IOUtils.toString(in, "UTF-8");

            /*Converting server response to JSONObject*/
            JSONObject JSONResult = new JSONObject(serverResponse);

            /*Getting response from JSON*/
            String generalResponse = JSONResult.getString("generalResponse");
            String responseCode = String.valueOf(JSONResult.getInt("responseCode"));

            /*Putting response in map*/
            response.put("generalResponse", generalResponse);
            response.put("responseCode", responseCode);

            /*If method is "get", parse the JSON*/
            if(method.equals("get")){
                /*Getting the assignments array*/
                JSONArray assignments = JSONResult.getJSONArray("assignments");
                /*Iterate through assignments array*/
                for (int i = 0; i < assignments.length(); i++) {
                    /*Getting a specific assignment library entry*/
                    JSONObject specificAssignment = assignments.getJSONObject(i);

                    /*Creating a map for containing information and putting data in*/
                    HashMap<String, String> assignmentInfo = new HashMap<>();
                    assignmentInfo.put("id",specificAssignment.getString("id"));
                    assignmentInfo.put("name",specificAssignment.getString("name"));
                    assignmentInfo.put("textId", specificAssignment.getString("text"));
                    assignmentInfo.put("teacherId",specificAssignment.getString("teacherId"));
                    /*Putting map into results map with assignment id as key*/

                    results.put("Assignment id" + specificAssignment.getString("id"), assignmentInfo);
                }
            }
            /*If the method is "create" the only response being put in the results map is the inserted id*/
            if(method.equals("create")){
                response.put("insertedId",JSONResult.getString("insertedId"));
            }

        } catch (IOException e){
            response.put("generalResponse", "Server connection failed");
            response.put("responseCode", "300");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*Putting in the server response*/
        results.put("response", response);
        return results;
    }

    /**
     * Handles results from doInBackground
     * @param results returned from doInBackground
     */
    protected void onPostExecute (HashMap<String, HashMap<String, String>> results){
        String generalResponse = results.get("response").get("generalResponse");
        String responseCode = results.get("response").get("responseCode");

        /*Dismisses progressdialog*/
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