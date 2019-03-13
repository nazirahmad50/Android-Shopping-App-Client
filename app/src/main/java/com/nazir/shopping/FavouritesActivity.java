package com.nazir.shopping;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Database.Database;
import com.nazir.shopping.Adapter.FavouritesAdapter;
import com.nazir.shopping.Interface.RecyclerItemTouchHelperListener;
import com.nazir.shopping.Model.Favourites;
import com.nazir.shopping.ViewHolder.HookahViewHolder;

public class FavouritesActivity extends Fragment   {

    //RecyclerView
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    FavouritesAdapter adapter;

    //Swipe Delete
    RelativeLayout rootLayout;

    //Refresh Layout
    SwipeRefreshLayout swpRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.activity_favourites,container,false);

        rootLayout = rootView.findViewById(R.id.root_favLayout);

        recyclerView = rootView.findViewById(R.id.recycler_favHookahs);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //Animation
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);

        //Refresh
        swpRefreshLayout = rootView.findViewById(R.id.swipe_layout);
        swipeRefreshLayout();

        loadFavourites();

        return rootView;
    }

    //************************************Swipe Refresh**********************************

    private void swipeRefreshLayout(){

        swpRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_blue_bright);

        swpRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(requireContext())) {
                    loadFavourites();
                    swpRefreshLayout.setRefreshing(false);

                }else{
                    Toast.makeText(requireActivity(), "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default load for the first time
        swpRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if (Common.isConnectedToInternet(requireContext())) {
                    loadFavourites();
                    swpRefreshLayout.setRefreshing(false);

                }else{
                    Toast.makeText(requireActivity(), "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }


    private void loadFavourites() {

        //We use this type of adapter because we are not getting anything from Firebase
        //Instead we are getting it from SQLite Database (local Database)
        adapter = new FavouritesAdapter(getActivity(),new Database(getActivity()).getAllFavourites(Common.cuurentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
//        //instanceof means that viewHolder extends CartViewHolder
//        Log.e("check", "swiped");
//
//        if (viewHolder instanceof HookahViewHolder) {
//
//            //Get the selected postion product name
////            String name = ((FavouritesAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
//
//            //Able to access the 'Order' model variables of the deleted item
//            final Favourites deleteItem = ((FavouritesAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
//
//            //Get the position of the currently selcted item (postion of the item in recycler view that is going to be deleted)
//            final int deleteIndex = viewHolder.getAdapterPosition();
//
//            //Remove from Cart
//            adapter.removeItem(deleteIndex);
//            adapter.notifyDataSetChanged();
//
//            //Remove from database
//            new Database(requireActivity()).removeFromFavourites(deleteItem.getHookahId(), Common.cuurentUser.getPhone());
//        }
//    }
}
