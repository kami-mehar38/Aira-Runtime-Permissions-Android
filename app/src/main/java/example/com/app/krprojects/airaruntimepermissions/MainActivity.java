package example.com.app.krprojects.airaruntimepermissions;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.krprojects.aira.Aira;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {

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
                checkForStoragePermission();
                break;
            }
            case R.id.btnContacts: {
                checkForContactsPermission();
                break;
            }
            case R.id.btnReqAll: {
                checkMultiplePermissions();
                break;
            }
        }
    }

    private void checkMultiplePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int PERMISSION_CONSTANT_ALL = 1;
            if (!Aira.checkPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}))
                Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                        PERMISSION_CONSTANT_ALL, "Need Permissions", "This app needs multiple permissions to work properly, grant the permission if you want to get all the features.",
                        new Aira.OnPermissionResultListener() {
                            @Override
                            public void onPermissionGranted(List<String> grantedPermissions) {
                                showToast("All permissions granted");
                            }

                            @Override
                            public void onPermissionFailed(List<String> failedPermissions) {
                                String message = null;
                                for (String failedPermission :
                                        failedPermissions) {
                                    message = failedPermission;
                                    message = message + " and ";
                                }
                                message = message + " failed";
                                showToast(message);
                            }
                        });

        }
    }

    private void checkForCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int PERMISSION_CONSTANT_CAMERA = 2;
            Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CONSTANT_CAMERA, "Camera Permission", "This app needs camera permission to work properly, grant the permission if you want to get all the features.",
                    new Aira.OnPermissionResultListener() {
                        @Override
                        public void onPermissionGranted(List<String> grantedPermissions) {
                            showToast(grantedPermissions.get(0) + " Granted");
                        }

                        @Override
                        public void onPermissionFailed(List<String> failedPermissions) {
                            showToast(failedPermissions.get(0) + " Failed");
                        }
                    });

        }
    }

    private void checkForStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int PERMISSION_CONSTANT_STORAGE = 3;
            Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_CONSTANT_STORAGE, "Storage Permission", "This app needs storage permission to work properly, grant the permission if you want to get all the features.",
                    new Aira.OnPermissionResultListener() {
                        @Override
                        public void onPermissionGranted(List<String> grantedPermissions) {
                            showToast(grantedPermissions.get(0) + " Granted");
                        }

                        @Override
                        public void onPermissionFailed(List<String> failedPermissions) {
                            showToast(failedPermissions.get(0) + " Failed");
                        }
                    });

        }
    }

    private void checkForContactsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int PERMISSION_CONSTANT_CONTACTS = 4;
            Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_CONSTANT_CONTACTS, "Contacts Permission", "This app needs contacts permission to work properly, grant the permission if you want to get all the features.",
                    new Aira.OnPermissionResultListener() {
                        @Override
                        public void onPermissionGranted(List<String> grantedPermissions) {
                            showToast(grantedPermissions.get(0) + " Granted");
                        }

                        @Override
                        public void onPermissionFailed(List<String> failedPermissions) {
                            showToast(failedPermissions.get(0) + " Failed");
                        }
                    });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Aira.onActivityResult(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Aira.onRequestPermissionResult(requestCode, permissions);
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
