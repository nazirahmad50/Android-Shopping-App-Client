package com.nazir.shopping.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Interface.ItemClickListener;
import com.nazir.shopping.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private static final String TAG = "CartViewHolder";

    public TextView txt_cart_name,txt_price, txt_item_color;
    public ElegantNumberButton btn_quantity;
    public ImageView cart_img;

    //Swipe Delete
    public RelativeLayout view_background;
    public LinearLayout view_foreground;


    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        Log.d(TAG, "CartViewHolder: itemView");

        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_item_color = itemView.findViewById(R.id.cart_item_color);
        txt_price = itemView.findViewById(R.id.cart_item_price);
        btn_quantity = itemView.findViewById(R.id.btn_quantity);
        cart_img = itemView.findViewById(R.id.cart_image);

        //Swipe Delete
        view_background = itemView.findViewById(R.id.view_background);
        view_foreground = itemView.findViewById(R.id.view_foreground);



    }

    @Override
    public void onClick(View v) {

    }


}