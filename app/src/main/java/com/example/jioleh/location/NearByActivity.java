package com.example.jioleh.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.listings.JioActivity;
import com.example.jioleh.listings.ViewJioActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NearByActivity extends AppCompatActivity
        implements
        AdapterView.OnItemSelectedListener,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final double QUERY_NEARBY_RADIUS = 1.0;
    private static final int MAP_ZOOM_STANDARD = 15;
    private static final LatLng DEFAULT_LOCATION = new LatLng(1.294792, 103.773658);
    private static final String TAG = "Location_Error";

    private boolean permissionDenied = false;

    private GoogleMap map;

    private GeoQuery geoQuery;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnInfoWindowClickListener(this);
        getDeviceLocation();
    }

    private void makeSpinner() {
        spinner = findViewById(R.id.spinner_radius);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius,R.layout.map_custom_spinner_item);

        adapter.setDropDownViewResource(R.layout.map_custom_spinner_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    //current location button animates map to user's location
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    // Checks permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            getDeviceLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }


    // Displays a dialog with error message explaining that the location permission is missing.
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        JioActivity jio = (JioActivity) marker.getTag();
        Intent nextActivity = new Intent(NearByActivity.this, ViewJioActivity.class);
        nextActivity.putExtra("activity_id", jio.getActivityId());
        nextActivity.putExtra("host_uid", jio.getHost_uid());
        startActivity(nextActivity);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            // Construct a FusedLocationProviderClient.
            FusedLocationProviderClient fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(this);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {

                            Location lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.setMyLocationEnabled(true);
                                // Set the map's camera position to the current location of the device.
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), MAP_ZOOM_STANDARD));

                                final double currentLatitude = lastKnownLocation.getLatitude();
                                final double currentLongitude = lastKnownLocation.getLongitude();
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), MAP_ZOOM_STANDARD));

                                //GeoFirestore
                                CollectionReference colRef = FirebaseFirestore.getInstance().collection("activities");
                                geoQuery = new GeoFirestore(colRef)
                                        .queryAtLocation(new GeoPoint(currentLatitude, currentLongitude), QUERY_NEARBY_RADIUS);

                                //add spinner for users to select radius of nearby query
                                makeSpinner();
                                
                                //adding info window adapter to map
                                map.setInfoWindowAdapter(new CustomInfoWindowAdapter(NearByActivity.this));
                                geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                                    @Override
                                    public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                                        JioActivity jio = documentSnapshot.toObject(JioActivity.class);
                                        if (jio.isCancelled() || jio.isConfirmed() || jio.isExpired()) {
                                            //do nth
                                        } else {
                                            LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                            MarkerOptions options = new MarkerOptions()
                                                    .position(latLng);

                                            map.addMarker(options)
                                                    .setTag(jio);
                                        }
                                    }

                                    @Override
                                    public void onDocumentExited(DocumentSnapshot documentSnapshot) {

                                    }

                                    @Override
                                    public void onDocumentMoved(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                                    }

                                    @Override
                                    public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                                    }

                                    @Override
                                    public void onGeoQueryReady() {

                                    }

                                    @Override
                                    public void onGeoQueryError(Exception e) {

                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(DEFAULT_LOCATION, MAP_ZOOM_STANDARD));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            } else {
                // Permission to access the location is missing. Show rationale and request permission
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);

            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0: geoQuery.setRadius(1.0);
                    break;
            case 1: geoQuery.setRadius(2.0);
                    break;
            case 2: geoQuery.setRadius(5.0);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

