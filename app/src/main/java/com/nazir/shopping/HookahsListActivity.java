package com.nazir.shopping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Database.Database;
import com.nazir.shopping.Interface.ItemClickListener;
import com.nazir.shopping.Model.Favourites;
import com.nazir.shopping.Model.Hookah;
import com.nazir.shopping.Model.Order;
import com.nazir.shopping.ViewHolder.HookahViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HookahsListActivity extends AppCompatActivity {



    //RecyclerView
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference hookahListRef;
    FirebaseRecyclerAdapter<Hookah, HookahViewHolder> adapter;


    String categoryId = "";


    //Search Functionality
    FirebaseRecyclerAdapter<Hookah, HookahViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar searchBar;

    //Favourites
    Database localDB;

    //Refresh Layout
    SwipeRefreshLayout swpRefreshLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hookahs_list);

        //Needed for Share Intent (FileUriExposedException)
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        firebaseDatabase =  FirebaseDatabase.getInstance();
        hookahListRef = firebaseDatabase.getReference("hookahs");

        recyclerView = findViewById(R.id.recycler_hookahs);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getHookahIntent();

        //Search Bar
        searchBar = findViewById(R.id.searchBar);
        searchBarFunctionality();

        //Local DB for favourites
        localDB = new Database(this);


        //Refresh
        swpRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout();


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
                if (Common.isConnectedToInternet(getBaseContext())) {
                    getHookahIntent();
                    swpRefreshLayout.setRefreshing(false);

                }else{
                    Toast.makeText(HookahsListActivity.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default load for the first time
        swpRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if (Common.isConnectedToInternet(getBaseContext())) {
                    getHookahIntent();
                    swpRefreshLayout.setRefreshing(false);

                }else{
                    Toast.makeText(HookahsListActivity.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }

    /**
     * Get the intent extra 'CategoryId' from 'HomeActivity' and check if its not null
     * If the intent extra and the 'CategoryId' is not null call the method 'LoadListHookah'
     */

    private void getHookahIntent(){
        if (getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");

            if (!categoryId.isEmpty() && categoryId != null){

                if (Common.isConnectedToInternet(this)) {
                    loadListHookah(categoryId);
                }else{
                    Toast.makeText(HookahsListActivity.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }



    }



    //************************************Load List**********************************
    /**
     * Loads the hookah list based on the 'categoryId' passed into the param
     * @param categoryId
     */
    private void loadListHookah(String categoryId){

        Query queryById = hookahListRef.orderByChild("menuid").equalTo(categoryId); //select from hookash where menu id equal to category id clicked

        FirebaseRecyclerOptions<Hookah> options = new FirebaseRecyclerOptions.Builder<Hookah>()
                .setQuery(queryById,Hookah.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Hookah,HookahViewHolder>(options){

            @NonNull
            @Override
            public HookahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.hookah_items_layout,parent,false);

                return new HookahViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull final HookahViewHolder viewHolder, final int position, @NonNull final Hookah model) {
                viewHolder.hookahName.setText(model.getName());

                Locale locale = new Locale("en", "GBP");
                NumberFormat numbFormat = NumberFormat.getCurrencyInstance(locale.UK);

                viewHolder.hookahPrice.setText(numbFormat.format(Double.valueOf(model.getPrice())));

                Picasso.with(getApplicationContext())
                        .load(model.getImage())
                        .into(viewHolder.hookahImage);

                //Add Favourites
                if (localDB.isFavourites(adapter.getRef(position).getKey(), Common.cuurentUser.getPhone())) {

                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                //Click to change state of Favourites icon
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favourites favourites = new Favourites();
                        favourites.setHookahId(adapter.getRef(position).getKey());
                        favourites.setHookahName(model.getName());
                        favourites.setHookahDescription(model.getDescription());
                        favourites.setHookahDiscount(model.getDiscount());
                        favourites.setHookahImage(model.getImage());
                        favourites.setHookahPrice(model.getPrice());
                        favourites.setHookahMenuId(model.getMenuid());
                        favourites.setUserPhone(Common.cuurentUser.getPhone());


                        if (!localDB.isFavourites(adapter.getRef(position).getKey(), Common.cuurentUser.getPhone())) {
                            localDB.addToFavourites(favourites);
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                        } else {
                            localDB.removeFromFavourites(adapter.getRef(position).getKey(), Common.cuurentUser.getPhone());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        }
                    }
                });

                //Share Button
                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(HookahsListActivity.this)
                                .load(model.getImage())
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("image/*");
                                        intent.putExtra(Intent.EXTRA_STREAM, Common.getlocalBitmapUri(HookahsListActivity.this,bitmap));
                                        intent.putExtra(Intent.EXTRA_TEXT, "Magix Shisha");
                                        startActivity(Intent.createChooser(intent, "Share Via"));

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

                //Quick Cart Button
                //First it checks if there is an item in the cart
//                    viewHolder.cart_img.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            boolean isExist = new Database(getApplicationContext()).checkHookahExists(adapter.getRef(position).getKey(), Common.cuurentUser.getPhone());
//                            if (!isExist) {
//                                new Database(getBaseContext()).addToCart(new Order(
//                                        Common.cuurentUser.getPhone(),
//                                        adapter.getRef(position).getKey(),
//                                        model.getName(),
//                                        "1",
//                                        model.getPrice(),
//                                        model.getDiscount(),
//                                        model.getImage(),
//                                        "Red"
//
//                                ));
//
//                            }else{
//
//                                new Database(getBaseContext()).increaseCartItem(Common.cuurentUser.getPhone(),adapter.getRef(position).getKey());
//                            }
//
//                            Toast.makeText(HookahsListActivity.this, model.getName()+ " Added to Cart", Toast.LENGTH_SHORT).show();
//                        }
//
//                    });



                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {

                        Intent intent = new Intent(HookahsListActivity.this, HookahDetailActivity.class);
                        intent.putExtra("hookahId", adapter.getRef(postion).getKey());
                        startActivity(intent);

                    }
                });


            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadListHookah(categoryId);
    }




    //************************************SearchBar Functions ************************************

    private void searchBarFunctionality(){

        searchBar.setHint("Enter your Hookah");
        loadSuggest();

        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);

        //Changing the search bar text listener
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //When user types their text wil change suggest list

                List<String> suggest = new ArrayList<>();


                for (String search:suggestList){

                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())){

                        suggest.add(search);
                    }
                }
                searchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //Listener for what happens after user taps search
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                //when searchbar is close
                //restore original suggest adapter
                if (!enabled){
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finished
                //show result of search adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    /**
     * When user clicks search after typing their hookah name which is the text param that is passed on,
     * from searchListener to startSearch
     * @param text
     */
    private void startSearch(CharSequence text) {

        //Query to search by name
        Query searchByName = hookahListRef.orderByChild("name").equalTo(text.toString());

        FirebaseRecyclerOptions<Hookah> options = new FirebaseRecyclerOptions.Builder<Hookah>()
                .setQuery(searchByName,Hookah.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Hookah, HookahViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HookahViewHolder viewHolder, int position, @NonNull Hookah model) {
                viewHolder.hookahName.setText(model.getName());

                Picasso.with(getApplicationContext())
                        .load(model.getImage())
                        .into(viewHolder.hookahImage);

                final Hookah local = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int postition, boolean isLongClick) {

                        Intent intent = new Intent(HookahsListActivity.this, HookahDetailActivity.class);
                        intent.putExtra("hookahId", searchAdapter.getRef(postition).getKey());
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public HookahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.hookah_items_layout,parent,false);

                return new HookahViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//
//        if (searchAdapter != null){
//            searchAdapter.stopListening();
//         }
//    }

    /**
     * Loads suggestions from database by based on the menuid being equal to the categoryId
     */
    private void loadSuggest() {

        hookahListRef.orderByChild("menuid").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                            Hookah item = postSnapshot.getValue(Hookah.class);
                            suggestList.add(item.getName());

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
