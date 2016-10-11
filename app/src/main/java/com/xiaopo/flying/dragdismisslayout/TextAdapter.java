package com.xiaopo.flying.dragdismisslayout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by snowbean on 16-10-11.
 */
public class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextViewHolder>{
    private static final String TAG = "TextAdapter";

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text,parent,false);
        return new TextViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 30;
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{

        public TextViewHolder(View itemView) {
            super(itemView);
        }
    }
}
