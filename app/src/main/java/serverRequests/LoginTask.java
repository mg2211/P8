package serverRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import activities.StudentActivity;
import activities.TeacherActivity;

import org.apache.commons.io.IOUtils;
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
public class LoginTask extends AsyncTask<String, Void, HashMap<String, String>> {
    private final ProgressDialog progressDialog;
    private final Context context;

    public LoginTask(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }


    @Override
    protected HashMap<String, String> doInBackground(String... userdata) {
        //Getting params
        String username = userdata[0];
        String password = userdata[1];

        //Initiating return vars.
        HashMap<String, String> result = new HashMap<>();
        String generalResponse = null;
        int responseCode = 0;
        int teacherId = 0;
        int studentId = 0;
        String email = null;
        String firstname = null;
        String lastname = null;
        String role = null;

        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/login.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", username)
                    .appendQueryParameter("password", password);

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
            JSONObject JSONResult = new JSONObject(response);

            //extract variables from JSONObject result var
            generalResponse = JSONResult.getString("generalresponse");
            responseCode = JSONResult.getInt("responsecode");
            username = JSONResult.getString("username");
            role = JSONResult.getString("role");
            firstname = JSONResult.getString("firstname");
            lastname = JSONResult.getString("lastname");
            email = JSONResult.getString("email");
            if (role.equals("student")) {
                studentId = JSONResult.getInt("studentId");
            } else if (role.equals("teacher")) {
                teacherId = JSONResult.getInt("teacherId");
            }

        } catch (IOException e) {
            responseCode = 300;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.put("Username", username);
        result.put("Password", password);
        result.put("responseCode", String.valueOf(responseCode));
        result.put("generalResponse", generalResponse);
        result.put("role", role);
        result.put("firstname", firstname);
        result.put("lastname", lastname);
        result.put("email", email);
        result.put("teacherId", String.valueOf(teacherId));
        result.put("studentId", String.valueOf(studentId));


        return result;
    }

    protected void onPostExecute(HashMap<String, String> result) {
        String generalResponse = result.get("generalResponse");
        String responseCode = result.get("responseCode");
        String role = result.get("role");
        String username = result.get("Username");
        String firstname = result.get("firstname");
        String lastname = result.get("lastname");
        String email = result.get("email");
        String teacherId = result.get("teacherId");
        String studentId = result.get("studentId");
        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            Intent intent;
            //if login credentials are right - set intent to either student or teacher depending on role variable.
            if (role.equals("student")) {
                intent = new Intent(context, StudentActivity.class);
            } else {
                intent = new Intent(context, TeacherActivity.class);
            }
            //save user information on device
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", username);
            editor.putString("firstname", firstname);
            editor.putString("lastname", lastname);
            editor.putString("email", email);
            editor.putString("role", role);
            if (role.equals("teacher")) {
                editor.putString("teacherId", teacherId);
            } else if (role.equals("student")) {
                editor.putString("studentId", studentId);
            }
            editor.apply();


            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else if (Integer.parseInt(responseCode) == 200) {
            //if login is wrong - make a toast saying so.
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
        } else if (Integer.parseInt(responseCode) == 300) {
            //If server connection fails.
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Server connection failed - Please try again later";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
    }
}
