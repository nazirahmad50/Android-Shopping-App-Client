package com.nazir.shopping.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nazir.shopping.R;

public class OrderDetailViewHolder extends RecyclerView.ViewHolder{

    public TextView name,quantity,price, subtotal, color;
    public ImageView hookah_img;

    public OrderDetailViewHolder(View itemView){
        super(itemView);

        name =  itemView.findViewById(R.id.hookah_name);
        quantity =  itemView.findViewById(R.id.hookah_quantity);
        hookah_img =  itemView.findViewById(R.id.hookah_img);
        price =  itemView.findViewById(R.id.hookah_price);
        subtotal =  itemView.findViewById(R.id.hookah_subtotal);
        color =  itemView.findViewById(R.id.hookah_color);



    }

}
