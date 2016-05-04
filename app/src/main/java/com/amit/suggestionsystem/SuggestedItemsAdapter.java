package com.amit.suggestionsystem;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Amit on 01-05-2016.
 */
public class SuggestedItemsAdapter extends RecyclerView.Adapter<SuggestedItemsAdapter.ViewHolder> {

    private static final String TAG = "SuggestedItemsAdapter";
    private Context mContext;
    private List<String> list = Collections.emptyList();

    public SuggestedItemsAdapter(Context context, List<String> arrayList) {
        this.mContext = context;
        this.list = arrayList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: " + "parent = [" + parent + "], viewType = [" + viewType + "]");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_file, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SuggestedItemsAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: " + "holder = [" + holder + "], position = [" + position + "]");
        holder.mUserIdTextView.setText(list.get(position));
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mUserIdTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mUserIdTextView = (TextView)itemView.findViewById(R.id.userIdTextView);

        }

    }
}
