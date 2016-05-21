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
public class UserTask extends AsyncTask<String, Void, HashMap<String,HashMap<String, String>>> {

    private final Callback delegate;
    private final Context context;
    private final ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
    }

    public UserTask(Callback delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }


    public void executeTask(String method, String role, String userId, String teacherId,
                            String username, String password, String lastName, String firstName,
                            String email, String parentEmail) {
        this.execute(method, role, userId, "", "", "", username, password, lastName,
                firstName, email, parentEmail);
    }

    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {

        String method;
        String role;
        String userId;
        String teacherId;
        String studentId;
        String classId;
        String username;
        String password;
        String lastName;
        String firstName;
        String email;
        String parentEmail;

        HashMap<String, HashMap<String, String>> result = new HashMap<>();

        method = params[0];
        role = params[1];
        userId = params[2];
        teacherId = params[3];
        studentId = params[4];
        classId = params[5];
        username = params[6];
        password = params[7];
        lastName = params[8];
        firstName = params[9];
        email = params[10];
        parentEmail = params[11];

        try {
            String generalResponse;
            int responseCode;

            URL url = new URL("http://emilsiegenfeldt.dk/p8/users.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("method", method)
                    .appendQueryParameter("role", role)
                    .appendQueryParameter("userid", userId)
                    .appendQueryParameter("studentid", studentId)
                    .appendQueryParameter("teacherid", teacherId)
                    .appendQueryParameter("classid", classId)
                    .appendQueryParameter("username", username)
                    .appendQueryParameter("password", password)
                    .appendQueryParameter("lastname", lastName)
                    .appendQueryParameter("firstname", firstName)
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("parentemail", parentEmail);

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

                JSONArray users = JSONResult.getJSONArray("users");
                for (int i = 0; i < users.length(); i++) {
                    HashMap<String, String> userInfo = new HashMap<>();

                    JSONObject user = users.getJSONObject(i);
                    userId = user.getString("userId");
                    userInfo.put("userId", userId);
                    userInfo.put("username", user.getString("username"));
                    userInfo.put("password", user.getString("password"));
                    userInfo.put("firstName", user.getString("firstName"));
                    userInfo.put("lastName", user.getString("lastName"));
                    userInfo.put("email", user.getString("email"));

                    role = user.getString("role");
                    userInfo.put("role", role);
                    if (role.equals("teacher")) {
                        userInfo.put("teacherId", user.getString("teacherId"));
                    } else if (role.equals("student")) {
                        userInfo.put("studentId", user.getString("studentId"));
                        userInfo.put("parentEmail", user.getString("parentEmail"));
                        userInfo.put("classId", user.getString("classId"));
                    }
                    result.put("userId: " + userId, userInfo);
                }

            } else if (params[0].equals("CREATE")) {

                String lastUserId;

                lastUserId = JSONResult.getString("lastUserId");

                HashMap<String, String> lastUser = new HashMap<>();

                lastUser.put("lastUserId", lastUserId);
                result.put("lastUserId: " + lastUserId, lastUser);
            }
            Log.d("UserTask response", result.toString());
            HashMap<String, String> serverResponse = new HashMap<>();
            serverResponse.put("generalResponse", generalResponse);
            serverResponse.put("responseCode", String.valueOf(responseCode));
            result.put("response", serverResponse);
        } catch(IOException |JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

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