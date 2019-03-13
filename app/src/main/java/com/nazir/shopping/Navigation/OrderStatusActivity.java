package com.nazir.shopping.Navigation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nazir.shopping.CartActivity;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Interface.ItemClickListener;
import com.nazir.shopping.Model.Hookah;
import com.nazir.shopping.Model.MyResponse;
import com.nazir.shopping.Model.Notification;
import com.nazir.shopping.Model.RequestUserInfo;
import com.nazir.shopping.Model.Sender;
import com.nazir.shopping.Model.Token;
import com.nazir.shopping.OrderDetailActivity;
import com.nazir.shopping.R;
import com.nazir.shopping.Remote.APIService;
import com.nazir.shopping.ViewHolder.HookahViewHolder;
import com.nazir.shopping.ViewHolder.OrderViewHolder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusActivity extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<RequestUserInfo, OrderViewHolder> adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference requestsRef;

    //Notification
    APIService mAPIService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_order_status,container,false);


        firebaseDatabase = FirebaseDatabase.getInstance();
        requestsRef = firebaseDatabase.getReference("request");

        recyclerView = rootView.findViewById(R.id.ordersList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

//        if (getIntent() == null) {
        loadOrder(Common.cuurentUser.getPhone());
//        }else{
//            loadOrder(getIntent().getStringExtra("userPhone"));
//        }

        //init notification Service
        mAPIService = Common.getFCMService();


        return rootView;
    }



    /**
     * This method will load orders based on the current user phone number being equal to the phone number in the database
     * This method also sets the recyclerView by calling its ViewHolder
     * @param phone
     */

    private void loadOrder(String phone) {

        //Query to search by name
        Query searchByPhone = requestsRef.orderByChild("phone").equalTo(phone);

        FirebaseRecyclerOptions<RequestUserInfo> options = new FirebaseRecyclerOptions.Builder<RequestUserInfo>()
                .setQuery(searchByPhone,RequestUserInfo.class)
                .build();

        //Creates the adapter to get the data from firebase and set it to the viewHolder fields
        adapter = new FirebaseRecyclerAdapter<RequestUserInfo, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final RequestUserInfo model) {

                viewHolder.txtOrderId.setText("#"+adapter.getRef(position).getKey());
                viewHolder.txtorderStatus.setText(Common.convertCodeStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));


                //ImageView order delete
                viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (adapter.getItem(position).getStatus().equals("0")){

                            deleteOrder(adapter.getRef(position).getKey());


                        }else{
                            Toast.makeText(getContext(), "Cannot delete this order as it is shipped", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                //Clicking on RecyclerView item
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {
                        Intent intent = new Intent(requireActivity(), OrderDetailActivity.class);
                        Common.requestUserInfo = model;
                        intent.putExtra("hookahid",adapter.getRef(position).getKey());

                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);

                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }



    private void deleteOrder(final String key) {

        requestsRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                sendNotificationOrder(key);


                Toast.makeText(getContext(), "Order " + key + " has been deleted", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //******************************************Notification****************************************

    private void sendNotificationOrder(final String order_numb) {

        DatabaseReference token = FirebaseDatabase.getInstance().getReference("tokens");
        Query data = token.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Token serverToken = postSnapshot.getValue(Token.class);

                    Notification notification = new Notification("Shopping", order_numb +" has been deleted by customer");
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mAPIService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    Log.d("check", ""+response.code()+" "+ response.body());

                                    if (response.body().success == 1) {

                                        Toast.makeText(getContext(), "Order Deleted", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }


                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                    Toast.makeText(getContext(), "Notification"+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }


}
