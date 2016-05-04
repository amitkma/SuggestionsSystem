package com.amit.suggestionsystem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Amit on 01-05-2016.
 */
public class Defaults {

    // Key for accessing orders in JSON
    public final static String ORDER_KEY = "orders";

    public static JSONObject jsonString = new JSONObject();

    public static Map<String, Map<String, Integer>> ordersMap = new LinkedHashMap<>();

    public final static String CURRENT_USER_TOKEN = "currentUserToken";


}
