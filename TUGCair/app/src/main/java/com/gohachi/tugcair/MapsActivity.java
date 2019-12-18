package com.gohachi.tugcair;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        try {
            String Latcode = getIntent().getStringExtra("Lat_Code");
            String Longcode = getIntent().getStringExtra("Long_Code");
            String AlamatSekarang = getIntent().getStringExtra("Address");

            // Add a marker in Sydney and move the camera
            LatLng lokasi = new LatLng(Double.parseDouble(Latcode),Double.parseDouble(Longcode));
//        mMap.setMinZoomPreference(6.0f);
//        mMap.setMaxZoomPreference(14.0f);



            float zoomLevel = 18.0f; //This goes up to 21
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
            mMap.addMarker(new MarkerOptions().position(lokasi).title(AlamatSekarang));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasi));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, zoomLevel));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
