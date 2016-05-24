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

/**
 * TextTask
 * Used for creating, reading, updating and deleting texts
 */
public class TextTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {
    /*The caller activity*/
    private final Context context;
    /*The Callback Interface*/
    private final Callback delegate;
    /*Declaring a Progressdialog*/
    private final ProgressDialog progressDialog;

    /**
     * Constructor
     * @param delegate - The callback interface to be used
     * @param context - The caller activity
     */
    public TextTask(Callback delegate, Context context){
        this.delegate = delegate;
        this.context = context;
        /*Creating and setting the progressdialog*/
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();
    }

    /**
     * Helper method
     * @param method - The method to send the server
     * @param textId - The id of the text
     * @param textName - The name of the text
     * @param textContent - The content of the text
     * @param complexity - The complexity of the text
     */
    public void executeTask(String method, String textId, String textName, String textContent, double complexity){
        this.execute(method, textId, textName, textContent, String.valueOf(complexity));
    }

    /**
     * doInBackground handles the server call
     * @param params - String array, either sent from the executeTask method or directly to the class
     * @return HashMap of results
     */
    @Override
    protected HashMap <String, HashMap<String, String>> doInBackground(String... params) {

        /*Getting params*/
        String method = params[0];
        String textId = params[1];
        String textName = params[2];
        String textContent = params[3];
        String complexity = params[4];

        /*Creating results and response maps*/
        HashMap<String, HashMap<String, String>> results = new HashMap<>();
        HashMap<String, String> response = new HashMap<>();
        try {
            /*Creating the server call*/
            URL url = new URL("http://emilsiegenfeldt.dk/p8/texts.php");

            /*Building the URI with POST parameters*/
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

            /*Catches the server response*/
            InputStream in = new BufferedInputStream(connection.getInputStream());

            String serverResponse = IOUtils.toString(in, "UTF-8");

            /*Converts the server response to JSONObject */
            JSONObject JSONResult = new JSONObject(serverResponse);

            /*Getting the reponse from JSON*/
            String generalResponse = JSONResult.getString("generalResponse");
            String responseCode = String.valueOf(JSONResult.getInt("responseCode"));

            /*Putting the response into it's HashMap*/
            response.put("generalResponse", generalResponse);
            response.put("responseCode", responseCode);

            /*If the method is create, get the text's new id and put it in the response*/
            if(method.equals("create")){
                response.put("insertedId", JSONResult.getString("insertedId"));
            }

            /*If the method is "get", parse the JSON for texts*/
            if(method.equals("get")){
                /*Getting the array of texts*/
                JSONArray texts = JSONResult.getJSONArray("texts");
                /*Iterate the array of texts*/
                for (int i = 0; i < texts.length(); i++) {
                    /*Getting the specific text in the array*/
                    JSONObject specificText = texts.getJSONObject(i);

                    /*Getting all information from the array*/
                    String specificTextname = specificText.getString("textName");
                    String specificTextContent = specificText.getString("textContent");
                    String specificTextId = specificText.getString("textId");
                    double specificTextComplexity = specificText.getDouble("complexity");
                    String specificTextAssigned = specificText.getString("assigned");

                    /*Creating a HashMap for containing textinfo and put data in it*/
                    HashMap<String, String> textInfo = new HashMap<>();
                    textInfo.put("textname", specificTextname);
                    textInfo.put("textcontent", specificTextContent);
                    textInfo.put("id", specificTextId);
                    textInfo.put("complexity", String.valueOf(specificTextComplexity));
                    textInfo.put("assigned", specificTextAssigned);

                    /*Putting a text into the results map*/
                    results.put("text"+i,textInfo);
                }
            }

        } catch (IOException e){
            response.put("generalResponse", "Server connection failed");
            response.put("responseCode", "300");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*Putting the response into the results map*/
        results.put("response", response);
        return results;
    }

    /**
     * Handles the results from doInBackground
     * @param results returned from doInBackground
     */
    protected void onPostExecute (HashMap<String, HashMap<String, String>> results) {
         /*getting the response variables*/
        String generalResponse = results.get("response").get("generalResponse");
        String responseCode = results.get("response").get("responseCode");
        /*Dismisses the progressdialog*/
        progressDialog.dismiss();

        if (Integer.parseInt(responseCode) == 100) {
            /*Sending the result back to the caller activity via the Callback interface*/
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) == 101) {
            /*Something went wrong database side, show a toast saying so*/
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            /*Sending the result back to the caller activity via the Callback interface*/
            delegate.asyncDone(results);
        } else if (Integer.parseInt(responseCode) > 101) {
            /*If the doInBackground fails, a toast saying so is shown*/
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Server connection failed - Please try again later";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
    }
}