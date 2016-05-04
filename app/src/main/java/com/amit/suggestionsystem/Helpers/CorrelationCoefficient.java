package com.amit.suggestionsystem.Helpers;

import android.util.Log;
import android.widget.Toast;

import com.amit.suggestionsystem.Defaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Amit on 03-05-2016.
 */
public class CorrelationCoefficient {

    private Serializable currentUserKey;
    private Map<String, Double> correlationMap = new HashMap<>();
    private ArrayList<Integer> coordinate_X = new ArrayList<>();
    private ArrayList<Integer> coordinate_Y = new ArrayList<>();

    // Default Constructor
    public CorrelationCoefficient(Serializable currentUserKey) {
        this.currentUserKey = currentUserKey;
    }

    public void correlationMap() {

        Map<String, Integer> userPrefsMap;
        Set<String> keysInA;

        userPrefsMap = new HashMap<>(Defaults.ordersMap.get(currentUserKey));
        keysInA = new HashSet<>(userPrefsMap.keySet()); // Set of item keys ordered by selected user

        for (Map.Entry<String, Map<String, Integer>> stringMapEntry : Defaults.ordersMap.entrySet()) {
            if (stringMapEntry.getKey() != currentUserKey) {
                String otherUserKey = stringMapEntry.getKey();
                Map<String, Integer> otherUserPrefsMap = stringMapEntry.getValue();
                Set<String> keysInB = new HashSet<>(otherUserPrefsMap.keySet()); // Set of item keys ordered by other user

                Set<String> inANotB = new HashSet<>(keysInA);
                inANotB.removeAll(keysInB);
                Set<String> inBNotA = new HashSet<>(keysInB);
                inBNotA.removeAll(keysInA);
                Set<String> commonKeys = new HashSet<>(keysInA);
                commonKeys.retainAll(keysInB); // set of common item keys ordered by both

                createArray(otherUserKey, inANotB, inBNotA, commonKeys, userPrefsMap, otherUserPrefsMap);
            }

        }


    }

    // create array for storing coordinates corresponding to the quantity ordered by selected user and other users
    private void createArray(String otherUserKey, Set<String> inANotB, Set<String> inBNotA, Set<String> commonKeys, Map<String, Integer> userPrefsMap, Map<String, Integer> otherUserPrefsMap) {
        for (String s : inANotB) {
            coordinate_X.add(userPrefsMap.get(s));
            coordinate_Y.add(0);
        }

        for (String s : inBNotA) {
            coordinate_Y.add(otherUserPrefsMap.get(s));
            coordinate_X.add(0);
        }

        for (String commonKey : commonKeys) {
            coordinate_X.add(userPrefsMap.get(commonKey));
            coordinate_Y.add(otherUserPrefsMap.get(commonKey));
        }

        calculateCoefficient(otherUserKey);
    }

    // Calculate correlation coefficient for calculating how much selected user is correlated with the other users
    private void calculateCoefficient(String otherUserKey) {
        int sum_X = 0;
        int sum_Y = 0;

        double Sxx = 0;
        double Syy = 0;
        double Sxy = 0;

        for (int i = 0; i < coordinate_X.size(); i++) {
            sum_X = sum_X + coordinate_X.get(i);
            sum_Y = sum_Y + coordinate_Y.get(i);
        }

        double x_mean = sum_X / coordinate_X.size();
        double y_mean = sum_Y / coordinate_Y.size();

        for (int i = 0; i < coordinate_X.size(); i++) {
            Sxx = Sxx + Math.pow((coordinate_X.get(i) - x_mean), 2);
            Syy = Syy + Math.pow((coordinate_Y.get(i) - y_mean), 2);
            Sxy = Sxy + ((coordinate_X.get(i) - x_mean) * (coordinate_Y.get(i) - y_mean));
        }

        double r = Sxy / Math.pow((Sxx * Syy), 0.5);
        correlationMap.put(otherUserKey, r);
    }

    // Sort the correlation map according to the correlation coefficient value
    public Map<String, Double> getCorrelationMap() {
        return MapUtil.sortByValue(correlationMap);
    }
}
