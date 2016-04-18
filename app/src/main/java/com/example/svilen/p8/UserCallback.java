package com.example.svilen.p8;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EmilSiegenfeldt on 14/04/16.
 */
public interface UserCallback {
    void userTaskDone(Map<String, HashMap<String, String>> users);
}
