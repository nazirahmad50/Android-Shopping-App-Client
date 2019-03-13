package com.nazir.shopping;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.MapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Common.Config;
import com.nazir.shopping.Database.Database;
import com.nazir.shopping.Helper.RecyclerItemTouchHelper;
import com.nazir.shopping.Interface.RecyclerItemTouchHelperListener;
import com.nazir.shopping.Model.MyResponse;
import com.nazir.shopping.Model.Notification;
import com.nazir.shopping.Model.Order;
import com.nazir.shopping.Model.RequestUserInfo;
import com.nazir.shopping.Model.Sender;
import com.nazir.shopping.Model.Token;

import com.nazir.shopping.Remote.APIService;
import com.nazir.shopping.Remote.IGoogleService;
import com.nazir.shopping.Adapter.CartAdapter;
import com.nazir.shopping.ViewHolder.CartViewHolder;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener, RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requstRef;

    public TextView totalPrice;
    FlatButton btnPlaceOrder;

    List<Order> cart = new ArrayList<>();

    CartAdapter cartAdapter;

    private int total = 0;

    //Notification
    APIService mAPIService;

    //Paypal Payment
    private static final int PAYPAL_REQUEST_CODE = 9999; //This sets funding amount for Paypal Sandbox account

    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //used sandbox for testing
            .clientId(Config.PAYPAL_CLIENT_ID);

    String address,comment;

    //Google Places
    Place shippingAddress;

    //Location
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private LocationManager locationManager;
    

    private static final int LOCATION_REQUEST_CODE = 9999;

    private static final int PLAY_SERVICES_REQUEST = 9997;

    private static final int ACCESS_FINE_LOCATION_CODE = 3310;
    
    private static final int REQUEST_RESOLVE_ERROR = 555;


    //Google Map Api Retrofit
    IGoogleService mGoogleMapsService;

    //Radio Buttons for Ship to Address
    RadioButton rdioShipTothisAddress;
    RadioButton rdioHomeAddress;

    //Activity Cart Layout for Swipe Delete cart item
    RelativeLayout actvity_cart_layout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //ToolBar: Display Category Name
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Enables the back arrow on top of the toolbar in order to go back by clicking on the arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //Firebase
        database = FirebaseDatabase.getInstance();
        requstRef = database.getReference("request");

        //RecyclerView
        recyclerView = findViewById(R.id.recycler_listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe Delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //Activity Cart Layout for Swipe Delete cart item
        actvity_cart_layout = findViewById(R.id.activity_cart_layout);

        totalPrice = findViewById(R.id.totalPrice);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        //init notification Service
        mAPIService = Common.getFCMService();

        //init Google Maps Api Service
        mGoogleMapsService = Common.getGoogleMapApi();

        //Init PayPal
        Intent intent = new Intent(CartActivity.this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //STEP 1
        loadListHookah();

        //STEP 2
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (totalPrice.getText().toString().equals("£0.00") ){
                    emptyCartDialog();

                }else{

                    if (cart.size() > 0) {

                        showAlertDialog();


                    }else{
                        Toast.makeText(CartActivity.this, "Your Cart is Empty", Toast.LENGTH_SHORT).show();

                    }

                }


            }
        });

        //location Permission
        runTimeLocationPermission();


    }

    //***********************************Load Hookah List*******************************

    private void loadListHookah() {

        //This will call a new Database class and call the method 'getCarts' which will get the data
        //from sql database and set it to the class 'Orders' list
        cart = new Database(this).getCarts(Common.cuurentUser.getPhone());
        cartAdapter = new CartAdapter(cart,this);
        cartAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cartAdapter);

        //calculate inital price
        total = 0;

        for (Order order:cart){

            total += (Double.valueOf(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
            Locale locale = new Locale("en", "GBP");
            NumberFormat numbFormat = NumberFormat.getCurrencyInstance(locale.UK);

            totalPrice.setText(numbFormat.format(total));
        }
  

    }

    //*************************************Show User Current Location**********************************

    private void runTimeLocationPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }else{

            if (checkPlayServices()){//if the device supports google maps services

                buildGoogleApiClient();
                createLocationRequest();
            }
        }
    }

    /**
     * This is to check if user accepted permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode){
            case LOCATION_REQUEST_CODE:
            {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if (checkPlayServices()){//if have play services on device

                        buildGoogleApiClient();
                        createLocationRequest();
                    }

                }
            }
        }
    }

    /**
     * Check if the device supports Google Play services (Google Maps)
     * @return
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS){

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){

                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_REQUEST).show();
            }else{
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(10);



    }

    private void settingsLocationRequest() {

        //This enables the Location widget in the settings in the actual device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                    {

                        if (mGoogleApiClient.isConnected()) {

                                if (ActivityCompat.checkSelfPermission(CartActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(CartActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(CartActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);


                                }
                            } else {
                                // get Location

                        }

                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(CartActivity.this, REQUEST_RESOLVE_ERROR);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

            settingsLocationRequest();
            displayLocation();
            startLocationUpdates();



    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;

        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


    }

    private void startLocationUpdates() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onLocationChanged(Location location) {
            mLastLocation = location;
            displayLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    //*****************************Google Places Autocomplete Edit Address **********************************

    private void autocompleteEditAddress(){
        PlaceAutocompleteFragment edtAddress = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //Hide places search icon before fragment
        //edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        //Set hint for Autocomplete Edit Text
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter Your Address");

        //Set Text Size for Autocomplete Edit Text
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);

        //Get address from Places Autocomplete
        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("Places Auto Complete",""+ status.getStatus());
            }
        });
    }

    //*****************************Dilog Include: Shipping address/Payment Methods **********************************



    private void showAlertDialog(){

        //**Create new alert dialog
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter your address");
        alertDialog.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment_layout,null);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Google places address Textfield
        autocompleteEditAddress();

        final MaterialEditText edtComment = order_address_comment.findViewById(R.id.edtComment);

        //Ship to address Radio Buttons
        shipToAddresses(order_address_comment);




        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Check Addresses Radio Buttons
                if (!rdioShipTothisAddress.isChecked() && !rdioHomeAddress.isChecked()){
                    
                    if (shippingAddress != null){

                        address = shippingAddress.getAddress().toString();


                    }else{
                        Toast.makeText(CartActivity.this, "Please Enter an address or select an address options", Toast.LENGTH_SHORT).show();
                        //Remove places fragment
                        getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                .commit();

                        return;
                    }
                }



                //Check if the Google places address text field is empty
                if (!TextUtils.isEmpty(address)){

                    comment = edtComment.getText().toString();

                    String formatAmount = totalPrice.getText().toString()
                            .replace("£", "")
                            .replace(",", "");


                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                            "GBP",
                            "Magix Shisha App Order",
                            PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);

                    //Remove places fragment (declared in line 19 xml file)
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();



                }else{
                    Toast.makeText(CartActivity.this, "Please Enter an address or select an address options", Toast.LENGTH_SHORT).show();
                    //Remove places fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                }



            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //Remove places fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialog.show();

    }






    private void shipToAddresses(final View order_address_comment) {

         rdioShipTothisAddress = order_address_comment.findViewById(R.id.rdioShipToAddress);
         rdioHomeAddress = order_address_comment.findViewById(R.id.rdioHomeAddress);

        final PlaceAutocompleteFragment edtAddress = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        //Ship to this address
        rdioShipTothisAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (mLastLocation != null) {
                        mGoogleMapsService.getAddressName(String.format(Locale.getDefault(),
                                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false&key=AIzaSyA4CC-ZBYPNJRwcHoSovjS1xQex4Of1qmc",
                                mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //If fetched API is ok
                                       
                                        if (response.isSuccessful()) {

                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body());

                                                JSONArray resultArray = jsonObject.getJSONArray("results");

                                                JSONObject firstObject = resultArray.getJSONObject(0);

                                                address = firstObject.getString("formatted_address");

                                                ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                                        .setText(address);

                                              

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            Toast.makeText(CartActivity.this, "Response not success", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(CartActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{

//                        mLastLocation.reset();
                        Toast.makeText(CartActivity.this, "Please Enable Location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Ship to Home address
        rdioHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    String fullAddress = Common.cuurentUser.getAddress() + " " + Common.cuurentUser.getPostcode() + " " + Common.cuurentUser.getCity();

                    if (Common.cuurentUser.getAddress() != null && Common.cuurentUser.getCity() != null
                            && Common.cuurentUser.getPostcode() != null){
                        if (!TextUtils.isEmpty(fullAddress)) {

                            address = fullAddress;

                            ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                .setText(address);

                        }else {
                            Toast.makeText(CartActivity.this, "Please Choose option or enter your address", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(CartActivity.this, "Please Update Your Home Address", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }




    //*****************************Empty Cart Dialog **********************************

    private void emptyCartDialog(){

        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("Cart is Empty");
        alertDialog.setMessage("Please add items to the cart");

        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }


    //*****************************PayPal Payment**********************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PAYPAL_REQUEST_CODE){

            if (resultCode == RESULT_OK){

                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirmation != null){

                    try {

                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);
                        Log.e("check", ""+confirmation.toJSONObject().toString());




                        RequestUserInfo requestUserInfo = new RequestUserInfo(
                                Common.cuurentUser.getPhone(),
                                Common.cuurentUser.getName(),
                                address,
                                totalPrice.getText().toString(),
                                "0",
                                comment,
                                "PayPal",
                                jsonObject.getJSONObject("response").getString("state"), //state from JSonObject
                                "Lang",//String.format("%s","%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude), //Todo: This crashes Paypal because of being null
                                cart
                        );

                        //submit to firebase
                        //Uses the 'System.CurrentTimeMillis' to set the key for the request node in firebase
                        String order_numb = String.valueOf(System.currentTimeMillis());
                        requstRef.child(order_numb)
                                .setValue(requestUserInfo);

                        //After submitting data to firebase clean the cart activity
                        new Database(getBaseContext()).cleanCart(Common.cuurentUser.getPhone());
                        total = 0;

                        //Notification
                        sendNotificationOrder(order_numb);
                        Toast.makeText(CartActivity.this, "Thank You, Order placed", Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(this, "Not confirmed", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();

            }
             if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID){
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();

            }

        }

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
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

                    Notification notification = new Notification("Shopping", "You have new order "+order_numb);
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mAPIService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    Log.d("check", ""+response.code()+" "+ response.body());

                                        if (response.body().success == 1) {

                                            finish();
                                        } else {
                                            Toast.makeText(CartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                    Toast.makeText(CartActivity.this, "Notification"+t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }

                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    //***********************************Swipe Delete Cart Item*******************************

    private void updateTotalPriceAfterDelete(){

        Locale locale = new Locale("en", "GBP");
        NumberFormat numbFormat = NumberFormat.getCurrencyInstance(locale.UK);

        //Update Total Price
        int total = 0;
        List<Order> orders = new Database(getBaseContext()).getCarts(Common.cuurentUser.getPhone());

        for (Order item:orders) {
            total += (Double.valueOf(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
        }
        if (cartAdapter.getItemCount() > 0) {

            totalPrice.setText(numbFormat.format(total));
        }else {
            totalPrice.setText(numbFormat.format(total));

        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        //instanceof means that viewHolder extends CartViewHolder
        if (viewHolder instanceof CartViewHolder){

            //Get the selected postion product name
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            //Able to access the 'Order' model variables of the deleted item
            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            //Get the position of the currently selcted item (postion of the item in recycler view that is going to be deleted)
            final int deleteIndex = viewHolder.getAdapterPosition();

            //Remove from Cart
            cartAdapter.removeItem(deleteIndex);

            //Remove from database
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(),Common.cuurentUser.getPhone());


            updateTotalPriceAfterDelete();


            //SnackBar for undo (restore) deleted cart item
            Snackbar snackbar = Snackbar.make(actvity_cart_layout,name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cartAdapter.restoreItem(deleteItem,deleteIndex);
                        new Database(getApplicationContext()).addToCart(deleteItem);

                        updateTotalPriceAfterDelete();


                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }

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

