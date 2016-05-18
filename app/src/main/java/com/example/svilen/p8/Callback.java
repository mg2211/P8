package com.example.svilen.p8;

import java.util.HashMap;

/**
 * Created by Brandur on 5/18/2016.
 */
public interface Callback {

    void asyncDone (HashMap<String, HashMap<String, String>> asyncResults);
}
