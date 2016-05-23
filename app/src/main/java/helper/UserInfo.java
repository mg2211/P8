package helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import activities.LoginActivity;

import java.util.HashMap;

/**
 * Class for containing and retrieving userinformation being stored on the device
 */
public class UserInfo {
    /*Context - the acitivity asking for the information*/
    private final Context context;

    /**
     * Contructor
     * @param context
     */
    public UserInfo(Context context){
        this.context = context;
    }

    /**
     * Getting the user information and return it
     * @return HashMap with the user information
     */
    public HashMap<String, String> getUser(){
        /*Getting userinfo from SharedPrefs*/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        /*return it*/
        return (HashMap<String, String>) preferences.getAll();
    }

    /**
     * Log out method
     * Deletes every information stored on the device and sends the user to the loginActivity
     */
    public void logOut(){
        /*Getting sharedPrefs*/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        /*Clearing the information*/
        editor.clear();
        /*Applying changes*/
        editor.apply();

        /*Sending the user to LoginActivity*/
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
