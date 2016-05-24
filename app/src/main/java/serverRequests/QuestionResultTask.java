package serverRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * QuestionResultTask
 * Used for inserting and getting results for assignment
 */
public class QuestionResultTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, HashMap<String, String>>>> {
    /*Caller activity*/
    private final Context context;
    /*Declaring a ProgressDialog*/
    private final ProgressDialog progressDialog;

    /**
     * Constructor
     * @param context
     */
    public QuestionResultTask(Context context) {
        this.context = context;
        /*Creating and setting the progressdialog*/
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    /**
     *
     * @param params string array of parameters sent to the class
     * @return HashMap<String, HashMap<String, HashMap<String, String>>> - with the results of an assignment
     */
    @Override
    protected HashMap<String, HashMap<String, HashMap<String, String>>> doInBackground(String... params) {

        /*Getting params*/
        String assignmentid = params[0];
        String questionid = params[1];
        String questionresultid = params[2];
        String answerid = params[3];
        String answeredcorrect = params[4];
        String iscomplete = params[5];
        String totaltimespent = params[6];
        String method = params[7];
        String assignmentLibraryid = params[8];

        /*Initiating response maps*/
        HashMap<String, String> responseMap = new HashMap<>();
        HashMap<String,HashMap<String, HashMap<String, String>>> results = new HashMap<>();
        HashMap<String,HashMap<String, String>> response = new HashMap<>();

        try {
            /*Creating the actual server call*/
            URL url = new URL(ServerAddress.url+"questionresults.php");

            /*Building the URI using POST parameters*/
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

            /*Catching the server response*/
            InputStream in = new BufferedInputStream(connection.getInputStream());

            String serverResponse = IOUtils.toString(in, "UTF-8");

            /*Converting to JSONObject*/
            JSONObject JSONResult = new JSONObject(serverResponse);
            /*Getting the server responseCode and generalResponse*/
            String generalResponse = JSONResult.getString("generalResponse");
            String responseCode = String.valueOf(JSONResult.getInt("responseCode"));
            /*Putting response into appropriate map*/
            responseMap.put("generalResponse", generalResponse);
            responseMap.put("responseCode", responseCode);

            /*If the method is "get", parse the results for assignmets*/
            if(method.equals("get")) {
                JSONArray assignmentResults = JSONResult.getJSONArray("results");

                /*Iterate through the list of assignments*/
                for(int i=0; i<assignmentResults.length(); i++) {
                    /*Getting the actual assignment array from the JSON*/
                    JSONObject assignment = assignmentResults.getJSONObject(i);
                    /*Getting time and ID*/
                    String assignmentTime = assignment.getString("time");
                    String assignmentId = assignment.getString("id");
                    /*Creating a map of time and putting the assignment time in*/
                    HashMap<String, String> time = new HashMap<>();
                    time.put("time", assignmentTime);

                    /*Creating a map of the assignment's results*/
                    HashMap<String, HashMap<String, String>> assignmentResult = new HashMap<>();

                    /*Removing time and id from the original HashMap*/
                    assignment.remove("time");
                    assignment.remove("id");

                    /*Checking if there is anything left i.e. the assignments has questions*/
                    if (assignment.names() != null) {
                        /*Iterating the question results*/
                        for (int n = 0; n < assignment.names().length(); n++) {
                            /*Getting the question id - the key for the array of question results */
                            String questionId = String.valueOf(assignment.names().get(n));
                            /*Getting the array of results for that question*/
                            JSONObject questionResult = assignment.getJSONObject(questionId);

                            /*Creating a map for containing results for a question*/
                            HashMap<String, String> specificQuestionResult = new HashMap<>();

                            /*Getting the information*/
                            String answerId = questionResult.getString("answerid");
                            String answerCorrect = questionResult.getString("answered correct");
                            String answerContent = questionResult.getString("answercontent");
                            /*Putting in the information*/
                            specificQuestionResult.put("answerId", answerId);
                            specificQuestionResult.put("correct", answerCorrect);
                            specificQuestionResult.put("answerContent", answerContent);

                            /*Putting the specific question result in the assignmentResult HashMap*/
                            assignmentResult.put(questionId, specificQuestionResult);
                        }
                    }
                        /*Putting in the time for the assignment*/
                        assignmentResult.put("time", time);
                        /*Putting the assignment's result into the general result HashMap*/
                        results.put(assignmentId, assignmentResult);
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
        return results;
    }

    /**
     * onPostExecute to handle the response
     * @param results returned from the doInBackground
     */
    protected void onPostExecute (HashMap<String, HashMap<String, HashMap<String,String>>> results){
        /*Dismisses the progressdialog*/
        progressDialog.dismiss();
        /*Getting the response code and generalResponse and showing generalResponse in a toast*/
        HashMap<String, HashMap<String, String>> response = results.get("response");
        int duration = Toast.LENGTH_LONG;
        CharSequence alert = response.get("response").get("generalResponse");
        Toast toast = Toast.makeText(context, alert, duration);
        toast.show();

    }
}