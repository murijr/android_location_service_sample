package com.ingenico.locationsample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    protected TextView logTextView;

    private boolean locationEnabled;
    private boolean locationRunning;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logTextView = findViewById(R.id.tvLogs);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                showLog("Status: " + location.getProvider() + " " + location.getLatitude() + ":" + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                showLog("Status: onStatusChanged " + provider + " " + status);
            }

            public void onProviderEnabled(String provider) {
                showLog("Status: onProviderEnabled " + provider);
            }

            public void onProviderDisabled(String provider) {
                showLog("Status: onProviderDisabled " + provider);
            }
        };

        findViewById(R.id.startLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartGetLocation();
            }
        });

        findViewById(R.id.stopLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopGetLocation();
            }
        });

        refreshStatus();
        showStatus();
    }

    private Boolean hasLocationPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @SuppressLint("MissingPermission")
    private void onStartGetLocation() {

        if (!hasLocationPermission()){

            EasyPermissions.requestPermissions(this, "",
                        1, Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        if (locationRunning) return;

        refreshStatus();
        if (locationEnabled) {

            //Criteria criteria = new Criteria();
            //criteria.setPowerRequirement(Criteria.POWER_LOW);
            //criteria.setAccuracy(Criteria.ACCURACY_LOW);
            //criteria.setSpeedRequired(false);
            //criteria.setAltitudeRequired(false);
            //criteria.setBearingRequired(false);
            //criteria.setCostAllowed(false);
            //String bestProvider = locationManager.getBestProvider(criteria, true);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationRunning = true;
            showLog("Starting to get location...");
        } else {
            showLog("You location is disabled, go to settings and enable it under location menu");
        }
    }

    private void onStopGetLocation() {
        if (!locationRunning) return;

        showLog("Canceling requestLocationUpdates");
        locationManager.removeUpdates(locationListener);
        showStatus();

        locationRunning = false;
    }

    private void showLog(String log) {
        logTextView.setText(logTextView.getText().toString() + (!TextUtils.isEmpty(logTextView.getText().toString()) ? "\n\n" : "") + log);
    }

    private void refreshStatus() {
        locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showStatus() {
        if (locationEnabled) {
            showLog("Status: Location is ready");
        } else {
            showLog("Status: Location is disabled");
        }
    }
}
