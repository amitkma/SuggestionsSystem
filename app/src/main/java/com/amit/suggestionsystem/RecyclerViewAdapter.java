package com.amit.suggestionsystem;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by Amit on 02-05-2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private Object[] accessKeysArray;


    // Default constructor for adapter
    public RecyclerViewAdapter(Context context, Object[] keys) {
        this.mContext = context;
        this.accessKeysArray = keys;
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mUserIdTextView.setText((CharSequence) accessKeysArray[position]);
    }

    // Return the size of the dataset
    @Override
    public int getItemCount() {
        return accessKeysArray.length;
    }

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mUserIdTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mUserIdTextView = (TextView)itemView.findViewById(R.id.userIdTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SuggestedItems.class);
                    intent.putExtra(Defaults.CURRENT_USER_TOKEN, (Serializable) accessKeysArray[getAdapterPosition()]);
                    mContext.startActivity(intent);
                    ((MainActivity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

    }
}
