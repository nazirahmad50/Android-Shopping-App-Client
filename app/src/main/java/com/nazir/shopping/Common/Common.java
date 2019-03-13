package com.nazir.shopping.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

import com.nazir.shopping.Model.RequestUserInfo;
import com.nazir.shopping.Model.User;
import com.nazir.shopping.Remote.APIService;
import com.nazir.shopping.Remote.IGoogleService;
import com.nazir.shopping.Remote.RetrofitClient;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Retrofit;

public class Common {

    //variable to save current user
    public static User cuurentUser;

    public static String PHONE_TEXT = "userPhone";

    public static String INTENT_HOOKAH_ID = "HookahId";


    //***************************Remember User********************
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    //***************************Delete Cart Item********************
    public static final String DELETE = "Delete";

    //Used for order detail activity
    public static RequestUserInfo requestUserInfo;


    public static String convertCodeStatus(String status) {

        if (status.equals("0")){
            return "Placed";
        }
        else if (status.equals("1")){
            return "Shipping";
        }
        else {
            return "Shipped";
        }

    }


    //*******************************Internet Connection********************
    public static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null){

                for (int i=0; i<info.length;i++){

                    if (info[i].getState() == NetworkInfo.State.CONNECTED){

                        return true;
                    }

                }
            }
        }

        return false;

    }


    //********************************Notification*****************
    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    //********************************Google Maps Api*****************

    public static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleService getGoogleMapApi(){

        return RetrofitClient.getGoogleApiClient(GOOGLE_API_URL).create(IGoogleService.class);
    }



    public static String getDate(long time){

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm",
                                calendar).toString());

        return date.toString();

    }



    //*************************************************************Used for Share Intent************************************************************************

    public static Uri getlocalBitmapUri(Context context, Bitmap bmp) {


        Uri bmpUri = null;

        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Share Image" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmpUri;
    }

}
