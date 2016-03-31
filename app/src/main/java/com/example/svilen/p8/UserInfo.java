package com.example.svilen.p8;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

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
        return (HashMap<String, String>) preferences.getAll();
    }
}
