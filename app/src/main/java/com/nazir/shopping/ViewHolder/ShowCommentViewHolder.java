package com.nazir.shopping.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nazir.shopping.R;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {

    public TextView txtUserPhone,txtComment;
    public RatingBar ratingBar;

    public ShowCommentViewHolder(View itemView) {
        super(itemView);

        txtUserPhone = itemView.findViewById(R.id.txtUserphone);
        txtComment = itemView.findViewById(R.id.txtComment);

        ratingBar = itemView.findViewById(R.id.ratingBar);

    }
}
