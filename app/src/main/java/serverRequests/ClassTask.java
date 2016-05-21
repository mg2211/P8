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
 *
 * The ClassTask class allows for getting class data from and updating data in the database asynchronously
 */
public class ClassTask extends AsyncTask<String, Void, HashMap<String,HashMap<String, String>>> {

    /** callback */
    private final Callback delegate;

    /** context */
    private final Context context;

    /** dialog showing progress during task execution */
    private final ProgressDialog progressDialog;


    /**
     * classTask constructor
     *
     * @param delegate the result is delegated to
     * @param context the execution context
     */
    public ClassTask(Callback delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    /**
     *
     * The executeTask method will execute a specific ClassTask based on its parameters
     *
     * @param method the method for executing, can be FETCH, CREATE, UPDATE or DELETE
     * @param classId the class id for the class to be fetched or updated
     * @param teacherId the teacher id for the class to be fetched or updated
     * @param className the class name for the class to be fetched or updated
     */
    public void executeTask(String method, String classId, String teacherId, String className) {
        this.execute(method, classId, teacherId, className, "", "");
    }

    /**
     * the doInBackground method creates a uri based on the execution
     * parameters for making an HTTP-request to the server.
     * It also fetches the server response and converts the returned JSON-array into a HashMap.
     *
     * @param params the parameters used in execution of the UserTask
     * @return result a HashMap containing the result of the UserTask
     */
    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {

        String method;
        String classId;
        String teacherId;
        String className;
        String studentId;
        String userId;

        HashMap<String, HashMap<String, String>> result = new HashMap<>();

        method = params[0];
        classId = params[1];
        teacherId = params[2];
        className = params[3];
        studentId = params[4];
        userId = params[5];

        try {
            String generalResponse;
            int responseCode;

            URL url = new URL("http://emilsiegenfeldt.dk/p8/classes.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("method", method)
                    .appendQueryParameter("classid", classId)
                    .appendQueryParameter("teacherid", teacherId)
                    .appendQueryParameter("classname", className)
                    .appendQueryParameter("studentid", studentId)
                    .appendQueryParameter("userid", userId);

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

            if (params[0].equals("FETCH")) {

                JSONArray classes = JSONResult.getJSONArray("classes");
                for (int i = 0; i < classes.length(); i++) {
                    HashMap<String, String> classInfo = new HashMap<>();

                    JSONObject classMap = classes.getJSONObject(i);
                    classId = classMap.getString("classId");
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", classMap.getString("teacherId"));
                    classInfo.put("className", classMap.getString("className"));
                    classInfo.put("teacherFirstName", classMap.getString("teacherFirstName"));
                    classInfo.put("teacherLastName", classMap.getString("teacherLastName"));
                    classInfo.put("teacherEmail", classMap.getString("teacherEmail"));
                    classInfo.put("numOfStudents", classMap.getString("numOfStudents"));

                    result.put("classId: " + classId, classInfo);
                }
            } else if (params[0].equals("CREATE")) {

                String lastClassId;

                lastClassId = JSONResult.getString("lastClassId");
                Log.d("lastClassId", JSONResult.getString("lastClassId"));

                HashMap<String, String> lastClass = new HashMap<>();
                lastClass.put("lastClassId", lastClassId);

                result.put("lastClassId: " + lastClassId, lastClass);
            }
            Log.d("ClassTask1 response", result.toString());
            HashMap<String, String> serverResponse = new HashMap<>();
            serverResponse.put("generalResponse", generalResponse);
            serverResponse.put("responseCode", String.valueOf(responseCode));
            result.put("response", serverResponse);
        } catch(IOException |JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * onPostExecute uses the results returned by doInBackground,
     * displays the server response code and message if necessary
     * and delegates its results if everything is ok.
     *
     * @param result the HashMap returned by doInBackground
     */
    protected void onPostExecute(HashMap<String,HashMap<String, String>> result) {

        String generalResponse = result.get("response").get("generalResponse");
        String responseCode = result.get("response").get("responseCode");

        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            result.remove("response");
            delegate.asyncDone(result);
        } else if (Integer.parseInt(responseCode) == 101) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            result.remove("response");
            delegate.asyncDone(result);
        } else if (Integer.parseInt(responseCode) > 101) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Response code " + responseCode + ", " + "Message: " + generalResponse, duration);
            toast.show();
        } else {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Something went horribly wrong, no response code!", duration);
            toast.show();
        }
    }
}