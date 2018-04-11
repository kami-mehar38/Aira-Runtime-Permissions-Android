package com.krprojects.aira;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

/**
 * This project is created by Kamran Ramzan on 11-Mar-18.
 */

@SuppressLint("StaticFieldLeak")

public class Aira {

    // instance of the context
    private static Context context;

    // instance of the Activity
    private static Activity appCompatActivity;

    // Listener that responds to the calling activity or fragment
    private static OnPermissionResultListener onPermissionResultListener;

    // String array of the required permissions
    private static String[] permissionsRequired;

    private static List<String> grantedPermissionsList = new ArrayList<>();
    private static List<String> failedPermissionsList = new ArrayList<>();

    // int  value to uniquely identify the result in onActivityResult() method
    private static int permissionConstant;
    private static boolean SHOULD_SEND_TO_SETTINGS;
    private static boolean SHOULD_SHOW_RATIONALE;
    private static boolean ASK_FOR_PERMISSION = false;
    private static boolean IS_GRANTED;


    public Aira() {

    }

    /**
     * @param context             Context of the activity
     * @param permissionsRequired Array of String with all the permissions required, this array can contains one or more permissions
     * @return boolean value, true if permission(s) is/are granted else false
     */
    public static boolean checkPermission(Context context, @NonNull String[] permissionsRequired) {

        Aira.context = context;
        SharedPreferences permissionStatus = context.getSharedPreferences("com.krprojects.aira", Context.MODE_PRIVATE);
        appCompatActivity = (Activity) context;

        Aira.permissionsRequired = permissionsRequired;


        for (String permissionRequired :
                Aira.permissionsRequired) {

            IS_GRANTED = ActivityCompat.checkSelfPermission(context, permissionRequired) == PackageManager.PERMISSION_GRANTED;

        }
        return IS_GRANTED;
    }

    /**
     * @param context                    Context of the activity
     * @param permissionsRequired        Array of String with all the permissions required, this array can contains one or more permissions
     * @param permissionConstant         An integer value to uniquely identify the permission request from other requests
     * @param title                      String value that shows the title of the dialog shown to the user when permissions are already denied by the user
     * @param message                    String value that shows the message of the dialog shown to the user when permissions are already denied by the user
     * @param onPermissionResultListener Listener for the permissions callback
     */

    public static void requestPermission(Context context, @NonNull String[] permissionsRequired, int permissionConstant, @NonNull String title, @NonNull String message, @NonNull OnPermissionResultListener onPermissionResultListener) {

        Aira.context = context;
        SharedPreferences permissionStatus = context.getSharedPreferences("com.krprojects.aira", Context.MODE_PRIVATE);
        appCompatActivity = (Activity) context;
        Aira.onPermissionResultListener = onPermissionResultListener;

        Aira.permissionsRequired = permissionsRequired;
        Aira.permissionConstant = permissionConstant;

        grantedPermissionsList.clear();
        failedPermissionsList.clear();


        for (String permissionRequired :
                Aira.permissionsRequired) {

            if (ActivityCompat.checkSelfPermission(context, permissionRequired) != PackageManager.PERMISSION_GRANTED) {

                SHOULD_SEND_TO_SETTINGS = false;
                SHOULD_SHOW_RATIONALE = false;
                ASK_FOR_PERMISSION = false;

                failedPermissionsList.add(permissionRequired);

                if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionRequired)) {
                    //Show Information about why you need the permission
                    SHOULD_SHOW_RATIONALE = true;
                    SHOULD_SEND_TO_SETTINGS = false;
                    ASK_FOR_PERMISSION = false;

                } else if (permissionStatus.getBoolean(permissionRequired, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    SHOULD_SEND_TO_SETTINGS = true;
                    SHOULD_SHOW_RATIONALE = false;
                    ASK_FOR_PERMISSION = false;

                } else {
                    //just request the permission
                    ASK_FOR_PERMISSION = true;
                    SHOULD_SEND_TO_SETTINGS = false;
                    SHOULD_SHOW_RATIONALE = false;

                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(permissionRequired, true);
                editor.apply();


            } else {
                //You already have the permission, just go ahead.
                grantedPermissionsList.add(permissionRequired);
            }

        }
        if (failedPermissionsList.size() > 0) {
            if (ASK_FOR_PERMISSION) {
                ActivityCompat.requestPermissions(appCompatActivity, Aira.permissionsRequired, Aira.permissionConstant);
            } else {
                if (SHOULD_SHOW_RATIONALE) {
                    showRationaleMessage(context, title, message);
                } else if (SHOULD_SEND_TO_SETTINGS) {
                    sendToSettings(context, title, message);
                }
            }
        } else Aira.onPermissionResultListener.onPermissionGranted(grantedPermissionsList);

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
                Aira.onPermissionResultListener.onPermissionFailed(failedPermissionsList);
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
                Aira.onPermissionResultListener.onPermissionFailed(failedPermissionsList);
            }
        });
        builder.show();
    }

    public static void onRequestPermissionResult(int requestCode, @NonNull String[] permissions) {
        if (requestCode == Aira.permissionConstant) {

            grantedPermissionsList.clear();
            failedPermissionsList.clear();
            //check if all permissions are granted
            for (String grantPermission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, grantPermission) == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissionsList.add(grantPermission);
                } else {
                    failedPermissionsList.add(grantPermission);
                }
            }

            if (failedPermissionsList.size() > 0)
                Aira.onPermissionResultListener.onPermissionFailed(failedPermissionsList);
            else Aira.onPermissionResultListener.onPermissionGranted(grantedPermissionsList);
        }
    }

    public static void onActivityResult(int requestCode) {
        grantedPermissionsList.clear();
        failedPermissionsList.clear();
        if (requestCode == permissionConstant) {
            for (String requiredPermission :
                    permissionsRequired) {
                if (ActivityCompat.checkSelfPermission(context, requiredPermission) == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissionsList.add(requiredPermission);
                } else {
                    failedPermissionsList.add(requiredPermission);
                }
            }

            if (failedPermissionsList.size() > 0)
                Aira.onPermissionResultListener.onPermissionFailed(failedPermissionsList);
            else Aira.onPermissionResultListener.onPermissionGranted(grantedPermissionsList);
        }
    }


    public interface OnPermissionResultListener {
        void onPermissionGranted(List<String> grantedPermissions);

        void onPermissionFailed(List<String> failedPermissions);
    }
}
