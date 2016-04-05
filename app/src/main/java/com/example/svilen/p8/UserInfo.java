package com.example.svilen.p8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.HashMap;

public class UserInfo {

    private Context context;

    public UserInfo(Context context){

        this.context = context;
    }

    public HashMap<String, String> getUser(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        return (HashMap<String, String>) preferences.getAll();
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
