package com.example.svilen.p8;

import java.util.HashMap;

/**
 * Created by Brandur on 4/14/2016.
 */
public interface AssignmentCallback {


        void assignmentListDone(HashMap<String, HashMap<String, String>> assignments);

         //assLibID, map of stuff relating to ID fx 1, String like $textname, second string actual data
    }

