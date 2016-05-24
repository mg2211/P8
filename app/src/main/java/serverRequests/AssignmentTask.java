package serverRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
 * AssignmentTask
 * Used for assigning, deleting and reading and updating assignments for students
 */
public class AssignmentTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    /*Callback interface*/
    private final Callback delegate;
    /*Declaring a ProgressDialog*/
    private final ProgressDialog progressDialog;
    /*The caller activity*/
    private final Context context;

    /**
     * Constructor
     * @param delegate - The Callback interface
     * @param context - The Caller activity
     */
    public AssignmentTask(Callback delegate, Context context){
        this.delegate = delegate;
        this.context = context;
        /*Creating and setting a progressdialog*/
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    /**
     * Helper method
     * @param method - The method to send the server
     * @param studentId - The studentID
     * @param assignmentLibId - The AssignmentLibraryId
     * @param from - Avaiable from timestamp
     * @param to - Avaiable to timestamp
     * @param assignmentId - The assignmentId
     */
    public void executeTask(String method, String studentId, String assignmentLibId, String from, String to, String assignmentId){
        this.execute(method, studentId, assignmentLibId, from, to, assignmentId);
    }

    /**
     * doInBackground handles the connection to the server
     * @param params - String array of parameters either sent from the executeTask method or directly to the class
     * @return HashMap of results
     */
    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {

        /*Getting params*/
        String method = params[0];
        String studentId = params[1];
        String assignmentLibId = params[2];
        String from = params[3];
        String to = params[4];
        String id = params[5];

        /*Initiating response variables*/
        String generalResponse = null;
        int responseCode = 0;

        /*Creating the results HashMap to be returned*/
        HashMap<String, HashMap<String, String>> results = new HashMap<>();

        try {
            /*Creating the actual server call*/
            URL url = new URL(ServerAddress.url+"assignments.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            /*Building URI with POST parameters*/
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

            /*Catch server response*/
            InputStream in = new BufferedInputStream(connection.getInputStream());

            String response = IOUtils.toString(in, "UTF-8");

            /*convert to JSON object*/
            JSONObject JSONResult = new JSONObject(response);
            /*Getting response from the JSON*/
            generalResponse = JSONResult.getString("generalResponse");
            responseCode = JSONResult.getInt("responseCode");
            /*If method is "get", parse the JSON to get assignments*/


            if(method.equals("get")) {
                /*Get the array of assignments from JSON*/
                JSONArray assignments = JSONResult.getJSONArray("assignments");
                /*Iterate the array*/
                for (int i = 0; i < assignments.length(); i++) {
                    /*Getting the specific assignment in the array*/
                    JSONObject specificAssignment = assignments.getJSONObject(i);

                    /*Getting all information from the array*/
                    String assLibId = String.valueOf(specificAssignment.getInt("assignmentlibraryid"));
                    String assignmentId = String.valueOf(specificAssignment.getInt("id"));
                    String assignmentName = specificAssignment.getString("assignmentName");
                    String textId = specificAssignment.getString("textId");
                    String assignmentFrom = specificAssignment.getString("from");
                    String assignmentTo = specificAssignment.getString("to");
                    String assignmentStudentId = specificAssignment.getString("studentId");
                    String isComplete = specificAssignment.getString("isComplete");
                    String timeSpent = specificAssignment.getString("timeSpent");

                    /*Creating a map for containing information and put data in it*/
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

                    /*Put the map created into the results HashMap*/
                    results.put("AssignmentId: " + assignmentId, assignmentInfo);
                }
            }


        } catch (IOException e) {
            responseCode = 300;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*Creating a HashMap for containing the response variables*/
        HashMap<String, String> response = new HashMap<>();
        /*Putting the response in the response map*/
        response.put("generalResponse", generalResponse);
        response.put("responseCode", String.valueOf(responseCode));
        /*Putting the response map into the results HashMap*/
        results.put("response", response);

        return results;
    }

    /**
     * Handles the results from doInBackground
     * @param results returned from doInBackground
     */
    protected void onPostExecute(HashMap<String, HashMap<String, String>> results) {
        /*getting the response variables*/
        String generalResponse = results.get("response").get("generalResponse");
        String responseCode = results.get("response").get("responseCode");
        /*Dismisses the progressdialog*/
        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            /*Everything okay - removing the reponse from the results map*/
            results.remove("response");
            /*Sending the result back to the caller activity via the Callback interface*/
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) == 101) {
            /*Something went wrong database side, show a toast saying so*/
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            /*Everything okay - removing the reponse from the results map*/
            results.remove("response");
            /*Sending the result back to the caller activity via the Callback interface*/
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) > 101) {
            /*If the doInBackground fails, a toast saying so is shown*/
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Server connection failed - Please try again later";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
    }
}