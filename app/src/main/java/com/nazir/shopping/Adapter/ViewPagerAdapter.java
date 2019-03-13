package com.nazir.shopping.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nazir.shopping.Model.Images;
import com.nazir.shopping.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    Activity activity;
    List<Images> images;
    LayoutInflater layoutInflater;

    public ViewPagerAdapter(Activity activity, List<Images> images) {
        this.activity = activity;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.viewpager_item, container, false);

        ImageView imageView;

        imageView = itemView.findViewById(R.id.image_hookah);


        try{
            Picasso.with(activity.getApplicationContext()).load(images.get(position).getImageLink()).into(imageView);

        }catch (Exception ex){
            Toast.makeText(activity, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View)object);
    }
}
