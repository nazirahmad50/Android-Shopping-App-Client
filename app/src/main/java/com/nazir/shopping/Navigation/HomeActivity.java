package com.nazir.shopping.Navigation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nazir.shopping.CartActivity;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Database.Database;
import com.nazir.shopping.FavouritesActivity;
import com.nazir.shopping.HookahDetailActivity;
import com.nazir.shopping.HookahsListActivity;
import com.nazir.shopping.Interface.ItemClickListener;
import com.nazir.shopping.Login.MainActivity;
import com.nazir.shopping.Model.Banner;
import com.nazir.shopping.Model.Category;
import com.nazir.shopping.Model.Token;
import com.nazir.shopping.Model.User;
import com.nazir.shopping.R;
import com.nazir.shopping.SearchActivity;
import com.nazir.shopping.ViewHolder.MenuViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference categoryRef;

    TextView txtFullName;

    RecyclerView recycler_Menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    //Refresh
    SwipeRefreshLayout swpRefreshLayout;

    //Cart button count
    CounterFab fab;
    
    //Banner Slider
    HashMap<String,String> img_list;
    SliderLayout mSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");

        setSupportActionBar(toolbar);

        //Load Menu
        recycler_Menu = findViewById(R.id.recycler_menu);

//        layoutManager = new LinearLayoutManager(this);
//        recycler_Menu.setLayoutManager(layoutManager);
        recycler_Menu.setLayoutManager(new GridLayoutManager(this,2));

        //init Firebase
        database = FirebaseDatabase.getInstance();
        categoryRef = database.getReference("category");





        //Animation
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_Menu.getContext(),
                R.anim.layout_fall_down);
        recycler_Menu.setLayoutAnimation(controller);

        //This should be in in the 'LoadMenu' function but for animation purpose this is moved here because its static
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(categoryRef,Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category,MenuViewHolder>(options){

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item_layout, parent,false);
                return new MenuViewHolder(itemView);

            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {

                viewHolder.txtMenuName.setText(model.getName());

                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.imageView);


                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {

                        //Get category id
                        Intent intent = new Intent(HomeActivity.this, HookahsListActivity.class);
                        intent.putExtra("CategoryId", adapter.getRef(postion).getKey());
                        startActivity(intent);



                    }
                });

            }
        };



        //Cart Floating Button
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        //Navigation Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set navigation drawer header Name for user
        View headerView = navigationView.getHeaderView(0);

        txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.cuurentUser.getName());


        if (Common.isConnectedToInternet(this)) {
            loadMenu();
        }else{
            Toast.makeText(HomeActivity.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        //Notification service
        updateToken(FirebaseInstanceId.getInstance().getToken());

        //Refresh
        swpRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout();
        
        //Banner Slider
        setupBannerSlider();

        


    }

    private void setupBannerSlider() {

        mSlider = findViewById(R.id.bannerSlider);

        img_list = new HashMap<>();

        final DatabaseReference bannerRef = database.getReference("banner");

        bannerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Banner banner = postSnapshot.getValue(Banner.class);

                    // Concat the key e.g 'Cobra@@@01' -> use 'Cobra' to show descrpiton of slider and 01 for hookah id to click
                    img_list.put(banner.getName()+ "@@@" + banner.getId(), banner.getImage());

                }

                for (String key:img_list.keySet()){

                    String[] keySplit = key.split("@@@");


                    String nameOfHookah = keySplit[0];
                    String idOfHookah = keySplit[1];


                    //Create Slider
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());

                    //Add extra bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("hookahId",idOfHookah);

                    textSliderView
                            .description(nameOfHookah)
                            .image(img_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {

                                    Intent intent = new Intent(HomeActivity.this, HookahDetailActivity.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);

                                }
                            });


                    mSlider.addSlider(textSliderView);

                    //Remove evetnt after finish
                    bannerRef.removeEventListener(this);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(3000);

    }

    //*******************************************Refresh Function***************************************

    private void swipeRefreshLayout(){

        swpRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_blue_bright);

        swpRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu();
                    swpRefreshLayout.setRefreshing(false);

                }else{
                    Toast.makeText(HomeActivity.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default load for the first time
        swpRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu();
                    swpRefreshLayout.setRefreshing(false);

                }else{
                    Toast.makeText(HomeActivity.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }


    //*******************************************Notification Function***************************************

    private void updateToken(String token) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference tokens = firebaseDatabase.getReference("tokens");
        Token data = new Token(token,false); //false because this token sent from Client app
        tokens.child(Common.cuurentUser.getPhone()).setValue(data);




    }


    //*******************************************Load Menu***************************************
    private void loadMenu() {

             adapter.startListening();
             recycler_Menu.setAdapter(adapter);

             //Animation
            recycler_Menu.getAdapter().notifyDataSetChanged();
            recycler_Menu.scheduleLayoutAnimation();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadMenu();
        fab.setCount(new Database(this).getCountCart(Common.cuurentUser.getPhone()));

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//        mSlider.stopAutoCycle();
//    }



    //******************************************* Change Password Dialog***************************************
    private void showChangeNameDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Change Name");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_username_layout,null);

        final MaterialEditText edtName = layout_pwd.findViewById(R.id.edt_user_name);


        alertDialog.setView(layout_pwd);

        //Show Data from Firebase in the EditText Fields
        FirebaseDatabase.getInstance().getReference("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.child(Common.cuurentUser.getPhone()).getValue(User.class);

                edtName.setText(user.getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final ProgressDialog watitingDialog = new ProgressDialog(HomeActivity.this);
                watitingDialog.show();


                        Map<String,Object> nameUpdate = new HashMap<>();
                        nameUpdate.put("name", edtName.getText().toString());

                        //Database update
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("user");
                        user.child(Common.cuurentUser.getPhone())
                                .updateChildren(nameUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        watitingDialog.dismiss();
                                        Toast.makeText(HomeActivity.this, "Name Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });






            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();

    }


    //******************************************* Change Home Address Dialog***************************************
    private void showChangeHomeAddressDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Change Home Address");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home_address = inflater.inflate(R.layout.home_address_layout,null);

        final MaterialEditText edtAddress = layout_home_address.findViewById(R.id.edtAddress);
        final MaterialEditText edPostcode = layout_home_address.findViewById(R.id.edtPostcode);
        final MaterialEditText edtCity = layout_home_address.findViewById(R.id.edtCity);

        alertDialog.setView(layout_home_address);

        //Show Data from Firebase in the EditText Fields
        FirebaseDatabase.getInstance().getReference("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.child(Common.cuurentUser.getPhone()).getValue(User.class);

                edtAddress.setText(user.getAddress());
                edPostcode.setText(user.getPostcode());
                edtCity.setText(user.getCity());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final ProgressDialog watitingDialog = new ProgressDialog(HomeActivity.this);
                watitingDialog.show();


                Common.cuurentUser.setAddress(edtAddress.getText().toString());
                Common.cuurentUser.setPostcode(edPostcode.getText().toString());
                Common.cuurentUser.setCity(edtCity.getText().toString());

                //Set the values in firebase current user (User class)
                FirebaseDatabase.getInstance().getReference("user")
                        .child(Common.cuurentUser.getPhone())
                        .setValue(Common.cuurentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                Toast.makeText(HomeActivity.this, "Upadted Successfuly", Toast.LENGTH_SHORT).show();
                                watitingDialog.dismiss();
                            }
                        });





            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();

    }




    //*******************************************Default Menu functions***************************************
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        getSupportActionBar().setTitle("Menu");
        fab.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_search){

            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

            getSupportActionBar().setTitle("Menu");

            startActivity(new Intent(HomeActivity.this,HomeActivity.class));
            finish();


        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_orders) {
            getSupportActionBar().setTitle("Orders");

            fab.setVisibility(View.GONE);

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.root_favLayout,new OrderStatusActivity());
            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStackImmediate();

            ft.commit();


        } else if (id == R.id.nav_log_out) {

            //Delete Remembered User Phone/Email not from FireBase
            AccountKit.logOut();

            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        } else if (id == R.id.nav_change_username) {

            showChangeNameDialog();

        }else if (id == R.id.nav_fav) {


             getSupportActionBar().setTitle("Favourites");

            fab.setVisibility(View.GONE);

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.root_favLayout,new FavouritesActivity());
            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStackImmediate();

            ft.commit();


        }

        else if (id == R.id.nav_change_address) {

            showChangeHomeAddressDialog();


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
