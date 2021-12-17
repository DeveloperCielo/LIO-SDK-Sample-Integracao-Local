package com.cielo.ordermanager.sdk.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.util.PermissionUtils;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;


public class LocationSampleActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = "LocationSampleActivity";

    private LocationManager locationManager;

    private MapView mapView;
    private MapboxMap mapboxMap;

    private final String[] locationsPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 99;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        PermissionUtils.checkPermission(this, locationsPermissions, new PermissionUtils.CheckPermissionListener() {
            @Override
            public void onNeedRequestPermissions(String[] permissionsName) {
                ActivityCompat.requestPermissions(LocationSampleActivity.this, permissionsName,
                        REQUEST_LOCATION_PERMISSION_CODE);
            }

            @Override
            public void onAllPermissionsGranted() {
                requestLocationUpdates();
            }
        });
    }

    private String getBestLocationProvider() {
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        return locationManager.getBestProvider(criteria, true);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        LocationSampleActivity.this.mapboxMap = mapboxMap;

                        if (PermissionUtils.hasNotGrantedPermissions(LocationSampleActivity.this, locationsPermissions))
                            return;

                        locationManager.requestLocationUpdates(getBestLocationProvider(),
                                10000L, 5, LocationSampleActivity.this);

                        final Location lastKnownLocation = locationManager.getLastKnownLocation(getBestLocationProvider());
                        if (lastKnownLocation != null)
                            addPositionOnMap(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (permissions.length > 0) {
                for (int index = 0; index < permissions.length; ++index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        shouldShowRequestPermissionRationale(permissions[index]);
                        return;
                    }
                }
                requestLocationUpdates();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null)
            mapView.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
        locationManager.removeUpdates(LocationSampleActivity.this);
    }

    @Override
    public void onLocationChanged(Location location) { //<9>
        Log.d(TAG, "onLocationChanged with location " + location.toString());
        addPositionOnMap(location.getLatitude(), location.getLongitude());
    }

    private void addPositionOnMap(double latitude, double longitude) {
        if (mapboxMap == null)
            return;

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)) // Sets the new camera position
                .zoom(17) // Sets the zoom to level 10
                .tilt(20) // Set the camera tilt to 20 degrees
                .build();

        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("LIO"));

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 4000);


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
