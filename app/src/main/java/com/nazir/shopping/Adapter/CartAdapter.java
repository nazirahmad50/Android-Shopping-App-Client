package com.nazir.shopping.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.nazir.shopping.CartActivity;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Database.Database;
import com.nazir.shopping.Interface.ItemClickListener;
import com.nazir.shopping.Model.Order;
import com.nazir.shopping.R;
import com.nazir.shopping.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


    public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private CartActivity cartActivity;

    private List<Order> listData;

    public CartAdapter(List<Order> listData, CartActivity cartActivity) {
        this.cartActivity = cartActivity;
        this.listData = listData;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cartActivity);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);

        return new CartViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {

        Picasso.with(cartActivity.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(holder.cart_img);


        holder.btn_quantity.setNumber(listData.get(position).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));

                new Database(cartActivity).updateCart(order);

                //calculate inital price
                int total = 0;
                List<Order> orders = new Database(cartActivity).getCarts(Common.cuurentUser.getPhone());
                for (Order item:orders){

                    total += (Double.valueOf(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                    Locale locale = new Locale("en", "GBP");
                    NumberFormat numbFormat = NumberFormat.getCurrencyInstance(locale.UK);

                    cartActivity.totalPrice.setText(numbFormat.format(total));

                    Double price = (Double.valueOf(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
                    holder.txt_price.setText(numbFormat.format(price));
                }



            }
        });


        Locale locale = new Locale("en", "GBP");
        NumberFormat numbFormat = NumberFormat.getCurrencyInstance(locale.UK);

        Double price = (Double.valueOf(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(numbFormat.format(price));

        holder.txt_cart_name.setText(listData.get(position).getProductName());
        holder.txt_item_color.setText(listData.get(position).getColor());
    }

    @Override
    public int getItemCount() {
            return listData.size();
        }



    //*******************************************Swipe Delete************************************

    public Order getItem(int position){

        return listData.get(position);
    }

    public void removeItem(int position){

        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item, int position){

        listData.add(position,item);
        notifyItemInserted(position);
    }


}
