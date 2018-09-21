package com.kgb.listmaker.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kgb.listmaker.R;

public class ListMakerViewHolders {

    //////////////////////////////////
    // Generic, multi-purpose views //
    //////////////////////////////////

    // The DetailTextViewHolder contains a single label that's left-aligned and a single label right-aligned
    public static class DetailTextViewHolder extends RecyclerView.ViewHolder {

        // Layout resource
        public static final int layoutResource = R.layout.list_item_two_labels;

        // Subviews
        public RelativeLayout backgroundLayout;
        public TextView textLabel;
        public TextView detailTextLabel;

        public DetailTextViewHolder(View view) {
            super(view);

            // Set the references to the subviews
            this.backgroundLayout = view.findViewById(R.id.background_layout);
            this.textLabel = view.findViewById(R.id.text_label);
            this.detailTextLabel = view.findViewById(R.id.detail_text_label);
        }
    }
}
