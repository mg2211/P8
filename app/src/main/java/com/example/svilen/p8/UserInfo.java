package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EmilSiegenfeldt on 31/03/16.
 */
public class UserInfo {

    private Context context;

    public UserInfo(Context context){
        this.context = context;
    }

    public HashMap<String, String> getUser(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        HashMap<String, String> user = (HashMap<String, String>) preferences.getAll();
        Log.d("called", "yes");
        Log.d("Userinfo", user.toString());
        return user;
    }

    public void logOut(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
