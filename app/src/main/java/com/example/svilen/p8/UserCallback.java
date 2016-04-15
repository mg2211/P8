package com.example.svilen.p8;

import java.util.HashMap;

/**
 * Created by EmilSiegenfeldt on 14/04/16.
 */
public interface UserCallback {
    void fetchUserDone(HashMap<String, HashMap<String, String>> texts);
}

