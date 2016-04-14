package com.example.svilen.p8;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivo on 12-4-2016.
 */
public interface RoleCallback {
    void roleListDone(Map<String, HashMap<String, String>> roles);
}