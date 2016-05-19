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
 * Created by Ivo on 19-5-2016.
 */
public class TextTask extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {

    private final Context context;
    private final Callback delegate;
    ProgressDialog progressDialog;

    public TextTask(Callback delegate, Context context){
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
            URL url = new URL("http://emilsiegenfeldt.dk/p8/texts.php");

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
            if(method.equals("update")){
                Log.d("SERVER RESPONSE",serverResponse);
            }

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
                    results.put("text"+i,textInfo);
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
        delegate.asyncDone(results);
    }
}