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
import java.net.URL;
import java.util.HashMap;

public class ServerRequests {
    private final Context context;
    ProgressDialog progressDialog;

    public ServerRequests(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
    }

    public void loginExecute(String username, String password) {
        new loginTask().execute(username, password);
        progressDialog.show();
    }

    public class loginTask extends AsyncTask<String, Void, HashMap<String, String>> {

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

    public void registerExecute(String role, String username, String password, String firstname, String lastname, String email) {
        new registerTask().execute(role, username, password, firstname, lastname, email);
        progressDialog.show();
    }

    public class registerTask extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... userdata) {
            String role = userdata[0];
            String username = userdata[1];
            String password = userdata[2];
            String firstname = userdata[3];
            String lastname = userdata[4];
            String email = userdata[5];

            String generalResponse = null;
            int responseCode = 0;


            try {
                URL url = new URL("http://emilsiegenfeldt.dk/p8/newUser.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", username)
                        .appendQueryParameter("role", role)
                        .appendQueryParameter("password", password)
                        .appendQueryParameter("firstname", firstname)
                        .appendQueryParameter("lastname", lastname)
                        .appendQueryParameter("email", email);

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
                generalResponse = JSONResult.getString("generalResponse");
                responseCode = JSONResult.getInt("responseCode");
                username = JSONResult.getString("username");

                Log.d("response", response);

            } catch (IOException e) {
                responseCode = 300;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, String> result = new HashMap<>();

            result.put("generalResponse", generalResponse);
            result.put("responseCode", String.valueOf(responseCode));
            result.put("username", username);
            //result.put("name",name);

            return result;
        }

        protected void onPostExecute(HashMap<String, String> result) {

            progressDialog.dismiss();
            String responseCode = result.get("responseCode");
            String generalResponse = result.get("generalResponse");
            String username = result.get("username");

            if (Integer.parseInt(responseCode) == 100) {
                //if everything is alright
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, generalResponse, duration);
                toast.show();
            } else if (Integer.parseInt(responseCode) == 200) {
                //if somethings wrong e.g. username already in use
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

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("teacherId", teacherId);

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
            int responseCode = 0;
            HashMap<String, HashMap<String, String>> results = new HashMap<>();

            try {
                URL url = new URL("http://emilsiegenfeldt.dk/p8/studentList.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("classId", classID);

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

            Log.d("Results", results.toString());
            results.remove("response");
            Log.d("Results", results.toString());
            progressDialog.dismiss();
        }

    }


