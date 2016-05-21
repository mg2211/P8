package serverRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
 * The Role class allows for getting role data from and updating data in the database asynchronously
 */
public class RoleTask extends AsyncTask<String, Void, HashMap<String,HashMap<String, String>>> {

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
    public RoleTask(Callback delegate, Context context) {
        progressDialog = new ProgressDialog(context);
        this.delegate = delegate;
        this.context = context;
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }


    /**
     * the doInBackground method creates a uri for making an HTTP-request to the server.
     * It also fetches the server response and converts the returned JSON-array into a HashMap.
     *
     * @param params the parameters used in execution of the UserTask.
     *               Since this method has only one way of execution, no parameter are passed.
     * @return result a HashMap containing the result of the UserTask
     */
    protected HashMap<String,HashMap<String, String>> doInBackground(String... params) {

        //Initiating return vars.
        HashMap<String, HashMap<String,String>> result = new HashMap<>();
        String generalResponse = null;
        int responseCode = 0;

        try {
            URL url = new URL("http://emilsiegenfeldt.dk/p8/roles.php");
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
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, generalResponse, duration);
            toast.show();
            result.remove("response");
            delegate.asyncDone(result);
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