package com.example.svilen.p8;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
    public void loginExecute(String username, String password){
        new loginTask().execute(username, password);
        progressDialog.show();
    }

    public class dbTask extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... userdata) {
            String username = userdata[0];
            String password = userdata[1];
            String name = userdata[2];

            String generalResponse = null;
            int responseCode = 0;


            try {
                URL url = new URL("http://emilsiegenfeldt.dk/p8/newUser.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", username)
                        .appendQueryParameter("password", password)
                        .appendQueryParameter("name", name);

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

                Log.d("response",response);

            } catch (IOException e) {
                responseCode = 300;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, String> result = new HashMap<>();

            result.put("generalResponse", generalResponse);
            result.put("responseCode", String.valueOf(responseCode));
            result.put("username",username);
            result.put("name",name);



            return result;
        }

        protected void onPostExecute(HashMap<String, String> result){

            progressDialog.dismiss();
            String responseCode = result.get("responseCode");
            String generalResponse = result.get("generalResponse");
            String username = result.get("username");
            String name = result.get("name");

            if(Integer.parseInt(responseCode) == 100){
                //if everything is alright
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, generalResponse, duration);
                toast.show();
            } else if(Integer.parseInt(responseCode) == 200){
                //if somethings wrong e.g. username already in use
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, generalResponse, duration);
                toast.show();
            } else if(Integer.parseInt(responseCode) == 300){
                //If server connection fails.
                int duration = Toast.LENGTH_LONG;
                CharSequence alert = "Server connection failed - Please try again later";
                Toast toast = Toast.makeText(context, alert, duration);
                toast.show();
            }

        }
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

            return result;
        }

        protected void onPostExecute(HashMap<String, String> result){
            String username = result.get("Username");
            String generalResponse = result.get("generalResponse");
            String responseCode = result.get("responseCode");
            String role = result.get("role");
            progressDialog.dismiss();

            if(Integer.parseInt(responseCode) == 100){
                Intent intent;
                //if login credentials are right - set intent to either student or teacher depending on role variable.
            if(role.equals("student")){
                intent = new Intent(context, StudentActivity.class);
            } else {
               intent = new Intent(context, TeacherActivity.class);
            }
            //start the right activity
            context.startActivity(intent);

            } else if(Integer.parseInt(responseCode) == 200){
                //if login is wrong - make a toast saying so.
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, generalResponse, duration);
                toast.show();
            } else if(Integer.parseInt(responseCode) == 300){
                //If server connection fails.
                int duration = Toast.LENGTH_LONG;
                CharSequence alert = "Server connection failed - Please try again later";
                Toast toast = Toast.makeText(context, alert, duration);
                toast.show();
            }
        }
    }

    public void registerExecute(String role, String username, String password, String firstname, String lastname, String email){
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

                Log.d("response",response);

            } catch (IOException e) {
                responseCode = 300;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, String> result = new HashMap<>();

            result.put("generalResponse", generalResponse);
            result.put("responseCode", String.valueOf(responseCode));
            result.put("username",username);
            //result.put("name",name);

            return result;
        }

        protected void onPostExecute(HashMap<String, String> result){

            progressDialog.dismiss();
            String responseCode = result.get("responseCode");
            String generalResponse = result.get("generalResponse");
            String username = result.get("username");
            //String name = result.get("name");

            if(Integer.parseInt(responseCode) == 100){
                //if everything is alright
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, generalResponse, duration);
                toast.show();
            } else if(Integer.parseInt(responseCode) == 200){
                //if somethings wrong e.g. username already in use
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, generalResponse, duration);
                toast.show();
            } else if(Integer.parseInt(responseCode) == 300){
                //If server connection fails.
                int duration = Toast.LENGTH_LONG;
                CharSequence alert = "Server connection failed - Please try again later";
                Toast toast = Toast.makeText(context, alert, duration);
                toast.show();
            }

        }
    }

    }
