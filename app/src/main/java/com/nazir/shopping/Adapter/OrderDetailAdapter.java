package com.nazir.shopping.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.nazir.shopping.Model.Order;
import com.nazir.shopping.R;
import com.nazir.shopping.ViewHolder.OrderDetailViewHolder;
import com.squareup.picasso.Picasso;


import java.util.List;
public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder> {

    List<Order> ordersList;
    Context context;

    public OrderDetailAdapter(Context context, List<Order> ordersList) {
        this.ordersList = ordersList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_item_layout,parent,false);

        return new OrderDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, final int position) {

        final Order order = ordersList.get(position);
        holder.name.setText(String.format("Name : %s", order.getProductName()));
        holder.price.setText(String.format("Price : £%s", order.getPrice()));
        holder.quantity.setText(String.format("Quantity : %s", order.getQuantity()));

        holder.subtotal.setText(String.format("Sub-Total : £%s", Integer.parseInt(order.getQuantity()) * Double.valueOf(order.getPrice())));
holder.color.setText("Color: "+order.getColor());
        Picasso.with(context).load(order.getImage()).into(holder.hookah_img);




    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }
}
