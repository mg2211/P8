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
 * The UserTask class allows for getting user data from and updating data in the database asynchronously
 */
public class UserTask extends AsyncTask<String, Void, HashMap<String,HashMap<String, String>>> {

    /** callback */
    private final Callback delegate;

    /** context */
    private final Context context;

    /** dialog showing progress during task execution */
    private final ProgressDialog progressDialog;

    /**
     * UserTask constructor
     *
     * @param delegate the result is delegated to
     * @param context the execution context
     */
    public UserTask(Callback delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }


    /**
     * The executeTask method will execute a specific UserTask based on its parameters
     *
     * @param method the method for executing, can be FETCH, CREATE, UPDATE or DELETE
     * @param role the role for the user to be fetched or updated
     * @param userId the user id for the user to be fetched or updated
     * @param teacherId the teacher id for the user to be fetched or updated
     * @param username the username for the user to be fetched or updated
     * @param password the password for the user to be fetched or updated
     * @param lastName the last name for the user to be fetched or updated
     * @param firstName the first name for the user to be fetched or updated
     * @param email the email address for the user to be fetched or updated
     * @param parentEmail the email address for a parent or caretaker of the user to be fetched or updated
     */
    public void executeTask(String method, String role, String userId, String teacherId,
                            String username, String password, String lastName, String firstName,
                            String email, String parentEmail) {
        this.execute(method, role, userId, "", "", "", username, password, lastName,
                firstName, email, parentEmail);
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