package com.nazir.shopping.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Database.Database;
import com.nazir.shopping.HookahDetailActivity;
import com.nazir.shopping.Interface.ItemClickListener;
import com.nazir.shopping.Model.Favourites;
import com.nazir.shopping.Model.Order;
import com.nazir.shopping.R;
import com.nazir.shopping.ViewHolder.HookahViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;



public class FavouritesAdapter extends RecyclerView.Adapter<HookahViewHolder> {

    private Context context;
    private List<Favourites> favouritesLis;

    public FavouritesAdapter(Context context, List<Favourites> favouritesLis) {
        this.context = context;
        this.favouritesLis = favouritesLis;
    }

    @NonNull
    @Override
    public HookahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.hookah_items_layout, parent, false);
        return new HookahViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HookahViewHolder viewHolder, final int position) {

        viewHolder.hookahName.setText(favouritesLis.get(position).getHookahName());
        viewHolder.hookahPrice.setText(String.format("£ %s", favouritesLis.get(position).getHookahPrice()));

        Picasso.with(context)
                .load(favouritesLis.get(position).getHookahImage())
                .into(viewHolder.hookahImage);


        //Cart Button
        viewHolder.cart_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(context).addToCart(new Order(
                        favouritesLis.get(position).getUserPhone(),
                        favouritesLis.get(position).getHookahId(),
                        favouritesLis.get(position).getHookahName(),
                        "1",
                        favouritesLis.get(position).getHookahPrice(),
                        favouritesLis.get(position).getHookahDiscount(),
                        favouritesLis.get(position).getHookahImage(),
                        "Red"
                ));

                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        //Share Button
        viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(context)
                        .load(favouritesLis.get(position).getHookahImage())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("image/*");
                                intent.putExtra(Intent.EXTRA_STREAM, Common.getlocalBitmapUri(context,bitmap));
                                intent.putExtra(Intent.EXTRA_TEXT, "Magix Shisha");
                                context.startActivity(Intent.createChooser(intent, "Share Via"));

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
        });

        viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(context).removeFromFavourites(favouritesLis.get(position).getHookahId(), Common.cuurentUser.getPhone());
                favouritesLis.remove(position);
                notifyItemRemoved(position);

            }
        });


        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int postion, boolean isLongClick) {

                Intent intent = new Intent(context, HookahDetailActivity.class);
                intent.putExtra("hookahId", favouritesLis.get(postion).getHookahId());
                context.startActivity(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return favouritesLis.size();
    }

    //******************************For Swipe Delete*************************

    public Favourites getItem(int position){

        return favouritesLis.get(position);
    }
    public void removeItem(int postion) {

        favouritesLis.remove(postion);
        notifyItemRemoved(postion);

    }

    public void restoreItem(Favourites item, int postion) {

        favouritesLis.add(postion, item);
        notifyItemInserted(postion);

    }

}
