package com.example.svilen.p8;

/**
 * Created by Brandur on 3/17/2016.
 */
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brandur on 3/15/2016.
 */
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
    public class loginTask extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... userdata) {
            String username = userdata[0];
            String password = userdata[1];
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
                JSONObject JSONresult = new JSONObject(response);

                //extract variables from JSONObject result var
                generalResponse = JSONresult.getString("generalresponse");
                responseCode = JSONresult.getInt("responsecode");
                username = JSONresult.getString("username");
                role = JSONresult.getString("role");

            } catch (IOException e) {
                responseCode = 300;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, String> result = new HashMap<>();
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
                int duration = Toast.LENGTH_LONG;
                String alert = "Server connection failed - Please try again later";
                Toast toast = Toast.makeText(context, alert, duration);
                toast.show();
            }
        }
    }

    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://emilsiegenfeldt.dk/p8/";
    String JSON_String;


    public void storeUserDataInBackground(User user,
                                          GetUserCallback userCallBack) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallBack).execute();
    }



    /**
     * parameter sent to task upon execution progress published during
     * background computation result of the background computation
     */

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallBack;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }
        private String getEncodedData(Map<String,String> data) {
            StringBuilder sb = new StringBuilder();
            for(String key : data.keySet()) {
                String value = null;
                try {
                    value = URLEncoder.encode(data.get(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if(sb.length()>0)
                    sb.append("&");

                sb.append(key + "=" + value);
            }
            return sb.toString();
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("do in background1111", "asdfghhjg");
            //Use HashMap, it works similar to NameValuePair
            Map<String,String> dataToSend = new HashMap<>();
            dataToSend.put("name", user.name);
            dataToSend.put("username", user.username);
            dataToSend.put("password", user.password);



            //Server Communication part - it's relatively long but uses standard methods

            //Encoded String - we will have to encode string by our custom method (Very easy)
            String encodedStr = getEncodedData(dataToSend);

            //Will be used if we want to read some data from server
            BufferedReader reader = null;

            //Connection Handling
            try {
                //Converting address String to URL
                URL url = new URL(SERVER_ADDRESS + "newUser.php");
                //Opening the connection (Not setting or using CONNECTION_TIMEOUT)
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                //Post Method
                con.setRequestMethod("POST");
                //To enable inputting values using POST method
                //(Basically, after this we can write the dataToSend to the body of POST method)
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                //Writing dataToSend to outputstreamwriter
                writer.write(encodedStr);
                //Sending the data to the server - This much is enough to send data to server
                //But to read the response of the server, you will have to implement the procedure below
                writer.flush();

                //Data Read Procedure - Basically reading the data comming line by line
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while((line = reader.readLine()) != null) { //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();           //Saving complete data received in string, you can do it differently

                //Just check to the values received in Logcat
                Log.i("custom_check","The values received in the store part are as follows:");
                Log.i("custom_check",line);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(reader != null) {
                    try {
                        reader.close();     //Closing the
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //Same return null, but if you want to return the read string (stored in line)
            //then change the parameters of AsyncTask and return that type, by converting
            //the string - to say JSON or user in your case
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            userCallBack.done(null);
        }

    }


    }
