package com.amit.suggestionsystem;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.amit.suggestionsystem.Helpers.DividerItem;
import com.amit.suggestionsystem.Helpers.HelpDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Users List");

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.usersRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create a map for storing orders and retrieve the users list who ordered.
        createOrdersMap();
        Object[] keys = Defaults.ordersMap.keySet().toArray(); //Getting keys of users corresponding to the orders.
        RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(this, keys);
        mRecyclerView.addItemDecoration(new DividerItem(this));
        mRecyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();

    }


    // Method for creating orders map for storing orders
    private void createOrdersMap() {
        try {

            Defaults.jsonString = new JSONObject(loadJSONString()); // convert string into json Object.
            JSONObject ordersJSONObject = Defaults.jsonString.getJSONObject(Defaults.ORDER_KEY);

            Iterator<String> keys = ordersJSONObject.keys();
            while (keys.hasNext()) {

                Map<String, Integer> itemsMap = new LinkedHashMap<>();
                String userKey;
                String key = keys.next();

                if (ordersJSONObject.getJSONObject(key).has("user") && ordersJSONObject.getJSONObject(key).has("items")) {
                    userKey = ordersJSONObject.getJSONObject(key).getString("user");
                    if (Defaults.ordersMap.containsKey(userKey)) {
                        itemsMap = Defaults.ordersMap.get(userKey);
                    }

                    JSONObject itemsOrdered = ordersJSONObject.getJSONObject(key).getJSONObject("items");
                    Iterator<String> pathKeys = itemsOrdered.keys();
                    while (pathKeys.hasNext()) {
                        int quantity = 0;
                        String pathKey = pathKeys.next();
                        if (itemsMap.containsKey(pathKey)) {
                            quantity = itemsMap.get(pathKey);
                        }

                        JSONObject itemObject = itemsOrdered.getJSONObject(pathKey);
                        int netQuantity = quantity + itemObject.getInt("quantity");
                        itemsMap.put(itemObject.getString("path"), netQuantity);
                    }
                    Defaults.ordersMap.put(userKey, itemsMap);
                }

            }

            Log.i(TAG, "createOrdersMap: "+Defaults.ordersMap.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String loadJSONString() {

        String json;
        try {

            // Load a json file and convert the whole data into a string.
            InputStream inputStream = this.getAssets().open("mozzo.json");
            int size = inputStream.available();
            byte[] bufferArray = new byte[size];
            inputStream.read(bufferArray);
            inputStream.close();
            json = new String(bufferArray, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;

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
        bundle.putString("message_key", getResources().getString(R.string.dialog_message));
        bundle.putString("title_key", getResources().getString(R.string.dialog_title));
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "help");
    }
}
