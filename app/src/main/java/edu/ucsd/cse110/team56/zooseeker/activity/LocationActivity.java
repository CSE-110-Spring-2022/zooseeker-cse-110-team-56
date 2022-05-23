package edu.ucsd.cse110.team56.zooseeker.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.ucsd.cse110.team56.zooseeker.R;
import edu.ucsd.cse110.team56.zooseeker.databinding.ActivityLocationBinding;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ActivityLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Add a marker in Sydney and move the camera
        LatLng ucsd = new LatLng(32.8801, -117.2340);
        this.map.addMarker(new MarkerOptions().position(ucsd).title("UCSD Position"));
        this.map.moveCamera(CameraUpdateFactory
                .newLatLng(ucsd));
        map.moveCamera(CameraUpdateFactory.zoomTo(11.5f));


        /* Listen for Location Updates */
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("CurrLocation", String.format("Location changed: %s", location));

                    var marker = new MarkerOptions()
                            .position(new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            ))
                            .title("Navigation Step");
                    map.addMarker(marker);
                }
            };
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }

    }



}