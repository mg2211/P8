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
public class StudentTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {
    Callback delegate;
    ProgressDialog progressDialog;
    final Context context;

    public StudentTask(Callback delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {
        String classID = params[0];
        String teacherId = params[1];
        String generalResponse = null;
        int responseCode = 0;
        HashMap<String, HashMap<String, String>> results = new HashMap<>();

        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/studentList.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("classId", classID)
                    .appendQueryParameter("teacherId",teacherId);

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
            String response = IOUtils.toString(in, "UTF-8"); // convert to string
            Log.d("student response",response);

            //convert to JSONobject

            JSONObject JSONResult = new JSONObject((response));
            generalResponse = JSONResult.getString("generalResponse");
            responseCode = JSONResult.getInt("responseCode");

            JSONArray students = JSONResult.getJSONArray("students");
            for (int i = 0; i < students.length(); i++) {
                JSONObject specificStudent = students.getJSONObject(i);
                String studentId = specificStudent.getString("studentId");
                String classId = specificStudent.getString("classId");
                String parentEmail = specificStudent.getString("parentEmail");
                String firstname = specificStudent.getString("firstname");
                String lastname = specificStudent.getString("lastname");
                String username = specificStudent.getString("username");
                String role = specificStudent.getString("role");
                String email = specificStudent.getString("email");

                HashMap<String, String> studentInfo = new HashMap<>();
                studentInfo.put("studentId", studentId);
                studentInfo.put("classId", classId);
                studentInfo.put("parentEmail", parentEmail);
                studentInfo.put("firstname", firstname);
                studentInfo.put("lastname", lastname);
                studentInfo.put("username", username);
                studentInfo.put("role", role);
                studentInfo.put("email", email);

                results.put("StudentID: " + studentId, studentInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

        String responseCode = results.get("response").get("responseCode");
        String generalResponse = results.get("response").get("generalResponse");
        results.remove("response");

        if (Integer.parseInt(responseCode) == 100) {
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) == 200) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
        } else if (Integer.parseInt(responseCode) == 300) {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Server connection failed - Please try again later";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }

        results.remove("response");
        progressDialog.dismiss();
    }



}
