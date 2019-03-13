package com.nazir.shopping;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.nazir.shopping.Adapter.OrderDetailAdapter;
import com.nazir.shopping.Common.Common;

public class OrderDetailActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView txt_total;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //ToolBar: Display Category Name
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Orders");
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        //Enables the back arrow on top of the toolbar in order to go back by clicking on the arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_order_detail);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        OrderDetailAdapter adapter = new OrderDetailAdapter(this,Common.requestUserInfo.getHookahs());

        txt_total = findViewById(R.id.txt_total);
        txt_total.setText("Total: " +Common.requestUserInfo.getTotal());

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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
