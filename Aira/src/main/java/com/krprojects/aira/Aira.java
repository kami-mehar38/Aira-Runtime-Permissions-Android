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

    // boolean value to check if the user is sent to settings to enable the permissions manually
    public static boolean IS_GRANTED = false;

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

    public Aira() {

    }

   /* *//**
     * @param permissionsRequired Array of String with all the permissions required, this array can contains one or more permissions
     * @return boolean value: True if permission/permissions are granted else False
     *//*

    public static boolean checkPermission(Context context, @NonNull String[] permissionsRequired) {

        Aira.context = context;
        permissionStatus = context.getSharedPreferences("com.krprojects.aira", Context.MODE_PRIVATE);
        appCompatActivity = (Activity) context;

        Aira.permissionsRequired = permissionsRequired;
        if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionsRequired[1])) {

                return false;
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission

               return false;
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(appCompatActivity, permissionsRequired, permissionConstant);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.apply();
        } else {
            //You already have the permission, just go ahead.
            return true;
        }
        return false;
    }
*/

    /**
     * @param permissionsRequired Array of String with all the permissions required, this array can contains one or more permissions
     * @param permissionConstant  An integer value to uniquely identify the permission request from other requests
     * @param title               String value that shows the title of the dialog shown to the user when permissions are already denied by the user
     * @param message             String value that shows the message of the dialog shown to the user when permissions are already denied by the user
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
            if (ActivityCompat.checkSelfPermission(context, permissionRequired) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionRequired)) {
                    //Show Information about why you need the permission
                    IS_GRANTED = false;

                } else if (permissionStatus.getBoolean(permissionRequired, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    IS_GRANTED = false;
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
                    return;
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(appCompatActivity, permissionsRequired, permissionConstant);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(permissionRequired, true);
                editor.apply();

                Log.i("TAG", "requestPermission: OKKK");

            } else {
                //You already have the permission, just go ahead.
                IS_GRANTED = true;
            }
            //Aira.onPermissionResultListener.onPermissionFailed();
        }

        if (IS_GRANTED)
            Aira.onPermissionResultListener.onPermissionGranted();
        else showRationaleMessage(context, title, message);

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
                //txtPermissions.setText("Permissions Required");
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
                IS_GRANTED = ActivityCompat.checkSelfPermission(appCompatActivity, requiredPermission) == PackageManager.PERMISSION_GRANTED;
                Log.i("TAG", "onActivityResult: " + IS_GRANTED);
                if (!IS_GRANTED)
                    break;
            }

            if (IS_GRANTED)
                Aira.onPermissionResultListener.onPermissionGranted();
            else Aira.onPermissionResultListener.onPermissionFailed();
        }
    }

    public interface OnPermissionResultListener {
        void onPermissionGranted();

        void onPermissionFailed();
    }
}
