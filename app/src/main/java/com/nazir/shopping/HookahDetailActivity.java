package com.nazir.shopping;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.nazir.shopping.Adapter.ViewPagerAdapter;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Database.Database;
import com.nazir.shopping.Model.Hookah;
import com.nazir.shopping.Model.Images;
import com.nazir.shopping.Model.Order;
import com.nazir.shopping.Model.Rating;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;


public class HookahDetailActivity extends AppCompatActivity implements RatingDialogListener {
    private static final String TAG = "HookahDetailActivity";

    TextView hookahName, hookahPrice, hookahDescription;
    ImageView hookahImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating;
    CounterFab btnCart;
    ActionProcessButton btnShowComment;

    ElegantNumberButton amountBtn;

    RatingBar ratingBar;

    String hookahId = "";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference hookahDetailRef;

    Hookah currentHookah;

    //Rating
    DatabaseReference ratingRef;

    ViewPagerAdapter adapter;
    ViewPager viewPager;
    SpringDotsIndicator dotsIndicator;
    MaterialSpinner spinner;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hookah_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Enables the back arrow on top of the toolbar in order to go back by clicking on the arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

       dotsIndicator = findViewById(R.id.worm_dots_indicator);

//        adapter = new ViewPagerAdapter(HookahDetailActivity.this,currentHookah.getImages());
        viewPager = findViewById(R.id.view_pager);
//        viewPager.setAdapter(adapter);
//        dotsIndicator.setViewPager(viewPager);


        firebaseDatabase = FirebaseDatabase.getInstance();
        hookahDetailRef = firebaseDatabase.getReference("hookahs");


        hookahName = findViewById(R.id.hookah_name);
        hookahPrice = findViewById(R.id.hookah_price);
        hookahDescription = findViewById(R.id.hookah_description);
        hookahImage = findViewById(R.id.image_hookah);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        

        btnCart = findViewById(R.id.btnCart);
        amountBtn = findViewById(R.id.amount_button);

        //Rating
        btnRating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);

        ratingRef = firebaseDatabase.getReference("rating");



        //STEP 1.
        if (getIntent() != null){
            hookahId = getIntent().getStringExtra("hookahId");

            if (!hookahId.isEmpty()){

                if (Common.isConnectedToInternet(this)){
                gethookahDetail(hookahId);
                getRatingHookah(hookahId);

                }else{
                    Toast.makeText(HookahDetailActivity.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        //STEP 2.
        btnCartClicked();
        btnRatingClicked();

        //Show Comment Button
        btnShowComment = findViewById(R.id.btnShowComment);
        btnShowCommentClicked();





    }

    //***********************************************Comment Button**************************************

    private void btnShowCommentClicked(){

        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HookahDetailActivity.this,ShowFeedbackComment.class);
                intent.putExtra(Common.INTENT_HOOKAH_ID,hookahId);
                startActivity(intent);
            }
        });


    }



    //***********************************************Cart Button**************************************

    /**
     * This method gets the hookah detail fields and passes it on to the Database class method called 'addToCart'
     */

    private void btnCartClicked(){


        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (amountBtn.getNumber().equals("1")){
//                    btnCart.increase();
//                    new Database(getBaseContext()).increaseCartItem(Common.cuurentUser.getPhone(),hookahId, list.get(spinner.getSelectedIndex()));
//
//                }
//                else if (Integer.parseInt(amountBtn.getNumber()) >1){
//                    btnCart.setCount(btnCart.getCount() + Integer.parseInt(amountBtn.getNumber()));
//                }
                boolean isExist = new Database(getApplicationContext()).checkHookahExists(hookahId, Common.cuurentUser.getPhone(),list.get(spinner.getSelectedIndex()));
                    if (!isExist) {
                        new Database(getBaseContext()).addToCart(new Order(
                                Common.cuurentUser.getPhone(),
                                hookahId,
                                currentHookah.getName(),
                                amountBtn.getNumber(),
                                currentHookah.getPrice(),
                                currentHookah.getDiscount(),
                                currentHookah.getImage(),
                                list.get(spinner.getSelectedIndex())
                        ));
                    } else {

                        new Database(getBaseContext()).increaseCartItem(Common.cuurentUser.getPhone(), hookahId, list.get(spinner.getSelectedIndex()));
                    }

            }

        });


    }

    //***********************************************Load Hookah Details**************************************

    /**
     * Get hookah detail from the database based on the param hookahId passed
     * @param hookahId
     */
    private void gethookahDetail(String hookahId) {

        hookahDetailRef.child(hookahId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentHookah = dataSnapshot.getValue(Hookah.class);

//                Picasso.with(getApplicationContext())
//                        .load(currentHookah.getImage())
//                        .into(hookahImage);

                adapter = new ViewPagerAdapter(HookahDetailActivity.this,currentHookah.getImages());
                viewPager.setAdapter(adapter);
                dotsIndicator.setViewPager(viewPager);

                list = new ArrayList<>();

                for (int i =0; i<currentHookah.getImages().size();i++){
                     list.add(currentHookah.getImages().get(i).getColor());
                }

                spinner =  findViewById(R.id.spinner);
                spinner.setItems(list);
                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                    @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        Toast.makeText(HookahDetailActivity.this, ""+item, Toast.LENGTH_SHORT).show();
                    }
                });

                collapsingToolbarLayout.setTitle(currentHookah.getName());

                hookahName.setText(currentHookah.getName());
                hookahDescription.setText(currentHookah.getDescription());

                Locale locale = new Locale("en", "GBP");
                NumberFormat numbFormat = NumberFormat.getCurrencyInstance(locale.UK);

                hookahPrice.setText(numbFormat.format(Double.valueOf(currentHookah.getPrice())));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //******************************************Rating Functions********************************

    private void getRatingHookah(String hookahId) {

        Query hookahRating = ratingRef.orderByChild("hookahid").equalTo(hookahId);

        hookahRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+= Integer.parseInt(item.getRatevalue());
                    count++;
                }

               if (count != 0){
                   float average = sum/count;
                   ratingBar.setRating(average);
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void btnRatingClicked() {

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AppRatingDialog.Builder()
                        .setPositiveButtonText("Submit")
                        .setNegativeButtonText("Cancel")
                        .setNoteDescriptions(Arrays.asList("very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                        .setDefaultRating(1)
                        .setTitle("Rate This Hookah")
                        .setDescription("Please select some stars and give your feedback")
                        .setTitleTextColor(R.color.colorPrimary)
                        .setHint("Please write your comment here...")
                        .setHintTextColor(R.color.colorAccent)
                        .setCommentTextColor(R.color.white)
                        .setCommentBackgroundColor(R.color.colorPrimaryDark)
                        .setWindowAnimation(R.style.RatingDialogFadeAnim)
                        .create(HookahDetailActivity.this)
                        .show();
            }
        });
    }


    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        //Upload rating to firebase
        final Rating rating = new Rating(Common.cuurentUser.getPhone(),
                hookahId,String.valueOf(value),comments);

        ratingRef.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(HookahDetailActivity.this, "Thank You for your Feedback", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    //*************************************************************Close activity after back Arrow pressed************************************************************************

    //Closes Activity after taping back Arrow at top of toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); ////Closes Activity after taping back Arrow at top of toolbar
        }

        return super.onOptionsItemSelected(item);
    }
}
