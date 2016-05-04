package com.amit.suggestionsystem;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.amit.suggestionsystem.Helpers.CorrelationCoefficient;
import com.amit.suggestionsystem.Helpers.DividerItem;
import com.amit.suggestionsystem.Helpers.HelpDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SuggestedItems extends AppCompatActivity {

    // Arraylists for holding different types of data
    private ArrayList<String> suggestedUserKeys = new ArrayList<>();
    private ArrayList<String> userItemKeys = new ArrayList<>();
    private ArrayList<String> suggestedItemKeys = new ArrayList<>();
    private ArrayList<String> suggestedItemNames = new ArrayList<>();

    private Serializable currentUserKey;

    private static final String TAG = "SuggestedItems";
    private ArrayList<String> data;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private AsyncTask<Void, Void, ArrayList<String>> mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        currentUserKey = i.getSerializableExtra(Defaults.CURRENT_USER_TOKEN);

        setContentView(R.layout.activity_suggested_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Suggestions");

        mRecyclerView = (RecyclerView) findViewById(R.id.ItemsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItem(this));
        mTask = new CreateDataSet().execute();

    }

    // Find the suggested items keys
    private void findSuggestedItems() {
        for (int i = suggestedUserKeys.size() - 1; i >= 0; i--) {

            Map<String, Integer> userPrefsMap;
            userPrefsMap = new HashMap<>(Defaults.ordersMap.get(currentUserKey));

            for (Map.Entry<String, Integer> stringIntegerEntry : userPrefsMap.entrySet()) {
                userItemKeys.add(stringIntegerEntry.getKey());
            }

            Map<String, Integer> otherUserPrefsMap = Defaults.ordersMap.get(suggestedUserKeys.get(i));
            for (Map.Entry<String, Integer> stringIntegerEntry : otherUserPrefsMap.entrySet()) {
                suggestedItemKeys.add(stringIntegerEntry.getKey());
            }

            suggestedItemKeys.removeAll(userItemKeys); // Only retain those items which are not ordered by the selected user.
        }

        setData(suggestedItemKeys);
    }


    // Set the data into a arraylist
    public void setData(ArrayList<String> data) {
        this.data = data;

        try {
            JSONObject menusJSONObject = Defaults.jsonString.getJSONObject("menus");
            for (String s : data) {
                String[] path = new String[3];
                for (int i = 0; i < s.split("@").length; i++) {
                    path[i] = s.split("@")[i];
                }
                Iterator<String> keys = menusJSONObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject jsonObject = menusJSONObject.getJSONObject(key);
                    if (jsonObject.has(path[0])) {
                        JSONObject itemsJSON = jsonObject.getJSONObject(path[0]).getJSONObject(path[1]);
                        if (itemsJSON.has(path[2])) {
                            JSONObject keyJSON = itemsJSON.getJSONObject(path[2]);
                            suggestedItemNames.add(keyJSON.getString("name"));
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    // Async task for loading data asynchronously (in background thread) so that UI thread can not be blocked.
    private class CreateDataSet extends AsyncTask<Void, Void, ArrayList<String>> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SuggestedItems.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        // The system calls this to perform work in a worker thread and
        // delivers it the parameters given to AsyncTask.execute()
        protected ArrayList<String> doInBackground(Void... urls) {

            // Calculate the correlation between two users using pearson correlation's coefficient method
            CorrelationCoefficient correlationCoefficient = new CorrelationCoefficient(currentUserKey);
            correlationCoefficient.correlationMap();

            for (Map.Entry<String, Double> stringDoubleEntry : correlationCoefficient.getCorrelationMap().entrySet()) {
                suggestedUserKeys.add(stringDoubleEntry.getKey());
            }

            findSuggestedItems();
            return suggestedItemNames;

        }

        // The system calls this to perform work in the UI thread and delivers
        // the result from doInBackground() //
        @Override
        protected void onPostExecute(ArrayList<String> suggestedItems) {
            super.onPostExecute(suggestedItems);
            progressDialog.dismiss();
            mAdapter = new SuggestedItemsAdapter(SuggestedItems.this, suggestedItems);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_help:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() {
        DialogFragment newFragment = new HelpDialog();
        Bundle bundle = new Bundle();
        bundle.putString("message_key", getResources().getString(R.string.suggestion_message));
        bundle.putString("title_key", getResources().getString(R.string.suggestion_title));
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "help");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
