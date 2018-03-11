package example.com.app.krprojects.airaruntimepermissions;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.krprojects.aira.Aira;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout view;
    private int PERMISSION_CONSTANT_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        view = findViewById(R.id.rlContainer);

        Button btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(this);
        Button btnStorage = findViewById(R.id.btnStorage);
        btnStorage.setOnClickListener(this);
        Button btnContacts = findViewById(R.id.btnContacts);
        btnContacts.setOnClickListener(this);
        Button btnReqAll = findViewById(R.id.btnReqAll);
        btnReqAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCamera: {
                checkForCameraPermission();
                break;
            }
            case R.id.btnStorage: {
                break;
            }
            case R.id.btnContacts: {
                break;
            }
            case R.id.btnReqAll: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                            PERMISSION_CONSTANT_CAMERA, "Need Permissions", "This app needs multiple permissions to work properly, grant the permission if you want to get all the features.",
                            new Aira.OnPermissionResultListener() {
                                @Override
                                public void onPermissionGranted() {
                                    showSnack(true);
                                }

                                @Override
                                public void onPermissionFailed() {
                                    showSnack(false);
                                }
                            });

                }
                break;
            }
        }
    }

    private void checkForCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CONSTANT_CAMERA, "Camera Permission", "This app needs camera permission to work properly, grant the permission if you want to get all the features.",
                    new Aira.OnPermissionResultListener() {
                        @Override
                        public void onPermissionGranted() {
                            showSnack(true);
                        }

                        @Override
                        public void onPermissionFailed() {
                            showSnack(false);
                        }
                    });

        }
    }

    // Showing the status in Snackbar
    public void showSnack(boolean isGranted) {
        String message;
        int color;
        if (isGranted) {
            message = "Permission is granted";
            color = Color.WHITE;
        } else {
            message = "Permission is not granted";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Aira.onActivityResult(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Aira.onRequestPermissionResult(requestCode, permissions, grantResults);
    }


}
