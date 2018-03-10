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
    private SharedPreferences permissionStatus;

    // boolean value to check if the user is sent to settings to enable the permissions manually
    public static boolean sentToSettings = false;

    // instance of the context
    private Context context;

    // instance of the Activity
    private Activity appCompatActivity;

    // Listener that responds to the calling activity or fragment
    private OnPermissionResultListener onPermissionResultListener;

    // String array of the required permissions
    private String[] permissionsRequired;

    // int  value to uniquely identify the result in onActivityResult() method
    private int permissionConstant;

    public Aira(Context context) {
        this.context = context;
        permissionStatus = context.getSharedPreferences("com.krprojects.aira", Context.MODE_PRIVATE);
        appCompatActivity = (Activity) context;
        onPermissionResultListener = (OnPermissionResultListener) context;
    }

    /**
     * @param permissionsRequired Array of String with all the permissions required, this array can contains one or more permissions
     * @param permissionConstant  An integer value to uniquely identify the permission request from other requests
     * @param title               String value that shows the title of the dialog shown to the user when permissions are already denied by the user
     * @param message             String value that shows the message of the dialog shown to the user when permissions are already denied by the user
     * @return boolean value: True if permission/permissions are granted else False
     */

    public boolean checkPermission(final String[] permissionsRequired, final int permissionConstant, String title, String message) {
        this.permissionsRequired = permissionsRequired;
        this.permissionConstant = permissionConstant;
        if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionsRequired[1])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(appCompatActivity, permissionsRequired, permissionConstant);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        appCompatActivity.startActivityForResult(intent, permissionConstant);
                        Toast.makeText(context, "Go to Permissions to Grant Access", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
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

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionConstant) {
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
                onPermissionResultListener.onPermissionGranted();
                Log.i("TAG", "onRequestPermissionsResult: OKKK");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, permissionsRequired[1])) {
                //txtPermissions.setText("Permissions Required");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Need Permissions");
                builder.setMessage("This app needs multiple permissions to work properly as designed.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(appCompatActivity, permissionsRequired, permissionConstant);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                onPermissionResultListener.onPermissionFailed();
            }
        }
    }

    public void onActivityResult(int requestCode) {
        if (requestCode == permissionConstant) {
            if (ActivityCompat.checkSelfPermission(appCompatActivity, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                onPermissionResultListener.onPermissionGranted();
            }
        }
    }

    public interface OnPermissionResultListener {
        void onPermissionGranted();

        void onPermissionFailed();
    }
}
