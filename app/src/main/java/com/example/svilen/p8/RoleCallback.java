package com.example.svilen.p8;

import java.util.HashMap;
import java.util.Map;

public interface RoleCallback {
    void roleListDone(Map<String, HashMap<String, String>> roles);
}