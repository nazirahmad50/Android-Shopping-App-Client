package com.nazir.shopping.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nazir.shopping.Interface.ItemClickListener;
import com.nazir.shopping.R;

public class HookahViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "HookahViewHolder";

    public TextView hookahName,hookahPrice;
    public ImageView hookahImage, fav_image,share_image,cart_img;

    private ItemClickListener itemClickListener;



    //Wen need to use the setter method for the itemClickListener interface so new itemclickListener can be created in other activities
    //And will be able to call its method 'onClick'
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public HookahViewHolder(View itemView) {
        super(itemView);


        hookahName = itemView.findViewById(R.id.hookah_item);
        hookahPrice = itemView.findViewById(R.id.hookah_price);
        hookahImage = itemView.findViewById(R.id.hookah_image);
        fav_image = itemView.findViewById(R.id.fav);
        share_image = itemView.findViewById(R.id.btnShare);
        cart_img = itemView.findViewById(R.id.cart_img);


        itemView.setOnClickListener(this);
    }

    /**
     * This viewHolder method is responsible for that happens when clicked on reyclerView cell
     * @param view
     */
    @Override
    public void onClick(View view) {

        /**We need the itemClickListener interface so that we can call its method (onClick) and pass the 3 parameters
         * Especialy the parameter 'getAdapterPostion()' which is only accessible to this viewHolder class method 'onClick'
         */
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }
}
