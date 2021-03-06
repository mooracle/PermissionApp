package com.mooracle.permissionapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 11;
    private int callButtonClickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate the call button object from the activity main layout:
        Button callButton = findViewById(R.id.callButton);

        // set on click listeners for the call button and make it call a number:
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });
    }

    // make a separate method to make a call and call this method from the on click listeners
    private void makeCall() {
        //add the counter
        callButtonClickCounter += 1;

        // check if under marshmallow skip the permission check:
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            //this is not recommended but this should be okay since after start activity this activity will be paused

            // make an intent to action call:
            Intent intent = new Intent(Intent.ACTION_CALL);

            // set the data attribute the tel: and number:
            intent.setData(Uri.parse("tel:" + "12345678"));

            startActivity(intent);
        }

        // start the new activity using the intent: (promptly will invoke error due lack of permission)
        // Fix add permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // set a message using the latest Activity compat shouldshowRequestPermissionRationale method:
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)){
                // set message
                new AlertDialog.Builder(this).setTitle("Call Permission Needed")
                        .setMessage("The app needs your permission to call, please grant it")
                        .setPositiveButton("Yep", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // recall the request permission:
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                        Manifest.permission.CALL_PHONE
                                }, PERMISSIONS_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this,
                                        "Sorry, the app cannot operate with no call permission :(",
                                        Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }// set else scenario thus lock the app if finally the user click deny and not to ask again:
            else {
                // Add warning that the user already set to deny the permission
                //    that this can only be fixed from setting>app, find the permission App settings and change the
                //    permissions to give access to call
                if (callButtonClickCounter > 1){
                    new AlertDialog.Builder(this).setTitle("Error")
                            .setMessage("The app need permission to access the Phone Call, " +
                                    "Unfortunately the last setting was fix to deny access to it, " +
                                    "The app requires you to change the permission through Settings>App, " +
                                    "Then change the permission setting to Permission App")
                            .setPositiveButton("Okay, I get it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                        PERMISSIONS_REQUEST_CODE);
            }


        }else {
            // make an intent to action call:
            Intent intent = new Intent(Intent.ACTION_CALL);

            // set the data attribute the tel: and number:
            intent.setData(Uri.parse("tel:" + "12345678"));

            startActivity(intent);
        }

    }

    // override method to handle the request permission results from makeCall method permission check:
    //note since there is only one permission asked then we do not need a case just if statement
    //in the case that there are more than one permission in question we need to use case switch statement.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // check if the requestCode is not null and same as PERMISSION_REQUEST_CODE
        if (requestCode == PERMISSIONS_REQUEST_CODE){
            if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //permission granted
                makeCall();
            }else { //permission denied
                Toast.makeText(MainActivity.this,
                        "Sorry, the app cannot operate with no call permission :(",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
