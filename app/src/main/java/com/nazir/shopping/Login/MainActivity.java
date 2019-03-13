package com.nazir.shopping.Login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.iml.ActionProcessButton;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nazir.shopping.Common.Common;
import com.nazir.shopping.Model.User;
import com.nazir.shopping.Navigation.HomeActivity;
import com.nazir.shopping.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    ActionProcessButton btn_continue;

    //Facebook Phone Number Verification
    private static final int REQUEST_CODE = 7171;

    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef;

    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //init Facebook Phone Number Verification
        AccountKit.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);


        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("user");

        btn_continue = findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Set up Facebook Account Kit
                Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);

                AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                        new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                                AccountKitActivity.ResponseType.TOKEN);

                intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
                startActivityForResult(intent, REQUEST_CODE);



            }
        });

         handler = new Handler();

        rememberUser();




    }



    private void rememberUser(){


        //Check Session Facebook Account (Remember User)
        if (AccountKit.getCurrentAccessToken() != null) {

            //Show waiting dialog
            final AlertDialog waitingDialog = new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Please Wait");
            waitingDialog.setCancelable(false);



                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {

                        //Login
                        usersRef.child(account.getPhoneNumber().toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        User localUser = dataSnapshot.getValue(User.class);

                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        Common.cuurentUser = localUser;
                                        startActivity(intent);

                                        waitingDialog.dismiss();
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {

                    }
                });
            }
        }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE){

            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null){

                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            else  if (result.wasCancelled()) {

                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                return;
            }else {

                if (result.getAccessToken() != null){



                    loginOrRegsiter();

                }
            }
        }
    }

    private void loginOrRegsiter(){

      //Show waiting dialog
      final AlertDialog waitingDialog = new SpotsDialog(this);
      waitingDialog.show();
      waitingDialog.setMessage("Please Wait");
      waitingDialog.setCancelable(false);




      //Get current Phone
      AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
          @Override
          public void onSuccess(Account account) {

              final String userPhone = account.getPhoneNumber().toString();

              //Check if userphone exists on firebase users
              usersRef.orderByKey().equalTo(userPhone)
                      .addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                              if (!dataSnapshot.child(userPhone).exists()){ //If user doesnt exist

                                  //Create new user and login
                                  User newUser = new User();
                                  newUser.setPhone(userPhone);
                                  newUser.setName("");

                                  //Add to Firebase
                                  usersRef.child(userPhone).setValue(newUser)
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {

                                                  if (task.isSuccessful()){
                                                      Toast.makeText(MainActivity.this, "User Register Successfull", Toast.LENGTH_SHORT).show();

                                                      //Login
                                                      usersRef.child(userPhone)
                                                              .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                  @Override
                                                                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                      User localUser = dataSnapshot.getValue(User.class);

                                                                      Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                                      Common.cuurentUser = localUser;
                                                                      startActivity(intent);
                                                                      waitingDialog.dismiss();
                                                                      finish();

                                                                  }

                                                                  @Override
                                                                  public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                  }
                                                              });

                                                    }

                                              }
                                          });
                              }else{ //If user already exists

                                  //Login
                                  usersRef.child(userPhone)
                                          .addListenerForSingleValueEvent(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                  User localUser = dataSnapshot.getValue(User.class);

                                                  Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                  Common.cuurentUser = localUser;
                                                  startActivity(intent);
                                                  waitingDialog.dismiss();
                                                  finish();

                                              }

                                              @Override
                                              public void onCancelled(@NonNull DatabaseError databaseError) {

                                              }
                                          });

                              }
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });

          }

          @Override
          public void onError(AccountKitError accountKitError) {

              Toast.makeText(MainActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();

          }
      });


  }







}
