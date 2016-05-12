package com.example.svilen.p8;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServerRequests {
}
class LoginTask extends AsyncTask<String, Void, HashMap<String, String>> {
        ProgressDialog progressDialog;
        final Context context;

        LoginTask(Context context) {
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
            editor.commit();


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

class ClassTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

        ClassCallback delegate;
        ProgressDialog progressDialog;
        final Context context;

        ClassTask(ClassCallback delegate, Context context) {
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

            String teacherId = params[0];
            String generalResponse = null;
            int responseCode = 0;
            HashMap<String, HashMap<String, String>> results = new HashMap<>();

            try {
                URL url = new URL("http://emilsiegenfeldt.dk/p8/class.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("teacherId", teacherId); //context).execute(teacherId);

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
                generalResponse = JSONResult.getString("generalResponse");
                responseCode = JSONResult.getInt("responseCode");

                JSONArray classes = JSONResult.getJSONArray("classes");
                for (int i = 0; i < classes.length(); i++) {
                    JSONObject specificClass = classes.getJSONObject(i);
                    String className = specificClass.getString("className");
                    String classId = String.valueOf(specificClass.getInt("classId"));
                    String classTeacher = String.valueOf(specificClass.getInt("teacherId"));
                    String numberOfStudents = String.valueOf(specificClass.getInt("students"));
                    HashMap<String, String> classInfo = new HashMap<>();
                    classInfo.put("studentsInClass", numberOfStudents);
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", classTeacher);
                    classInfo.put("className", className);

                    results.put("ClassID: " + classId, classInfo);
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
                delegate.classListDone(results);
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
class StudentTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {
        StudentCallback delegate;
        ProgressDialog progressDialog;
        final Context context;

        StudentTask(StudentCallback delegate, Context context) {
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
            String generalResponse = null;
            String teacherId = params[1];
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
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, generalResponse, duration);
                toast.show();
                delegate.studentListDone(results);
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

class RoleTask extends AsyncTask<String, Void, Map<String,HashMap<String, String>>> {

    RoleCallback delegate;
    ProgressDialog progressDialog;
    final Context context;

    @Override
    protected void onPreExecute() {
    }

    RoleTask(RoleCallback delegate, Context context) {
        progressDialog = new ProgressDialog(context);
        this.delegate = delegate;
        this.context = context;
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    @Override
    protected Map<String,HashMap<String, String>> doInBackground(String... params) {

        //Initiating return vars.
        Map<String, HashMap<String,String>> result = new HashMap<>();
        String generalResponse = null;
        int responseCode = 0;
        String role = null;

        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/roleList.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("","");

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

            //convert to readable string
            String response = IOUtils.toString(in, "UTF-8");

            //convert to JSON object
            JSONObject JSONResult = new JSONObject(response);

            //extract variables from JSONObject result var
            generalResponse = JSONResult.getString("generalResponse");
            responseCode = JSONResult.getInt("responseCode");

            String roleName;
            String roleId;
            HashMap<String, String> resultData = new HashMap<>();

            JSONArray roles = JSONResult.getJSONArray("roles");
            for (int i = 0; i < roles.length(); i++) {
                JSONObject specificText = roles.getJSONObject(i);
                roleId = specificText.getString("roleId");
                roleName = specificText.getString("roleName");
                resultData.put(roleId, roleName);
            }
            result.put("resultData", resultData);

        } catch (IOException e) {
            responseCode = 300;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> response = new HashMap<>();
        response.put("generalResponse", generalResponse);
        response.put("responseCode", String.valueOf(responseCode));
        result.put("response", response);

        return result;
    }

    protected void onPostExecute(Map<String,HashMap<String, String>> result) {

        String generalResponse = result.get("response").get("generalResponse");
        String responseCode = result.get("response").get("responseCode");

        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            result.remove("response");
            delegate.roleListDone(result);
        } else if (Integer.parseInt(responseCode) != 100) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Response code " + responseCode +", " + "Message: " + generalResponse, duration);
        }
        else {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Something went horribly wrong, no response code!", duration);
            toast.show();
        }
    }
}

class TextTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>>{

    private final Context context;
    private final TextCallback delegate;
    ProgressDialog progressDialog;

    public TextTask(TextCallback delegate, Context context){
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }
    public void executeTask(String method, String textId, String textName, String textContent, double complexity){
        this.execute(method, textId, textName, textContent, String.valueOf(complexity));
    }
    @Override
    protected HashMap <String, HashMap<String, String>> doInBackground(String... params) {
        String method = params[0];
        String textId = params[1];
        String textName = params[2];
        String textContent = params[3];
        String complexity = params[4];
        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        HashMap<String, String> response = new HashMap<>();
        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/textTask.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("method", method)
                    .appendQueryParameter("id",textId)
                    .appendQueryParameter("textName", textName)
                    .appendQueryParameter("textContent", textContent)
                    .appendQueryParameter("complexity", complexity);

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

            JSONObject JSONResult = new JSONObject(serverResponse);
            String generalResponse = JSONResult.getString("generalResponse");
            String responseCode = String.valueOf(JSONResult.getInt("responseCode"));

            response.put("generalResponse", generalResponse);
            response.put("responseCode", responseCode);
            if(method.equals("create")){
                response.put("insertedId", JSONResult.getString("insertedId"));
            }

            if(method.equals("get")){
                JSONArray texts = JSONResult.getJSONArray("texts");
                for (int i = 0; i < texts.length(); i++) {
                    JSONObject specificText = texts.getJSONObject(i);
                    String specificTextname = specificText.getString("textName");
                    String specificTextContent = specificText.getString("textContent");
                    String specificTextId = specificText.getString("textId");
                    double specificTextComplexity = specificText.getDouble("complexity");
                    HashMap<String, String> textInfo = new HashMap<>();
                    textInfo.put("textname", specificTextname);
                    textInfo.put("textcontent", specificTextContent);
                    textInfo.put("id", specificTextId);
                    textInfo.put("complexity", String.valueOf(specificTextComplexity));
                    results.put("TextId: " + specificTextId, textInfo);
                }
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
        delegate.TextCallBack(results);
    }
}


class UserTask extends AsyncTask<String, Void, Map<String,HashMap<String, String>>> {

    UserCallback delegate;
    private final Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
    }

    public UserTask(UserCallback delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }


    public void executeTask(String method, String role, String userId, String teacherId, String studentId,
                            String classId, String username, String password, String lastName, String firstName,
                            String email, String parentEmail) {
        this.execute(method, role, userId, teacherId, studentId, classId, username, password, lastName,
                firstName, email, parentEmail);
    }

    @Override
    protected Map<String, HashMap<String, String>> doInBackground(String... params) {

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

        Map<String, HashMap<String, String>> result = new HashMap<>();

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
                    userInfo.put("userId", user.getString("userId"));
                    userInfo.put("username", user.getString("username"));
                    userInfo.put("password", user.getString("password"));
                    userInfo.put("userId", userId);
                    userInfo.put("firstName", user.getString("firstName"));
                    userInfo.put("lastName", user.getString("lastName"));
                    userInfo.put("email", user.getString("email"));

                    role = user.getString("role");
                    userInfo.put("role", role);
                    if (role.equals("teacher")) {
                        userInfo.put("teacherId", user.getString("teacherId"));
                    } else if (role.equals("student")) {
                        userInfo.put("studentId", user.getString("studentId"));
                        userInfo.put("classId", user.getString("classId"));
                        userInfo.put("parentEmail", user.getString("parentEmail"));
                    }
                    result.put("userId: " + userId, userInfo);
                }
                Log.d("UserTask response", result.toString());
            }
            HashMap<String, String> serverResponse = new HashMap<>();
            serverResponse.put("generalResponse", generalResponse);
            serverResponse.put("responseCode", String.valueOf(responseCode));
            result.put("response", serverResponse);
            } catch(IOException|JSONException e) {
                e.printStackTrace();
            }
            return result;
    }

    protected void onPostExecute(Map<String,HashMap<String, String>> result) {

        String generalResponse = result.get("response").get("generalResponse");
        String responseCode = result.get("response").get("responseCode");

        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            result.remove("response");
            delegate.userTaskDone(result);
        } else if (Integer.parseInt(responseCode) != 100) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Response code " + responseCode +", " + "Message: " + generalResponse, duration);
            toast.show();
        }
        else {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Something went horribly wrong, no response code!", duration);
            toast.show();
        }
    }
}

class ClassTaskNew extends AsyncTask<String, Void, Map<String,HashMap<String, String>>> {

