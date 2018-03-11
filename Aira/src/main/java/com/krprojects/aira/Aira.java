package com.krprojects.aira;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * This project is created by Kamran Ramzan on 11-Mar-18.
 */

public class Aira {

    // shared preferences to check if the permissions is already granted or not
    private static SharedPreferences permissionStatus;

    // instance of the context
    private static Context context;

    // instance of the Activity
    private static Activity appCompatActivity;

    // Listener that responds to the calling activity or fragment
    private static OnPermissionResultListener onPermissionResultListener;

    // String array of the required permissions
    private static String[] permissionsRequired;

    // int  value to uniquely identify the result in onActivityResult() method
    private static int permissionConstant;
    private static boolean IS_GRANTED;
    private static boolean SHOULD_SEND_TO_SETTINGS;
    private static boolean SHOULD_SHOW_RATIONALE;
    private static boolean ASK_FOR_PERMISSION = false;


    public Aira() {

    }

    /**
     * @param permissionsRequired        Array of String with all the permissions required, this array can contains one or more permissions
     * @param permissionConstant         An integer value to uniquely identify the permission request from other requests
     * @param title                      String value that shows the title of the dialog shown to the user when permissions are already denied by the user
     * @param message                    String value that shows the message of the dialog shown to the user when permissions are already denied by the user
     * @param onPermissionResultListener Listener for the permissions callback
     */

    public static void requestPermission(Context context, @NonNull String[] permissionsRequired, int permissionConstant, @NonNull String title, @NonNull String message, @NonNull OnPermissionResultListener onPermissionResultListener) {

        Aira.context = context;
        permissionStatus = context.getSharedPreferences("com.krprojects.aira", Context.MODE_PRIVATE);
        appCompatActivity = (Activity) context;
        Aira.onPermissionResultListener = onPermissionResultListener;

        Aira.permissionsRequired = permissionsRequired;
        Aira.permissionConstant = permissionConstant;


        for (String permissionRequired :
                Aira.permissionsRequired) {
            IS_GRANTED = false;
            SHOULD_SEND_TO_SETTINGS = false;
            SHOULD_SHOW_RATIONALE = false;
            ASK_FOR_PERMISSION = false;
            if (ActivityCompat.checkSelfPermission(context, permissionRequired) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionRequired)) {
                    //Show Information about why you need the permission
                    SHOULD_SHOW_RATIONALE = true;
                    SHOULD_SEND_TO_SETTINGS = false;
                    IS_GRANTED = false;
                    ASK_FOR_PERMISSION = false;
                } else if (permissionStatus.getBoolean(permissionRequired, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    SHOULD_SEND_TO_SETTINGS = true;
                    SHOULD_SHOW_RATIONALE = false;
                    IS_GRANTED = false;
                    ASK_FOR_PERMISSION = false;
                } else {
                    //just request the permission
                    ASK_FOR_PERMISSION = true;
                    SHOULD_SEND_TO_SETTINGS = false;
                    SHOULD_SHOW_RATIONALE = false;
                    IS_GRANTED = false;
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(permissionRequired, true);
                editor.apply();


            } else {
                //You already have the permission, just go ahead.
                IS_GRANTED = true;
            }
            if (!IS_GRANTED) {
                Log.i("TAG", "requestPermission: IS_GRANTED " + IS_GRANTED);
                break;
            }

        }

        if (IS_GRANTED)
            Aira.onPermissionResultListener.onPermissionGranted();
        else if (ASK_FOR_PERMISSION)
            ActivityCompat.requestPermissions(appCompatActivity, Aira.permissionsRequired, Aira.permissionConstant);
        else {
            if (SHOULD_SHOW_RATIONALE)
                showRationaleMessage(context, title, message);
            else if (SHOULD_SEND_TO_SETTINGS)
                sendToSettings(context, title, message);
        }

    }

    private static void sendToSettings(Context context, @NonNull String title, @NonNull String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", Aira.context.getPackageName(), null);
                intent.setData(uri);
                appCompatActivity.startActivityForResult(intent, Aira.permissionConstant);
                Toast.makeText(Aira.context, "Go to Permissions to Grant Access", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Aira.onPermissionResultListener.onPermissionFailed();
            }
        });
        builder.show();
    }

    private static void showRationaleMessage(Context context, @NonNull String title, @NonNull String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(appCompatActivity, Aira.permissionsRequired, Aira.permissionConstant);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Aira.onPermissionResultListener.onPermissionFailed();
            }
        });
        builder.show();
    }

    public static void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Aira.permissionConstant) {
            //check if all permissions are granted
            boolean allGranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allGranted = true;
                } else {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Aira.onPermissionResultListener.onPermissionGranted();
                Log.i("TAG", "onRequestPermissionsResult: OKKK");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, Aira.permissionsRequired[0])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Need Permissions");
                builder.setMessage("This app needs multiple permissions to work properly as designed.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(appCompatActivity, Aira.permissionsRequired, Aira.permissionConstant);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Aira.onPermissionResultListener.onPermissionFailed();
                    }
                });
                builder.show();
            } else {
                Aira.onPermissionResultListener.onPermissionFailed();
            }
        }
    }

    public static void onActivityResult(int requestCode) {
        if (requestCode == permissionConstant) {
            for (String requiredPermission :
                    permissionsRequired) {
                //Got Permission
                SHOULD_SHOW_RATIONALE = ActivityCompat.checkSelfPermission(appCompatActivity, requiredPermission) == PackageManager.PERMISSION_GRANTED;
                Log.i("TAG", "onActivityResult: " + SHOULD_SHOW_RATIONALE);
                if (!SHOULD_SHOW_RATIONALE)
                    break;
            }

            if (SHOULD_SHOW_RATIONALE)
                Aira.onPermissionResultListener.onPermissionGranted();
            else Aira.onPermissionResultListener.onPermissionFailed();
        }
    }


    public interface OnPermissionResultListener {
        void onPermissionGranted();

        void onPermissionFailed();
    }
}
