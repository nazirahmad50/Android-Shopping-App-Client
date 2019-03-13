package com.nazir.shopping;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Model.Rating;
import com.nazir.shopping.ViewHolder.ShowCommentViewHolder;

public class ShowFeedbackComment extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ratingRef;

    FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder> adapter;

    //Swipe Layout
    SwipeRefreshLayout refreshLayout;

    String hookahId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_feedback_comment);

        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        ratingRef = firebaseDatabase.getReference("rating");

        //RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe Layout
        refreshLayout = findViewById(R.id.swipe_layout);

        swipeLayoutIntent();


    }

    private void swipeLayoutIntent(){

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getHookahIntent();
                refreshLayout.setRefreshing(false);



            }
        });

        //Thread to load comment on first launch
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getHookahIntent();
                refreshLayout.setRefreshing(false);
            }
        });


    }

    private void getHookahIntent(){

        if (getIntent() != null){

            hookahId = getIntent().getStringExtra(Common.INTENT_HOOKAH_ID);

            if (!hookahId.isEmpty() && hookahId != null){


                loadFeedBackComments();

            }
        }
    }



    private void loadFeedBackComments(){

        Query query = ratingRef.orderByChild("hookahid").equalTo(hookahId);

        FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                .setQuery(query,Rating.class)
                .build();

            adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShowCommentViewHolder viewHolder, int position, @NonNull Rating model) {

                viewHolder.ratingBar.setRating(Float.parseFloat(model.getRatevalue()));
                viewHolder.txtComment.setText(model.getComment());
                viewHolder.txtUserPhone.setText(model.getUserPhone());




            }

            @NonNull
            @Override
            public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_comment_layout,parent,false);

                return new ShowCommentViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }

//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (adapter != null){
//            adapter.stopListening();
//        }
//
//    }
}