    ClassCallbackNew delegate;
    private final Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
    }

    public ClassTaskNew(ClassCallbackNew delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    public void executeTask(String method, String classId, String teacherId, String className,
                            String studentId, String userId) {
        this.execute(method, classId, teacherId, className, studentId, userId);
    }

    @Override
    protected Map<String, HashMap<String, String>> doInBackground(String... params) {

        String method;
        String classId;
        String teacherId;
        String className;
        String studentId;
        String userId;

        Map<String, HashMap<String, String>> result = new HashMap<>();

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

                    result.put("classId: " + classId, classInfo);
                }
                Log.d("ClassTask response", result.toString());
            }
            HashMap<String, String> serverResponse = new HashMap<>();
            serverResponse.put("generalResponse", generalResponse);
            serverResponse.put("responseCode", String.valueOf(responseCode));
            result.put("response", serverResponse);
        } catch(IOException|JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void onPostExecute(Map<String,HashMap<String, String>> result) {

        String generalResponse = result.get("response").get("generalResponse");
        String responseCode = result.get("response").get("responseCode");

        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            result.remove("response");
            delegate.classListDone(result);
        } else if (Integer.parseInt(responseCode) != 100) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Response code " + responseCode +", " + "Message: " + generalResponse, duration);
            toast.show();
        }
        else {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Something went horribly wrong, no response code!", duration);
            toast.show();
        }
    }
}

class QuestionTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    QuestionCallback delegate;
    private final Context context;
    ProgressDialog progressDialog;

    public QuestionTask(QuestionCallback delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    public void executeTask(String method, String questionId, String textId, String answers, String questionContent) {
        this.execute(method, questionId, questionContent, answers, textId);
    }

    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... params) {
        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        String generalResponse;
        int responseCode;
        String method = params[0];
        String questionId = params[1];
        String questionContent = params[2];
        String answers = params[3];
        String textId = params[4];

        try {
        URL url = new URL("http://emilsiegenfeldt.dk/p8/questions.php");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        Uri.Builder builder = new Uri.Builder().appendQueryParameter("method", method)
                .appendQueryParameter("textId", textId)
                .appendQueryParameter("id", questionId)
                .appendQueryParameter("questionContent", questionContent)
                .appendQueryParameter("questionId", questionId)
                .appendQueryParameter("answers", answers);

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
        Log.d("response", response);
        Log.d("query", query);

            JSONObject JSONResult = new JSONObject(response);
            generalResponse = JSONResult.getString("generalResponse");
            responseCode = JSONResult.getInt("responseCode");
            if(method.equals("get")) {
                JSONArray questions = JSONResult.getJSONArray("questions");
                for (int i = 0; i < questions.length(); i++) {
                    HashMap<String, String> questionInfo = new HashMap<>();

                    JSONObject question = questions.getJSONObject(i);
                    String id = question.getString("id");
                    questionInfo.put("questionContent", question.getString("content"));
                    questionInfo.put("textId", question.getString("textId"));
                    questionInfo.put("questionId", question.getString("id"));
                    JSONArray questionAnswers = question.getJSONArray("answers");
                    String questionAnswersString = "";
                    for(int n = 0; n<questionAnswers.length(); n++){
                        JSONObject answer = questionAnswers.getJSONObject(n);
                        String answerText = answer.getString("answertext");
                        String answerId = answer.getString("id");
                        String isCorrect = answer.getString("iscorrect");
                        String specificAnswer = answerId+";"+answerText+";"+isCorrect;
                        if(n == 0){
                            questionAnswersString =specificAnswer;
                        } else {
                            questionAnswersString = questionAnswersString+"#"+specificAnswer;
                        }
                    }
                    questionInfo.put("answers",questionAnswersString);
                    results.put("QuestionID:" + id, questionInfo);
                }
            }
            HashMap<String, String> serverResponse = new HashMap<>();
            serverResponse.put("generalResponse",generalResponse);
            serverResponse.put("responseCode", String.valueOf(responseCode));
            results.put("response",serverResponse);

        } catch(IOException e){
        //server error
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    protected void onPostExecute(HashMap<String, HashMap<String, String>> results) {
        Log.d("results",results.toString());
        progressDialog.dismiss();
        delegate.QuestionTaskDone(results);
    }
}
class AssignmentLibTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>>{

    private final Context context;
    private final AssignmentLibCallback delegate;
    ProgressDialog progressDialog;

    public AssignmentLibTask(AssignmentLibCallback delegate, Context context){
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
        delegate.AssignmentLibDone(results);
    }
}

    class AssignmentTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {


        AssignmentCallback delegate;
        ProgressDialog progressDialog;
        Context context;

        AssignmentTask(AssignmentCallback delegate, Context context){
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

                    HashMap<String, String> assignmentInfo = new HashMap<>();
                    assignmentInfo.put("assignmentlibraryid", assLibId);
                    assignmentInfo.put("assignmentid", assignmentId);
                    assignmentInfo.put("assignmentLibName", assignmentName);
                    assignmentInfo.put("textId", textId);
                    assignmentInfo.put("availableFrom", assignmentFrom);
                    assignmentInfo.put("availableTo",assignmentTo);
                    assignmentInfo.put("studentId", assignmentStudentId);
                    assignmentInfo.put("isComplete",isComplete);

                    results.put("AssignmentId: " +  assignmentId, assignmentInfo);
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
                delegate.assignmentDone(results);
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