package com.gohachi.tugcair;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gohachi.tugcair.signature.DrawSignatureActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPersonActivity extends AppCompatActivity {

    private static String location;
    private FusedLocationProviderClient mFusedLocationClient;

    private FirebaseFirestore mDatabase;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private android.widget.Button mBtnSubmit;
    private EditText mFullname, mNoKtp, mAddressKtp, mAddressNow, mContactPhone;
    private StringBuilder stringBuilder;

    private FirebaseAuth mAuth;

    private boolean isGPS = false;
    private boolean isContinue = false;

    final String TAG = "AddPersonActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_add_person);

        this.mFullname = (EditText) findViewById(R.id.txtFullnamez);
        this.mNoKtp = (EditText) findViewById(R.id.txtNoCard);
        this.mAddressKtp = (EditText) findViewById(R.id.txtAddressCard);
        this.mContactPhone = (EditText) findViewById(R.id.txtContactPerson);
        this.mAddressNow = (EditText) findViewById(R.id.txtAddressNow);
        this.mBtnSubmit = (Button) findViewById(R.id.btnSubmit);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FirestoreInit();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            AddPersonActivity.location =  String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude);
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = mFullname.getText().toString();
                String noktp = mNoKtp.getText().toString();
                String alamat = mAddressKtp.getText().toString();
                String alamatsekarang = mAddressNow.getText().toString();
                String nohp = mContactPhone.getText().toString();

                new GpsUtils(AddPersonActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        // turn on GPS
                        isGPS = isGPSEnable;
                    }
                });

                if(isGPS == false){
                    Toast.makeText(AddPersonActivity.this, "Please turn on GPS to use this app!", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    redirectPage(AddPersonActivity.this, MainActivity.class);
                    finish();
                }


                if(fullname.isEmpty() || noktp.isEmpty() || alamat.isEmpty() || nohp.isEmpty()){
                    Snackbar.make(v, "Data tidak boleh kosong!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }else{
                    isContinue = false;
                    getLocation();
                }
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(AddPersonActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(AddPersonActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddPersonActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {

                String fullname = mFullname.getText().toString();
                String noktp = mNoKtp.getText().toString();
                String alamat = mAddressKtp.getText().toString();
                String alamatsekarang = mAddressNow.getText().toString();
                String nohp = mContactPhone.getText().toString();

                mFusedLocationClient.getLastLocation().addOnSuccessListener(AddPersonActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

//                        String coorLocation; String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude);
                        String fullname = mFullname.getText().toString();
                        String noktp = mNoKtp.getText().toString();
                        String alamat = mAddressKtp.getText().toString();
                        String alamatsekarang = mAddressNow.getText().toString();
                        String nohp = mContactPhone.getText().toString();

                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            AddPersonActivity.location = String.format(Locale.US, "%s,%s", wayLatitude, wayLongitude);
//                            txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));

                            if (AddPersonActivity.location != null){
                                Intent intent = new Intent(AddPersonActivity.this, DrawSignatureActivity.class);
                                intent.putExtra("Fullname", fullname);
                                intent.putExtra("No_card", noktp);
                                intent.putExtra("Address", alamat);
                                intent.putExtra("Address_now", alamatsekarang);
                                intent.putExtra("No_Phone", nohp);
                                intent.putExtra("Location_Coord", AddPersonActivity.location);
                                startActivity(intent);
                            }else{
                                Toast.makeText(AddPersonActivity.this, "Data tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    }
                });
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(AddPersonActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                    if (location != null) {
                                        wayLatitude = location.getLatitude();
                                        wayLongitude = location.getLongitude();
                                        AddPersonActivity.location = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude);
                                    } else {
                                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                                    }
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    private void FirestoreInit(){
        mDatabase = FirebaseFirestore.getInstance();
    }

    private void redirectPage(Activity activity, Class goTo) {
        Intent intent = new Intent(activity, goTo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            redirectPage(AddPersonActivity.this, DashboardActitvity.class);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}