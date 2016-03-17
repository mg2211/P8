package com.example.svilen.p8;

/**
 * Created by Brandur on 3/17/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Brandur on 3/15/2016.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user){ //save all info on users
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("name", user.name);
        spEditor.putString("username", user.username);
        spEditor.putString("password", user.password);
        spEditor.commit();

    }

    public User getLoggedInUser(){ //this method returns a user which will have the attributes of the logged in user, that is who is stored in the local db

        String name = userLocalDatabase.getString("name", "");
        String username = userLocalDatabase.getString("username","");
        String password = userLocalDatabase.getString("password","");

        User storedUser = new User(name, username, password);
        return storedUser;
    }

    public void setUserLoggedIn (boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean getUserLoggedIn(){ // check if user is logged in or not
        if(userLocalDatabase.getBoolean("loggedIn", false)== true){
            return true;
        }else{return false;}
    }

    public void clearUserDate(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
