package com.progressifff.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity_layout);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(!this.checkRequestRationale()) {
                requestPermissions(this, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else if(getSupportFragmentManager().getFragments().isEmpty()) {
            setupContentFragment(new WeatherFragment());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            setupContentFragment(new WeatherFragment());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length != 0 && grantResults[0] == 0) {
                setupContentFragment(new WeatherFragment());
            } else if (checkRequestRationale()) {
                setupContentFragment(new PermissionsInstructionFragment());
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkRequestRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            setupContentFragment(new PermissionsRationaleFragment());
            return true;
        }
        return false;
    }

    public static void requestPermissions(Activity activity, int permissionRequestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{(Manifest.permission.ACCESS_FINE_LOCATION)},
                permissionRequestCode);
    }

    private void setupContentFragment(@NonNull Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentContainer, fragment);
        fragmentTransaction.commit();
    }
}
