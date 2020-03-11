package com.cielo.ordermanager.sdk.sample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.TAG
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback

class LocationSampleActivity : AppCompatActivity(), LocationListener {

    private var locationManager: LocationManager? = null
    private lateinit var mapView: MapView
    private var mapboxMap: MapboxMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(OnMapReadyCallback { mapboxMap: MapboxMap? ->
            this@LocationSampleActivity.mapboxMap = mapboxMap
            val location1 = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location1 != null) {
                Log.d(TAG, location1.toString())
                onLocationChanged(location1)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLocationChanged(location: Location) { //<9>
        Log.d(TAG, "onLocationChanged with location $location")
        if (mapboxMap != null) {
            val position = CameraPosition.Builder()
                    .target(LatLng(location.latitude, location.longitude)) // Sets the new camera position
                    .zoom(17.0) // Sets the zoom to level 10
                    .tilt(20.0) // Set the camera tilt to 20 degrees
                    .build()
            mapboxMap?.addMarker(MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .title("LIO"))
            mapboxMap?.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 4000)
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}
}
