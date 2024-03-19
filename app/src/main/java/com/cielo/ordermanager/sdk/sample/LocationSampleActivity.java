    package com.cielo.ordermanager.sdk.sample;
    import android.Manifest;
    import android.app.Activity;
    import android.content.Context;
    import android.content.pm.PackageManager;
    import android.location.Criteria;
    import android.location.Location;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.os.Bundle;
    import android.util.Log;
    import com.cielo.ordermanager.sdk.R;
    import com.mapbox.mapboxsdk.annotations.MarkerOptions;
    import com.mapbox.mapboxsdk.camera.CameraPosition;
    import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
    import com.mapbox.mapboxsdk.geometry.LatLng;
    import com.mapbox.mapboxsdk.maps.MapView;
    import com.mapbox.mapboxsdk.maps.MapboxMap;

    import org.jetbrains.annotations.Nullable;

    public class LocationSampleActivity extends Activity implements LocationListener {
        private static final String TAG = "LocationSampleActivity";
        private LocationManager locationManager = null;
        private MapView mapView;
        MapboxMap mapboxMap;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_location);
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(mapboxMap -> {
                LocationSampleActivity.this.mapboxMap = mapboxMap;
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location1 != null) {
                    Log.d(TAG, location1.toString());
                    LocationSampleActivity.this.onLocationChanged(location1);
                }
            });
        }
        @Override
        protected void onStart() {
            super.onStart();
            mapView.onStart();
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();
            mapView.onDestroy();
        }
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged with location " + location.toString());
            String text = String.format("Lat:\t %f\nLong:\t %f\nAlt:\t %f\nBearing:\t %f", location.getLatitude(),
                    location.getLongitude(), location.getAltitude(), location.getBearing());
            if (mapboxMap != null) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude())) // Sets the new camera position
                        .zoom(17) // Sets the zoom to level 10
                        .tilt(20) // Set the camera tilt to 20 degrees
                        .build();
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("LIO"));
                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 4000);
            }
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